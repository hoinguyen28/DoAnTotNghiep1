package vn.ptit.webTranh_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "art")

public class Art {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_art")
    private int idArt; // Mã tranh
    @Column(name = "name_art")
    private String nameArt; // Tên tranh
    @Column(name = "author")
    private String author; // Tên tác giả
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description; // Mô tả
    @Column(name = "price")
    private double price; // Giá niêm yết
    @Column(name = "quantity")
    private int quantity; // Số lượng
    @Column(name = "review_status")
    private String reviewStatus = "PENDING"; // Giá trị mặc định
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "art_genre", joinColumns = @JoinColumn(name = "id_art"), inverseJoinColumns = @JoinColumn(name = "id_genre"))
    private List<Genre> listGenres; // Danh tranh thể loại

    @OneToMany(mappedBy = "art",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Image> listImages; // Danh tranh ảnh

    @OneToOne(mappedBy = "art", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private OrderDetail orderDetail; // Một tranh chỉ có một chi tiết đơn 

    @OneToMany(mappedBy = "art",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FavoriteArt> listFavoriteArts; // Danh tranh tranh yêu thích
    @OneToMany(mappedBy = "art",fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Feedbacks> listFeedbacks; // Danh tranh feedbacks

    @OneToMany(mappedBy = "art",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CartItem> listCartItems;
    @OneToMany(mappedBy = "art", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Discount> discounts; // Danh tranh giảm giá

    // Phương thức tính giá sau khi giảm giá
    public double getFinalPrice() {
        double finalPrice = price; // Giá gốc

        // Lọc các giảm giá hiệu lực tại thời điểm hiện tại
        Optional<Discount> currentDiscount = discounts.stream()
                .filter(discount -> !discount.getStartDate().after(new Date()) &&
                        !discount.getEndDate().before(new Date()))
                .findFirst();

        // Nếu có giảm giá hợp lệ, tính toán giá cuối cùng
        if (currentDiscount.isPresent()) {
            Discount discount = currentDiscount.get();
            finalPrice = finalPrice * (1 - discount.getDiscountPercentage() / 100); // Áp dụng giảm giá
        }

        return finalPrice;
    }

}
