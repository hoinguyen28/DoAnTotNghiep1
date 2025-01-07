package vn.ptit.webTranh_backend.service.Discount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.ptit.webTranh_backend.entity.Discount;
import vn.ptit.webTranh_backend.entity.Art;
import vn.ptit.webTranh_backend.dao.DiscountRepository;
import vn.ptit.webTranh_backend.dao.ArtRepository;
import vn.ptit.webTranh_backend.service.Discount.DiscountService;

import java.util.Date;
import java.util.List;

@Service
public class DiscountServiceImpl implements DiscountService { @Autowired
private DiscountRepository discountRepository;

    @Autowired
    private ArtRepository artRepository;

    @Override
    public void applyDiscountForMultipleArtworks(List<Integer> artIds, double discountPercentage, Date startDate, Date endDate) {
        // Lấy tất cả các tranh từ ID
        List<Art> arts = artRepository.findAllByIdArtIn(artIds);  // Dùng phương thức đã sửa ở ArtRepository

        for (Art art : arts) {
            Discount discount = new Discount();
            discount.setDiscountPercentage(discountPercentage);
            discount.setStartDate(startDate);
            discount.setEndDate(endDate);
            discount.setArt(art);  // Gán tranh vào đối tượng giảm giá
            discountRepository.save(discount);  // Lưu giảm giá vào cơ sở dữ liệu
        }
    }
}