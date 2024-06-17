package com.sparta.newspeed.awss3.service;

import com.sparta.newspeed.common.aws3.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest(properties = "spring.profiles.active:test")
//public class S3ServiceTest {
//    @Autowired
//    private S3Service s3Service;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucket;
//
//
////    public String readFile(String fileName) {
////        URL url = amazonS3Client.getUrl(bucket, fileName);
////        String urltext = "" + url;
////        return urltext;
////    }
//    @Test
//    public void deleteFile() {
//        s3Service.deleteFile("image.png");
//    }
//}
