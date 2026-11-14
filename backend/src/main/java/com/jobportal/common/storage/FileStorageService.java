package com.jobportal.common.storage;

import com.jobportal.common.exception.ApiException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Saves uploaded resumes on local disk. (Swap for S3 or similar when deploying for real.) */
@Service
public class FileStorageService {
    private static final Set<String> ALLOWED = Set.of("pdf", "doc", "docx");
    private final Path root;

    public FileStorageService(@Value("${app.upload-dir}") String dir) throws IOException {
        this.root = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    /** Stores the file and returns the generated stored name. */
    public String saveResume(MultipartFile file) {
        if (file == null || file.isEmpty()) throw ApiException.badRequest("Please choose a file");
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT) : "";
        if (!ALLOWED.contains(ext)) throw ApiException.badRequest("Only PDF, DOC or DOCX files are allowed");
        String stored = UUID.randomUUID() + "." + ext;
        try {
            Files.copy(file.getInputStream(), root.resolve(stored), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Could not save file");
        }
        return stored;
    }

    public Resource load(String storedName) {
        Path p = root.resolve(storedName).normalize();
        if (!p.startsWith(root) || !Files.exists(p)) throw ApiException.notFound("File not found");
        return new FileSystemResource(p);
    }

    public void delete(String storedName) {
        if (storedName == null) return;
        try {
            Files.deleteIfExists(root.resolve(storedName).normalize());
        } catch (IOException ignored) {
            // best effort
        }
    }
}
