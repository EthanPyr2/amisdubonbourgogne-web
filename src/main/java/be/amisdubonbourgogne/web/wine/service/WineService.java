package be.amisdubonbourgogne.web.wine.service;

import be.amisdubonbourgogne.web.wine.dto.WinePublicResponse;
import be.amisdubonbourgogne.web.wine.entity.Wine;
import be.amisdubonbourgogne.web.wine.repository.WineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.plaf.synth.Region;
import java.util.List;

@Service
@AllArgsConstructor
public class WineService {
    private final WineRepository repository;
    //C
    public Wine save(Wine wine){
        return repository.save(wine);
    }
    //R
    public Wine read(Long id){
        return repository.getReferenceById(id);
    }
    public List<Wine> getAll(){
        return repository.findAll();
    }
    //U
    //D
    public void delete(Long id){
        repository.deleteById(id);
    }
    public void isNotAvailable(Wine wine){
        wine.setAvailable(false);
    }
    //Vers DTO
    public WinePublicResponse toPublic(Wine wine) {
        return new WinePublicResponse(
                wine.getSlug(),
                wine.getProducer(),
                wine.getAppellation(),
                wine.getCuvee(),
                (Region) wine.getRegion(),
                wine.getColor(),
                wine.getVintage(),
                wine.getPrice(),
                wine.getGrapes(),
                wine.getDescription()
        );
    }
    public List<WinePublicResponse> findAvailable() {
        return repository.findAll().stream()
                .filter(Wine::isAvailable)
                .map(this::toPublic)
                .toList();
    }
}
