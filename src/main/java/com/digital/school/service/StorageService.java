package com.digital.school.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {
    void init();
    Stream<Path> loadAll();
    Path load(String filename);
    Resource loadAsResource(String filename);
    void delete(String filepath);
    void deleteAll();
	String getUrl(String path);
	String storeFile(MultipartFile file) throws IOException;
    String store(MultipartFile file, String s);
    String storeFile(byte[] fileData, String fileName);

}