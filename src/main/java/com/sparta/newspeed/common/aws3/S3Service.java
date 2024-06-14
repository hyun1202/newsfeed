package com.sparta.newspeed.common.aws3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String fileUrl = "https://" + bucket + "/test" + fileName;
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);
            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String readFile(String fileName) {
        URL url = amazonS3Client.getUrl(bucket, fileName);
        String urltext = "" + url;
        return urltext;
    }
    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
