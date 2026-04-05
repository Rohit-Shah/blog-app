package com.blog.blog.service.serviceBean.FileServiceBean;

import com.blog.blog.service.FileService.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryFileService implements FileService {
    @Override
    public String uploadFile(MultipartFile file) {
        return "";
    }
}
