package be.amisdubonbourgogne.web.region.repository;

import be.amisdubonbourgogne.web.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findAllByOrderByDisplayOrderAscNameAsc();
    List<Region> findByActiveTrueOrderByDisplayOrderAscNameAsc();
    Optional<Region> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, Long id);
}
