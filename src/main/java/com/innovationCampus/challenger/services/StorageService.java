package com.innovationCampus.challenger.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class StorageService {

    public String uploadFile(MultipartFile file) {
        // В реальном приложении здесь была бы логика загрузки файла в S3 или другое хранилище.
        // Мы вернем фиктивный URL.
        return "https://storage.example.com/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
    }
}
