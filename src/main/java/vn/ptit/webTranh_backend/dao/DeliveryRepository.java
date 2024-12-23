package vn.ptit.webTranh_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.ptit.webTranh_backend.entity.Delivery;


@RepositoryRestResource(path = "deliveries")
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
}
