package be.amisdubonbourgogne.web.wine.service;

import be.amisdubonbourgogne.web.wine.dto.WineAdminResponse;
import be.amisdubonbourgogne.web.wine.dto.WinePublicResponse;
import be.amisdubonbourgogne.web.wine.dto.WineRequest;
import be.amisdubonbourgogne.web.wine.entity.Wine;
import be.amisdubonbourgogne.web.wine.repository.WineRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


import java.text.Normalizer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class WineService {
    private final WineRepository repository;
    //-)----PUBLIC----//
    public List<WinePublicResponse> findAvailable() {
        return repository.findAll().stream()
                .filter(Wine::isAvailable)
                .map(this::toPublic)
                .toList();
    }

    //-------ADMIN-----//
    //C
    public WineAdminResponse save(WineRequest request) {
        Wine wine = new Wine();
        applyRequest(wine, request);
        wine.setSlug(generateUniqueSlug(wine));
        return toAdmin(repository.save(wine));

    }
    //R
    public WineAdminResponse findByIdAdmin(Long id){
        return repository.findById(id)
                .map(this::toAdmin)
                .orElseThrow(() -> new RuntimeException("Vin introuvable"));


    }
    public List<WineAdminResponse> findAllAdmin(){
        return repository.findAll().stream()
                .map(this::toAdmin)
                .toList();
    }
    //U
    //D
    public void delete(Long id){
        repository.deleteById(id);
    }
    public void isNotAvailable(Wine wine){
        wine.setAvailable(false);
    }
    //-------Intern------//
    private WinePublicResponse toPublic(Wine wine) {
        return new WinePublicResponse(
                wine.getSlug(),
                wine.getProducer(),
                wine.getAppellation(),
                wine.getCuvee(),
                wine.getRegion(),
                wine.getColor(),
                wine.getVintage(),
                wine.getPrice(),
                wine.getGrapes(),
                wine.getDescription()
        );
    }
    private WineAdminResponse toAdmin(Wine wine) {
        return new WineAdminResponse(
                wine.getId(),
                wine.getSlug(),
                wine.getProducer(),
                wine.getAppellation(),
                wine.getCuvee(),
                wine.getRegion(),
                wine.getColor(),
                wine.getVintage(),
                wine.getVolumeCl(),
                wine.getPrice(),
                wine.getGrapes(),
                wine.getDescription(),
                wine.isAvailable(),
                wine.isFeatured(),
                wine.getCreatedAt(),
                wine.getUpdateAt()

        );
    }

    private void applyRequest(Wine wine, WineRequest request){
        wine.setProducer(request.producer());
        wine.setAppellation(request.appellation());
        wine.setRegion(request.region());
        wine.setColor(request.color());
        wine.setVintage(request.vintage());
        wine.setPrice(request.price());
        wine.setGrapes(request.grapes());
        wine.setDescription(request.description());

    }
    private String toSlug(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
    private String generateUniqueSlug(Wine wine) {
        String base = toSlug(Stream.of(wine.getProducer(), wine.getAppellation(),
                        wine.getCuvee(), wine.getVintage())
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(" ")));

        String slug = base;
        int counter = 2;
        while (repository.existsBySlug(slug)) {
            slug = base + "-" + counter++;
        }
        return slug;
    }

}
