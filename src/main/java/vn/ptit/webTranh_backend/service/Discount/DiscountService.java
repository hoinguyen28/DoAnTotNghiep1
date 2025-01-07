package vn.ptit.webTranh_backend.service.Discount;

import java.util.List;
import java.util.Date;

public interface DiscountService {
    void applyDiscountForMultipleArtworks(List<Integer> artIds, double discountPercentage, Date startDate, Date endDate);
}