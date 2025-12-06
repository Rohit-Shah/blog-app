package com.blog.blog.service.FileService;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class FileService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${CLOUDINARY_URL}")
    String cloudinaryURL;

    private Cloudinary cloudinaryService;

    @PostConstruct
    public void init(){
        this.cloudinaryService = new Cloudinary(cloudinaryURL);
    }

    @Async
    public CompletableFuture<String> uploadFile(byte[] imageBytes)  {
        try{
            Map params = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true,
                    "folder","blog_post_uploads"
            );

            Map uploadedMedia = cloudinaryService.uploader().upload(imageBytes, params);
            return CompletableFuture.completedFuture(uploadedMedia.get("secure_url").toString());
        }catch (IOException e){
            log.error("Error while uploading image : {} ",e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

//    public String uploadFile1()  {
//        Cloudinary cloudinary = new Cloudinary(cloudinaryURL);
//        try{
//            Map params1 = ObjectUtils.asMap(
//                    "use_filename", true,
//                    "unique_filename", false,
//                    "overwrite", true
//            );
//
//            System.out.println(
//                    cloudinary.uploader().upload("https://cloudinary-devs.github.io/cld-docs-assets/assets/images/coffee_cup.jpg", params1));
//        }catch (IOException e){
//            throw new RuntimeException(e.getMessage());
//        }
//        return "uploaded";
//    }

}
