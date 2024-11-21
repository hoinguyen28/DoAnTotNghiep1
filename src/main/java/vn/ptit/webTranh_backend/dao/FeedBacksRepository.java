package vn.ptit.webTranh_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.ptit.webTranh_backend.entity.Feedbacks;


@RepositoryRestResource(path = "feedbacks")
public interface FeedBacksRepository extends JpaRepository<Feedbacks, Integer> {
    long countBy();
}
