package vn.ptit.webTranh_backend.service.Order;

import vn.ptit.webTranh_backend.dao.*;
import vn.ptit.webTranh_backend.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImp implements OrderService{
    private final ObjectMapper objectMapper;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ArtRepository artRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    public OrderServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ResponseEntity<?> save(JsonNode jsonData) {
        try {
            // Chuyển đổi jsonData thành đối tượng Order
            Order orderData = objectMapper.treeToValue(jsonData, Order.class);
            orderData.setTotalPrice(orderData.getTotalPriceProduct());
            orderData.setDateCreated(Date.valueOf(LocalDate.now()));
            orderData.setStatus("Đang xử lý");

            // Lấy thông tin người dùng và thanh toán
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idUser"))));
            Optional<User> user = userRepository.findById(idUser);
            orderData.setUser(user.get());

            int idPayment = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idPayment"))));
            Optional<Payment> payment = paymentRepository.findById(idPayment);
            orderData.setPayment(payment.get());

            // Lưu đơn hàng
            Order newOrder = orderRepository.save(orderData);

            // Lấy thông tin sản phẩm (tranh) trong đơn hàng
            JsonNode artNode = jsonData.get("art");
            int quantity = Integer.parseInt(formatStringByJson(String.valueOf(artNode.get("quantity"))));

            // Vì mỗi tranh chỉ có 1 sản phẩm, chúng ta không cần quan tâm đến số lượng, chỉ cần kiểm tra và cập nhật là đủ
            Art artResponse = objectMapper.treeToValue(artNode.get("art"), Art.class);

            // Lấy tranh từ cơ sở dữ liệu
            Optional<Art> art = artRepository.findById(artResponse.getIdArt());
            if (art.isPresent()) {
                Art artEntity = art.get();

                // Kiểm tra nếu tranh có trong kho (quantity > 0)
                if (artEntity.getQuantity() > 0) {
                    // Cập nhật tranh sau khi bán (giảm quantity xuống 0)
                    artEntity.setQuantity(0); // Tranh đã được bán hết, không còn trong kho

                    // Tạo chi tiết đơn hàng
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setArt(artEntity);
                    orderDetail.setOrder(newOrder);
                    orderDetail.setPrice(artEntity.getPrice());
                    orderDetailRepository.save(orderDetail);

                    // Lưu lại tranh đã cập nhật
                    artRepository.save(artEntity);
                } else {
                    return ResponseEntity.badRequest().body("Sản phẩm đã hết hàng.");
                }
            }

            // Xóa các mục trong giỏ hàng của người dùng
            cartItemRepository.deleteCartItemsByIdUser(user.get().getIdUser());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }


    @Override
    @Transactional
    public ResponseEntity<?> update(JsonNode jsonData) {
        try {
            int idOrder = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idOrder"))));
            String status = formatStringByJson(String.valueOf(jsonData.get("status")));

            // Lấy đơn hàng từ cơ sở dữ liệu
            Optional<Order> order = orderRepository.findById(idOrder);
            if (order.isPresent()) {
                Order existingOrder = order.get();
                existingOrder.setStatus(status);

                // Nếu đơn hàng bị hủy, hoàn trả số lượng tranh về kho
                if (status.equals("Bị huỷ")) {
                    List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailsByOrder(existingOrder);

                    for (OrderDetail orderDetail : orderDetailList) {
                        Art artOrderDetail = orderDetail.getArt();

                        // Kiểm tra xem tranh có bị bán hết không (quantity == 0)
                        if (artOrderDetail.getQuantity() == 0) {
                            artOrderDetail.setQuantity(1); // Hoàn trả tranh về kho nếu đơn hàng bị hủy
                            artRepository.save(artOrderDetail);
                        }
                    }
                }

                // Lưu lại trạng thái đơn hàng
                orderRepository.save(existingOrder);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<?> cancel(JsonNode jsonData) {
        try {
            // Lấy idUser từ JSON và tìm thông tin User
            int idUser = Integer.parseInt(formatStringByJson(String.valueOf(jsonData.get("idUser"))));
            User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

            // Lấy đơn hàng gần nhất của người dùng
            Order order = orderRepository.findFirstByUserOrderByIdOrderDesc(user);
            if (order == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy đơn hàng.");
            }

            // Cập nhật trạng thái đơn hàng thành "Bị huỷ"
            order.setStatus("Bị huỷ");

            // Hoàn trả tranh về kho nếu đơn hàng bị hủy
            List<OrderDetail> orderDetailList = orderDetailRepository.findOrderDetailsByOrder(order);
            for (OrderDetail orderDetail : orderDetailList) {
                Art artOrderDetail = orderDetail.getArt();

                // Nếu tranh đã được bán (quantity == 0), hoàn trả lại
                if (artOrderDetail.getQuantity() == 0) {
                    artOrderDetail.setQuantity(1); // Hoàn trả lại tranh về kho
                    artRepository.save(artOrderDetail);
                }
            }

            // Lưu lại trạng thái mới của đơn hàng
            orderRepository.save(order);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().build();
    }

    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }

}