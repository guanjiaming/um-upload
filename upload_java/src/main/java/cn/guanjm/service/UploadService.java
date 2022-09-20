package cn.guanjm.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String uploadFile(MultipartFile multipartFile);
    String verifyFile(String hash);
}
