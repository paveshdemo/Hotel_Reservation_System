package com.hotelreservationsystem.hotelreservationsystem.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service to handle file storage operations for payment proofs
 */
@Service
public class FileStorageService {
    
    @Value("${payment.proof.upload-dir:uploads/payment-proofs}")
    private String uploadDir;
    
    // Allowed file types
    private static final String[] ALLOWED_FILE_TYPES = {
        "image/jpeg", "image/jpg", "image/png", "image/gif", 
        "application/pdf"
    };
    
    // Maximum file size in bytes (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    
    /**
     * Get the absolute upload directory path
     */
    private Path getUploadPath() throws IOException {
        Path uploadPath;
        
        // If uploadDir is absolute, use it as is
        File uploadFile = new File(uploadDir);
        if (uploadFile.isAbsolute()) {
            uploadPath = Paths.get(uploadDir);
        } else {
            // Otherwise, resolve relative to user home directory
            uploadPath = Paths.get(System.getProperty("user.home"))
                .resolve("Hotel_Reservation_System")
                .resolve(uploadDir);
        }
        
        // Create directory if it doesn't exist
        System.out.println("FileStorageService: Upload path: " + uploadPath.toAbsolutePath());
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                System.out.println("FileStorageService: Created upload directory: " + uploadPath.toAbsolutePath());
            } catch (IOException e) {
                System.err.println("FileStorageService: Failed to create directory: " + uploadPath.toAbsolutePath());
                System.err.println("FileStorageService: Error: " + e.getMessage());
                throw new IOException("Failed to create upload directory: " + uploadPath.toAbsolutePath(), e);
            }
        }
        
        return uploadPath;
    }
    
    /**
     * Save an uploaded file and return the stored file path
     */
    public String saveFile(MultipartFile file) throws IOException, IllegalArgumentException {
        // Validate file
        validateFile(file);
        
        // Get upload directory path
        Path uploadPath = getUploadPath();
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID() + "_" + originalFilename;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        try {
            Files.write(filePath, file.getBytes());
            System.out.println("FileStorageService: File saved successfully at " + filePath.toAbsolutePath());
            return filePath.toString();
        } catch (IOException e) {
            System.err.println("FileStorageService: Failed to save file: " + filePath.toAbsolutePath());
            System.err.println("FileStorageService: Error: " + e.getMessage());
            throw new IOException("Failed to save file: " + filePath.toAbsolutePath(), e);
        }
    }
    
    /**
     * Retrieve a file by its path
     */
    public byte[] getFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filePath);
        }
        
        return Files.readAllBytes(path);
    }
    
    /**
     * Delete a file by its path
     */
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        
        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("FileStorageService: File deleted successfully at " + filePath);
        }
    }
    
    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5MB");
        }
        
        // Check file type
        String contentType = file.getContentType();
        boolean isAllowedType = false;
        
        for (String allowedType : ALLOWED_FILE_TYPES) {
            if (allowedType.equals(contentType)) {
                isAllowedType = true;
                break;
            }
        }
        
        if (!isAllowedType) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: PNG, JPG, JPEG, GIF, PDF");
        }
    }
    
    /**
     * Check if file exists
     */
    public boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }
    
    /**
     * Get file size
     */
    public long getFileSize(MultipartFile file) {
        return file.getSize();
    }
    
    /**
     * Get file MIME type
     */
    public String getFileType(MultipartFile file) {
        return file.getContentType();
    }
}
