package com.pathfinder.server.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pathfinder.server.global.exception.imageexception.ImageNotFoundException;
import com.pathfinder.server.image.entity.Image;
import com.pathfinder.server.image.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Service
public class ImageService {
    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ImageService(AmazonS3 amazonS3, ImageRepository imageRepository) {
        this.amazonS3 = amazonS3;
        this.imageRepository = imageRepository;
    }

    public String saveFile(MultipartFile multipartFile) throws IOException {
        String imageName = multipartFile.getOriginalFilename() + "_" + UUID.randomUUID().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, imageName, multipartFile.getInputStream(), metadata);
        String url = amazonS3.getUrl(bucket, imageName).toString();

        Image image = new Image();
        image.setImageName(imageName);
        image.setUrl(url);
        imageRepository.save(image);

        return url;
    }


    public void deleteImage(String imageUrl)  {
        Optional<Image> image = imageRepository.findByUrl(imageUrl);
        if (image.isPresent()) {
            amazonS3.deleteObject(bucket, image.get().getImageName());
        }
        else {
            throw new ImageNotFoundException();
        }
    }

}