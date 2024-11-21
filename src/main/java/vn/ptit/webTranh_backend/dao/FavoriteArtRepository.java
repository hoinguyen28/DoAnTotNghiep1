package vn.ptit.webTranh_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import vn.ptit.webTranh_backend.entity.FavoriteArt;
import vn.ptit.webTranh_backend.entity.Art;
import vn.ptit.webTranh_backend.entity.User;


import java.util.List;

@RepositoryRestResource(path = "favorite-art")
public interface FavoriteArtRepository extends JpaRepository<FavoriteArt, Integer>{
    public FavoriteArt findFavoriteArtByArtAndUser(Art art, User user);
    public List<FavoriteArt> findFavoriteArtsByUser(User user);
}
