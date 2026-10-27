package be.amisdubonbourgogne.web.wine.repository;

import be.amisdubonbourgogne.web.wine.entity.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.plaf.synth.Region;
import java.util.List;
import java.util.Optional;

@Repository

public interface WineRepository extends JpaRepository<Wine,Long>{
    //Query SQL
    Optional<Wine> findBySlugAndAvailableTrue(String slug);
    List<Wine> findByAvailableTrueOrderByRegionAscColorAscAppellationAsc();
    List<Wine> findByAvailableTrueAndRegionOrderByColorAscAppellationAsc(Region region);


    boolean existsByRegionId(Long regionId);
}
