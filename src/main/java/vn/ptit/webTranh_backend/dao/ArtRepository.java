package vn.ptit.webTranh_backend.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;
import vn.ptit.webTranh_backend.entity.Art;


@RepositoryRestResource(path = "arts")
public interface ArtRepository extends JpaRepository<Art, Integer> {
    Page<Art> findByNameArtContaining(@RequestParam("nameArt") String nameArt, Pageable pageable);
    Page<Art> findByListGenres_idGenre(@RequestParam("idGenre") int idGenre, Pageable pageable);
    Page<Art> findByNameArtContainingAndListGenres_idGenre(@RequestParam("nameArt") String nameArt ,@RequestParam("idGenre") int idGenre, Pageable pageable);
    long count();
}
