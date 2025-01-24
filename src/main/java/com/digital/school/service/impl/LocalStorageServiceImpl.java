package com.digital.school.service.impl;

import com.digital.school.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class LocalStorageServiceImpl implements StorageService {

    private final Path rootLocation;

    public LocalStorageServiceImpl(@Value("${app.storage.location}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public void store(String path, byte[] content) {
        try {
            Path destinationFile = this.rootLocation.resolve(Paths.get(path)).normalize().toAbsolutePath();
            
            if (!destinationFile.getParent().startsWith(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("Cannot store file outside current directory.");
            }
            
            Files.createDirectories(destinationFile.getParent());
            Files.write(destinationFile, content);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource loadAsResource(String path) {
        try {
            Path file = rootLocation.resolve(path);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not read file: " + path, e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            Path file = rootLocation.resolve(path);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + path, e);
        }
    }

    @Override
    public String getUrl(String path) {
        return "/storage/" + path;
    }

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String store(MultipartFile file, String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<Path> loadAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Path load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}
}
