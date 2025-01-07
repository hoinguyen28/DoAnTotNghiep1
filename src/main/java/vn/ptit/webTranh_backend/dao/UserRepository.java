package vn.ptit.webTranh_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.ptit.webTranh_backend.entity.User;
@RepositoryRestResource(excerptProjection = User.class, path = "users")
public interface UserRepository extends JpaRepository<User, Integer> {
    public boolean existsByUsername(String username);
    public boolean existsByEmail(String email);
    public User findByUsername(String username);
    public User findByEmail(String email);
    @Query(value = "SELECT * FROM user WHERE id_user = :id", nativeQuery = true)
    User findUserById(@Param("id") int id);
}
