package com.icaro.simba_dj.service.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final Path path = Paths.get("uploads");

    public String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Arquivo de imagem inválido ou ausente.");
        }
        if(!Files.exists(path)){
            Files.createDirectories(path);
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Files.copy(file.getInputStream(), path.resolve(fileName));

        return "/uploads/" + fileName;
    }
}
