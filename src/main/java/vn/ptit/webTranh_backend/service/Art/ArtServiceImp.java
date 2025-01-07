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
import java.util.NoSuchElementException;
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

//    @Override
//    @Transactional
//    public ResponseEntity<?> update(JsonNode artJson) {
//        try {
//            Art art = objectMapper.treeToValue(artJson, Art.class);
//            List<Image> imagesList = imageRepository.findImagesByArt(art);
//
//            // Lưu thể loại của tranh
//            List<Integer> idGenreList = objectMapper.readValue(artJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {
//            });
//            List<Genre> genreList = new ArrayList<>();
//            for (int idGenre : idGenreList) {
//                Optional<Genre> genre = genreRepository.findById(idGenre);
//                genreList.add(genre.get());
//            }
//            art.setListGenres(genreList);
//
//            // Kiểm tra xem thumbnail có thay đổi không
//            String dataThumbnail = formatStringByJson(String.valueOf((artJson.get("thumbnail"))));
//            if (Base64ToMultipartFileConverter.isBase64(dataThumbnail)) {
//                for (Image image : imagesList) {
//                    if (image.isThumbnail()) {
////                        image.setDataImage(dataThumbnail);
//                        MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
//                        String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Art_" + art.getIdArt());
//                        image.setUrlImage(thumbnailUrl);
//                        imageRepository.save(image);
//                        break;
//                    }
//                }
//            }
//
//            Art newArt = artRepository.save(art);
//
//            // Kiểm tra ảnh có liên quan
//            List<String> arrDataRelatedImg = objectMapper.readValue(artJson.get("relatedImg").traverse(), new TypeReference<List<String>>() {});
//
//            // Xem có xoá tất ở bên FE không
//            boolean isCheckDelete = true;
//
//            for (String img : arrDataRelatedImg) {
//                if (!Base64ToMultipartFileConverter.isBase64(img)) {
//                    isCheckDelete = false;
//                }
//            }
//            // Nếu xoá hết tất cả
//            if (isCheckDelete) {
//                imageRepository.deleteImagesWithFalseThumbnailByArtId(newArt.getIdArt());
//                Image thumbnailTemp = imagesList.get(0);
//                imagesList.clear();
//                imagesList.add(thumbnailTemp);
//                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
//                    String img = arrDataRelatedImg.get(i);
//                    Image image = new Image();
//                    image.setArt(newArt);
////                    image.setDataImage(img);
//                    image.setThumbnail(false);
//                    MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
//                    String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + newArt.getIdArt() + "." + i);
//                    image.setUrlImage(imgURL);
//                    imagesList.add(image);
//                }
//            } else {
//                // Nếu không xoá hết tất cả (Giữ nguyên ảnh hoặc thêm ảnh vào)
//                for (int i = 0; i < arrDataRelatedImg.size(); i++) {
//                    String img = arrDataRelatedImg.get(i);
//                    if (Base64ToMultipartFileConverter.isBase64(img)) {
//                        Image image = new Image();
//                        image.setArt(newArt);
////                        image.setDataImage(img);
//                        image.setThumbnail(false);
//                        MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
//                        String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + newArt.getIdArt() + "." + i);
//                        image.setUrlImage(imgURL);
//                        imageRepository.save(image);
//                    }
//                }
//            }
//
//            newArt.setListImages(imagesList);
//            // Cập nhật lại ảnh
//            artRepository.save(newArt);
//
//            return ResponseEntity.ok("Success!");
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }@Override
//@Transactional
//public ResponseEntity<?> update(JsonNode artJson) {
//    try {
//        // Chuyển JSON thành đối tượng Art
//        Art art = objectMapper.treeToValue(artJson, Art.class);
//        List<Image> imageList = imageRepository.findImagesByArt(art);
//
//        // Lưu danh sách thể loại
//        List<Integer> idGenreList = objectMapper.readValue(artJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {});
//        List<Genre> genreList = new ArrayList<>();
//        for (int idGenre : idGenreList) {
//            Optional<Genre> genre = genreRepository.findById(idGenre);
//            genre.ifPresent(genreList::add);
//        }
//        art.setListGenres(genreList);
//
//        // Cập nhật thumbnail
//        String dataThumbnail = artJson.get("thumbnail").asText();
//        if (Base64ToMultipartFileConverter.isBase64(dataThumbnail)) {
//            for (Image image : imageList) {
//                if (image.isThumbnail()) {
//                    MultipartFile multipartFile = Base64ToMultipartFileConverter.convert(dataThumbnail);
//                    String thumbnailUrl = uploadImageService.uploadImage(multipartFile, "Art_" + art.getIdArt());
//                    image.setUrlImage(thumbnailUrl);
//                    image.setArt(art); // Gắn liên kết Art
//                    imageRepository.save(image);
//                    break;
//                }
//            }
//        }
//
//        // Lưu Art trước để đảm bảo nó đã có ID
//        Art updatedArt = artRepository.save(art);
//
//        // Xử lý danh sách ảnh liên quan
//        List<String> arrDataRelatedImg = objectMapper.readValue(artJson.get("listImages").traverse(), new TypeReference<List<String>>() {});
//        boolean isDeleteAllImages = arrDataRelatedImg.stream().allMatch(Base64ToMultipartFileConverter::isBase64);
//
//        if (isDeleteAllImages) {
//            // Xoá tất cả ảnh liên quan ngoại trừ thumbnail
//            imageRepository.deleteImagesWithFalseThumbnailByArtId(updatedArt.getIdArt());
//            Image thumbnailTemp = imageList.stream().filter(Image::isThumbnail).findFirst().orElse(null);
//            imageList.clear();
//            if (thumbnailTemp != null) {
//                imageList.add(thumbnailTemp);
//            }
//            // Thêm ảnh mới
//            for (int i = 0; i < arrDataRelatedImg.size(); i++) {
//                String img = arrDataRelatedImg.get(i);
//                Image image = new Image();
//                image.setArt(updatedArt); // Gắn liên kết Art
//                image.setThumbnail(false);
//                MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
//                String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + updatedArt.getIdArt() + "." + i);
//                image.setUrlImage(imgURL);
//                imageList.add(image);
//            }
//        } else {
//            // Nếu không xoá hoàn toàn
//            for (int i = 0; i < arrDataRelatedImg.size(); i++) {
//                String img = arrDataRelatedImg.get(i);
//                if (Base64ToMultipartFileConverter.isBase64(img)) {
//                    Image image = new Image();
//                    image.setArt(updatedArt); // Gắn liên kết Art
//                    image.setThumbnail(false);
//                    MultipartFile relatedImgFile = Base64ToMultipartFileConverter.convert(img);
//                    String imgURL = uploadImageService.uploadImage(relatedImgFile, "Art_" + updatedArt.getIdArt() + "." + i);
//                    image.setUrlImage(imgURL);
//                    imageRepository.save(image);
//                }
//            }
//        }
//
//        updatedArt.setListImages(imageList);
//        artRepository.save(updatedArt);
//
//        return ResponseEntity.ok("Update successful!");
//    } catch (Exception e) {
//        e.printStackTrace();
//        return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
//    }
//}

    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode artJson) {
        try {
            // Chuyển JSON thành đối tượng Art
            Art art = objectMapper.treeToValue(artJson, Art.class);

            // Lưu thể loại liên quan
            List<Integer> idGenreList = objectMapper.readValue(artJson.get("idGenres").traverse(), new TypeReference<List<Integer>>() {});
            List<Genre> genreList = new ArrayList<>();
            for (int idGenre : idGenreList) {
                genreRepository.findById(idGenre).ifPresent(genreList::add);
            }
            art.setListGenres(genreList);

            // Danh sách ảnh hiện tại
            List<Image> currentImages = imageRepository.findImagesByArt(art);

            // Cập nhật thumbnail
            String thumbnailData = artJson.get("thumbnail").asText();
            if (Base64ToMultipartFileConverter.isBase64(thumbnailData)) {
                MultipartFile thumbnailFile = Base64ToMultipartFileConverter.convert(thumbnailData);
                String thumbnailUrl = uploadImageService.uploadImage(thumbnailFile, "Art_" + art.getIdArt());

                Image thumbnailImage = currentImages.stream()
                        .filter(Image::isThumbnail)
                        .findFirst()
                        .orElse(new Image());
                thumbnailImage.setArt(art);
                thumbnailImage.setUrlImage(thumbnailUrl);
                thumbnailImage.setThumbnail(true);
                imageRepository.save(thumbnailImage);
            }

            // Danh sách ảnh liên quan
            List<String> relatedImagesData = objectMapper.readValue(artJson.get("listImages").traverse(), new TypeReference<List<String>>() {});
            List<Image> updatedImages = new ArrayList<>();

            for (int i = 0; i < relatedImagesData.size(); i++) {
                String imgData = relatedImagesData.get(i);
                if (Base64ToMultipartFileConverter.isBase64(imgData)) {
                    MultipartFile relatedFile = Base64ToMultipartFileConverter.convert(imgData);
                    String imgUrl = uploadImageService.uploadImage(relatedFile, "Art_" + art.getIdArt() + "_" + i);

                    Image image = new Image();
                    image.setArt(art);
                    image.setThumbnail(false);
                    image.setUrlImage(imgUrl);
                    updatedImages.add(image);
                }
            }

            // Xóa các ảnh cũ không còn trong danh sách
            List<Image> imagesToDelete = currentImages.stream()
                    .filter(img -> !img.isThumbnail() && !updatedImages.contains(img))
                    .toList();
            imageRepository.deleteAll(imagesToDelete);

            // Lưu lại danh sách ảnh mới
            imageRepository.saveAll(updatedImages);

            // Cập nhật danh sách ảnh trong Art
            art.setListImages(updatedImages);
            artRepository.save(art);
            updateArtReviewStatus(art.getIdArt(),"Chờ duyệt");
            return ResponseEntity.ok("Update successful!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("An error occurred: " + e.getMessage());
        }
    }

    public void updateArtReviewStatus(int idArt, String reviewStatus) {
        // Tìm tranh theo ID
        Art art = artRepository.findById(idArt)
                .orElseThrow(() -> new NoSuchElementException("Art not found with ID: " + idArt));

        // Cập nhật reviewStatus
        art.setReviewStatus(reviewStatus);

        // Lưu thay đổi vào cơ sở dữ liệu
        artRepository.save(art);
    }

    @Override
    public long getTotalArt() {
        return artRepository.count();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}
