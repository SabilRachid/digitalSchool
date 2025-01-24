package com.digital.school.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import com.digital.school.service.StorageService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

//@Service
public class FileSystemStorageService implements StorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    @Override
    public void init() {
        rootLocation = Paths.get(uploadDir);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    @Override
    public String store(MultipartFile file, String filename) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Failed to store empty file.");
            }
            
            Path destinationFile = this.rootLocation.resolve(
                Paths.get(filename))
                .normalize()
                .toAbsolutePath();
                
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                    StandardCopyOption.REPLACE_EXISTING);
            }
            
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                .filter(path -> !path.equals(this.rootLocation))
                .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void delete(String filepath) {
        try {
            Path file = load(filepath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

	@Override
	public String getUrl(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store(String path, byte[] content) {
		// TODO Auto-generated method stub
		
	}
}
