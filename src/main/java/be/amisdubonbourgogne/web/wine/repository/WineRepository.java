package be.amisdubonbourgogne.web.wine.repository;

import be.amisdubonbourgogne.web.wine.entity.Wine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository

public interface WineRepository extends JpaRepository<Wine,Long>{
    boolean existsBySlug(String slug);

}
