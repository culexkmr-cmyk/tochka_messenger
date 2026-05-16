package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Media;
import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.MediaRepository;
import com.tochka.tochka_messenger.DB.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Value("${app.upload.dir:uploads/avatars}")
    private String uploadDir;

    /**
     * Обновление nickname пользователя
     */
    public User updateNickname(User user, String newNickname) {
        if (newNickname == null || newNickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be empty");
        }
        user.setNickname(newNickname.trim());
        return userRepository.save(user);   
    }
    public User updateProfilePicture(User user, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("ProfilePicture file is empty");
        }

        // Создаём директорию, если её нет
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // Сохраняем файл на диск
        file.transferTo(filePath.toFile());

        // Создаём Media-запись
        Media avatar = new Media();
        avatar.setUser(user);
        // Для аватарки chat = null, так как это не медиа чата
        avatar.setChat(null);
        avatar.setFileUrl("/uploads/profilePicture/" + filename);
        avatar.setFileType(file.getContentType());
        avatar.setFileName(originalFilename);
        avatar.setFileSize(file.getSize());

        Media savedProfilePicture = mediaRepository.save(avatar);

        // Устанавливаем новую аватарку пользователю (старая удалится сама)
        user.setProfile_picture(savedProfilePicture);
        return userRepository.save(user);
    }

    /**
     * Получение информации о профиле
     */
    public UserProfileDto getProfile(User user) {
        String avatarUrl = user.getProfile_picture() != null
                ? user.getProfile_picture().getFileUrl()
                : null;
        return new UserProfileDto(
                user.getUsername(),
                user.getNickname(),
                avatarUrl
        );
    }
}