package com.pathfinder.server.image.controller;

import com.pathfinder.server.image.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class imgeController {
    private final ImageService imageService;

    public imgeController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity postImage(@RequestParam(value="image") MultipartFile image) throws  IOException {
        String url = imageService.saveFile(image);
        return new ResponseEntity(url, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity deleteImage(@RequestParam String imageUrl) {
        imageService.deleteImage(imageUrl);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
