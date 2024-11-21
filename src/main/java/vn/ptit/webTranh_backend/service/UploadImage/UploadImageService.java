package vn.ptit.webTranh_backend.service.UploadImage;

import org.springframework.web.multipart.MultipartFile;
public interface UploadImageService {
    String uploadImage(MultipartFile multipartFile, String name);
    void deleteImage(String imgUrl);
}
