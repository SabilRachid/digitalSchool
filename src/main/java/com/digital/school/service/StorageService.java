package com.digital.school.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();
    String store(MultipartFile file, String filename);
    Stream<Path> loadAll();
    Path load(String filename);
    Resource loadAsResource(String filename);
    void delete(String filepath);
    void deleteAll();
	String getUrl(String path);
	void store(String path, byte[] content);
}