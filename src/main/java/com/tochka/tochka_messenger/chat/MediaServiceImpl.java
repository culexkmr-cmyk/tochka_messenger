package com.tochka.tochka_messenger.chat;

import com.tochka.tochka_messenger.DB.entities.Chat;
import com.tochka.tochka_messenger.DB.entities.Media;
import com.tochka.tochka_messenger.DB.entities.User;
import com.tochka.tochka_messenger.DB.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaServiceImpl implements GetMedia, SaveMedia {

    @Value("${app.upload.media.dir:uploads/chat-media}")
    private String uploadDir;

    @Autowired
    private MediaRepository mediaRepository;

    @Override
    @Transactional(readOnly = true)
    public Media getMedia(Long mediaId, User requester) {
        Media media = mediaRepository.findById(mediaId).orElse(null);
        if (media == null) {
            return null;
        }
        Chat chat = media.getChat();
        if (chat == null) {
            return null;
        }
        if (!chat.getUsers().contains(requester)) {
            return null;
        }
        return media;
    }

    @Override
    @Transactional
    public Media saveMedia(MultipartFile file, Chat chat, User uploader) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        file.transferTo(filePath.toFile());


        Media media = new Media();
        media.setUser(uploader);
        media.setChat(chat);
        media.setFileUrl("/uploads/chat-media/" + filename);
        media.setFileType(file.getContentType());
        media.setFileName(originalFilename);
        media.setFileSize(file.getSize());

        return mediaRepository.save(media);
    }
}