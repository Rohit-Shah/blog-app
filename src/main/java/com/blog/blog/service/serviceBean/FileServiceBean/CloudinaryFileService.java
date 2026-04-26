package com.blog.blog.service.serviceBean.FileServiceBean;

import com.blog.blog.service.FileService.FileService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryFileService implements FileService {

    private final Cloudinary cloudinaryConfig;

    @Override
    public String uploadFile(MultipartFile file) {
        File uploadedFile = null;
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            uploadedFile = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
            file.transferTo(uploadedFile);
            Map<String,Object> uploadedResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            return uploadedResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException("File upload failed",e);
        }
        finally {
            if(uploadedFile != null && uploadedFile.exists()){
                boolean isLocalFileDeleted = uploadedFile.delete();
                if(isLocalFileDeleted){
                    log.info("Local file deleted");
                }
                else{
                    log.warn("Unable to delete local file");
                }
            }
        }
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {

        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
