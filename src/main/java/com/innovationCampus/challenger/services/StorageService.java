package com.innovationCampus.challenger.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class StorageService {

    public String uploadFile(MultipartFile file) {
        // Пока что заглушка
        return "https://storage.example.com/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
    }
}
