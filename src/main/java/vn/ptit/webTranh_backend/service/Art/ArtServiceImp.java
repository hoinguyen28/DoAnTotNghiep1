package vn.ptit.webTranh_backend.service.Art;

import vn.ptit.webTranh_backend.dao.ArtRepository;
import vn.ptit.webTranh_backend.dao.GenreRepository;
import vn.ptit.webTranh_backend.dao.ImageRepository;
import vn.ptit.webTranh_backend.entity.Art;
import vn.ptit.webTranh_backend.entity.Genre;
import vn.ptit.webTranh_backend.entity.Image;
import vn.ptit.webTranh_backend.service.UploadImage.UploadImageService;
import vn.ptit.webTranh_backend.service.Util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class ArtServiceImp implements ArtService{
    private final ObjectMapper objectMapper;
    @Autowired
    private ArtRepository artRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UploadImageService uploadImageService;

    public ArtServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode artJson) {
        try {
            Art art = objectMapper.treeToValue(artJson, Art.class);

            // Lưu thể loại của tranh
            List<Integer> idGenreList = objectMapper.readValue(artJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
            });
            List<Genre> genreList = new ArrayList<>();
            for (int idGenre : idGenreList) {
                Optional<Genre> genre = genreRepository.findById(idGenre);
                genreList.add(genre.get());
            }
            art.setListGenres(genreList);

            // Lưu trước để lấy id tranh đặt tên cho ảnh
            Art newArt = artRepository.save(art);

            // Lưu thumbnail cho ảnh
            String dataThumbnail = formatStringByJson(String.valueOf((artJson.get("thumbnail"))));

            Image thumbnail = new Image();
            thumbnail.setArt(newArt);
//            thumbnail.setDataImage(dataThumbnail);
            thumbnail.setThumbnail(true);
            MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
            String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Art_" + newArt.getIdArt());
            thumbnail.setUrlImage(thumbnailUrl);

            List<Image> imagesList = new ArrayList<>();
            imagesList.add(thumbnail);


            // Lưu những ảnh có liên quan
            String dataRelatedImg = formatStringByJson(String.valueOf((artJson.get("relatedImg"))));
            List<String> arrDataRelatedImg = objectMapper.readValue(artJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {
            });

            for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                String img = arrDataRelatedImg.get(i);
                Image image = new Image();
                image.setArt(newArt);
//                image.setDataImage(img);
                image.setThumbnail(false);
                MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + newArt.getIdArt() + "." + i);
                image.setUrlImage(imgURL);
                imagesList.add(image);
            }

            newArt.setListImages(imagesList);
            // Cập nhật lại ảnh
            artRepository.save(newArt);

            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode artJson) {
        try {
            Art art = objectMapper.treeToValue(artJson, Art.class);
            List<Image> imagesList = imageRepository.findImagesByArt(art);

            // Lưu thể loại của tranh
            List<Integer> idGenreList = objectMapper.readValue(artJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
            });
            List<Genre> genreList = new ArrayList<>();
            for (int idGenre : idGenreList) {
                Optional<Genre> genre = genreRepository.findById(idGenre);
                genreList.add(genre.get());
            }
            art.setListGenres(genreList);

            // Kiểm tra xem thumbnail có thay đổi không
            String dataThumbnail = formatStringByJson(String.valueOf((artJson.get("thumbnail"))));
            if (Base64ToMultipartFileConverter.isBase64(dataThumbnail)) {
                for (Image image : imagesList) {
                    if (image.isThumbnail()) {
//                        image.setDataImage(dataThumbnail);
                        MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
                        String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Art_" + art.getIdArt());
                        image.setUrlImage(thumbnailUrl);
                        imageRepository.save(image);
                        break;
                    }
                }
            }

            Art newArt = artRepository.save(art);

            // Kiểm tra ảnh có liên quan
            List<String> arrDataRelatedImg = objectMapper.readValue(artJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {});

            // Xem có xoá tất ở bên FE không
            boolean isCheckDelete = true;

            for (String img : arrDataRelatedImg) {
                if (!Base64ToMultipartFileConverter.isBase64(img)) {
                    isCheckDelete = false;
                }
            }

            // Nếu xoá hết tất cả
            if (isCheckDelete) {
                imageRepository.deleteImagesWithFalseThumbnailByArtId(newArt.getIdArt());
                Image thumbnailTemp = imagesList.get(0);
                imagesList.clear();
                imagesList.add(thumbnailTemp);
                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                    String img = arrDataRelatedImg.get(i);
                    Image image = new Image();
                    image.setArt(newArt);
//                    image.setDataImage(img);
                    image.setThumbnail(false);
                    MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                    String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + newArt.getIdArt() + "." + i);
                    image.setUrlImage(imgURL);
                    imagesList.add(image);
                }
            } else {
                // Nếu không xoá hết tất cả (Giữ nguyên ảnh hoặc thêm ảnh vào)
                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
                    String img = arrDataRelatedImg.get(i);
                    if (Base64ToMultipartFileConverter.isBase64(img)) {
                        Image image = new Image();
                        image.setArt(newArt);
//                        image.setDataImage(img);
                        image.setThumbnail(false);
                        MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
                        String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + newArt.getIdArt() + "." + i);
                        image.setUrlImage(imgURL);
                        imageRepository.save(image);
                    }
                }
            }

            newArt.setListImages(imagesList);
            // Cập nhật lại ảnh
            artRepository.save(newArt);

            return ResponseEntity.ok("Success!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public long getTotalArt() {
        return artRepository.count();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}
