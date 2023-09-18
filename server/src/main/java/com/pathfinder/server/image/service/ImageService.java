package com.pathfinder.server.image.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.pathfinder.server.global.exception.imageexception.FileIsNotImageFile;
import com.pathfinder.server.global.exception.imageexception.ImageNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@Service
public class ImageService {
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp"
    );
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ImageService(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (!isImageFile(file)) {
            throw new FileIsNotImageFile();
        }

        String imageName = file.getOriginalFilename() + "_" + UUID.randomUUID().toString();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucket, imageName, file.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, imageName).toString();
    }


    public void deleteImage(String imageUrl) {
        try {
            String key = findVerifiedImage(imageUrl);
            amazonS3.deleteObject(bucket, key);
        } catch (AmazonServiceException e) {
            throw new ImageNotFoundException();
        }
    }

    private String  findVerifiedImage(String imageUrl) {
        String regex = "https://main20-pathfinder\\.s3\\.ap-northeast-2\\.amazonaws\\.com/(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(imageUrl);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private boolean isImageFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new FileIsNotImageFile();
        }

        String fileExtension = getFileEctension(fileName).toLowerCase();
        return ALLOWED_IMAGE_EXTENSIONS.contains(fileExtension);

    }

    private String getFileEctension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        if (lastIndexOfDot == -1) {
            return "";
        }
        return fileName.substring(lastIndexOfDot+1);
    }
}