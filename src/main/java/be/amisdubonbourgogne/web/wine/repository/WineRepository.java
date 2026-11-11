package be.amisdubonbourgogne.web.wine.repository;

import be.amisdubonbourgogne.web.wine.entity.Wine;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository

public interface WineRepository extends JpaRepository<Wine,Long>{
    boolean existsBySlug(String slug);
    boolean existsByRegionId(Long regionId);

    @Override
    @EntityGraph(attributePaths = "region")
    List<Wine> findAll();

    @Override
    @EntityGraph(attributePaths = "region")
    Optional<Wine> findById(Long id);

    @EntityGraph(attributePaths = "region")
    Optional<Wine> findBySlug(String slug);
}
