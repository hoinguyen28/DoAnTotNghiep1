package vn.ptit.webTranh_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.ptit.webTranh_backend.entity.Delivery;
import vn.ptit.webTranh_backend.entity.Discount;


@RepositoryRestResource(path = "discounts")
public interface DiscountRepository extends JpaRepository<Discount, Integer> {
}
