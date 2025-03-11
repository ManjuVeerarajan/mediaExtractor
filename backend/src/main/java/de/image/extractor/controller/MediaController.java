package de.image.extractor.controller;

import de.image.extractor.model.MediaItem;
import de.image.extractor.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Component
public class MediaController {
    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);
    private final MediaService mediaService;

    @GetMapping("/all")
    public List<MediaItem> getAllMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return mediaService.getAllMedia(page, size);
        } catch (IOException e) {
            logger.error("Failed to retrieve all media: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve media: " + e.getMessage(), e);
        }
    }

    @GetMapping("/filter")
    public List<MediaItem> filterMedia(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return mediaService.filterMedia(keyword, page, size);
        } catch (IOException e) {
            logger.error("Failed to filter media for keyword: {}", keyword, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Filter failed: " + e.getMessage(), e);
        }
    }
}
