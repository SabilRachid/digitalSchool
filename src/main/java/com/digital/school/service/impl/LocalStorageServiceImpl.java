package com.digital.school.service.impl;

import com.digital.school.dto.ResourceDto;
import com.digital.school.service.CourseService;
import com.digital.school.service.ResourceService;
import com.digital.school.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class LocalStorageServiceImpl implements StorageService {


    private Path rootLocation;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private CourseService courseService;

    @Value("${app.storage.resources}")
    private String resourceStoragePath;


    public LocalStorageServiceImpl(@Value("${app.storage.resources}") String storageLocation, ResourceService resourceService) {
        this.rootLocation = Paths.get(storageLocation);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
        this.resourceService = resourceService;
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

	public String storeFile(MultipartFile file) throws IOException {
        Path storagePath = Paths.get(resourceStoragePath).toAbsolutePath().normalize();
        Files.createDirectories(storagePath);

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = storagePath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/resources/" + fileName; // URL pour accéder au fichier
    }

    @Override
    public String store(MultipartFile file, String s) {
        return "";
    }

    //implement cette methode
    public ResourceDto saveResource(ResourceDto dto) {
        com.digital.school.model.Resource resource = ResourceDto.toEntity(dto);
        resource.setCourse(courseService.findById(dto.getCourseId())); // Associer un cours
        com.digital.school.model.Resource saved = resourceService.addResource(resource);
        return ResourceDto.fromEntity(saved);
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
