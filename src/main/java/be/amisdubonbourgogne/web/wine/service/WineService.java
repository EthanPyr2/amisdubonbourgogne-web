package be.amisdubonbourgogne.web.wine.service;

import be.amisdubonbourgogne.web.region.service.RegionService;
import be.amisdubonbourgogne.web.wine.dto.WineAdminResponse;
import be.amisdubonbourgogne.web.wine.dto.WinePublicResponse;
import be.amisdubonbourgogne.web.wine.dto.WineFormatRequest;
import be.amisdubonbourgogne.web.wine.dto.WineFormatResponse;
import be.amisdubonbourgogne.web.wine.dto.WineRequest;
import be.amisdubonbourgogne.web.wine.entity.Wine;
import be.amisdubonbourgogne.web.wine.entity.WineFormat;
import be.amisdubonbourgogne.web.wine.exception.WineNotFoundException;
import be.amisdubonbourgogne.web.wine.repository.WineRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class WineService {
    private final WineRepository repository;
    private final RegionService regionService;
    private static final Comparator<Wine> ORDRE_CARTE =
            Comparator.comparing((Wine w) -> w.getRegion().getDisplayOrder())
                    .thenComparing(w -> w.getRegion().getName())
                    .thenComparing(w -> w.getColor().ordinal())
                    .thenComparing(Wine::getAppellation);
    //-)----PUBLIC----//
    public List<WinePublicResponse> findAvailable() {
        return repository.findAll().stream()
                .filter(Wine::isAvailable)
                .sorted(ORDRE_CARTE)
                .map(this::toPublic)
                .toList();
    }
    public List<WinePublicResponse> findFeatured() {
        return repository.findAll().stream()
                .filter(Wine::isAvailable)
                .filter(Wine::isFeatured)
                .sorted(ORDRE_CARTE)
                .map(this::toPublic)
                .toList();
    }
    public WinePublicResponse findBySlug(String slug){
       return repository.findBySlug(slug)
               .filter(Wine::isAvailable)
               .map(this::toPublic)
               .orElseThrow(() -> new WineNotFoundException("Vin introuvable (slug " + slug + ")"));

    }

    //-------ADMIN-----//
    //C
    @Transactional
    public WineAdminResponse create(WineRequest request) {
        Wine wine = new Wine();
        applyRequest(wine, request);
        wine.setSlug(generateUniqueSlug(wine));
        return toAdmin(repository.save(wine));

    }
    //R
    public WineAdminResponse findByIdAdmin(Long id){
        return toAdmin(findWine(id));


    }
    public List<WineAdminResponse> findAllAdmin(){
        return repository.findAll().stream()
                .sorted(ORDRE_CARTE)
                .map(this::toAdmin)
                .toList();
    }
    //U
    @Transactional
    public WineAdminResponse update(Long id, WineRequest request){
        Wine wine = findWine(id);
        applyRequest(wine, request);
        return toAdmin(wine);

    }
    @Transactional
    public void setAvailability(Long id, boolean available) {
        findWine(id).setAvailable(available);
    }
    //D
    @Transactional
    public void delete(Long id){
        repository.delete(findWine(id));
    }

    //-------Intern------//
    private WinePublicResponse toPublic(Wine wine) {
        return new WinePublicResponse(
                wine.getSlug(),
                wine.getProducer(),
                wine.getAppellation(),
                wine.getCuvee(),
                RegionService.toPublic(wine.getRegion()),
                wine.getColor(),
                wine.getVintage(),
                toFormats(wine),
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
                RegionService.toPublic(wine.getRegion()),
                wine.getColor(),
                wine.getVintage(),
                toFormats(wine),
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
        wine.setRegion(regionService.getByName(request.region()));
        wine.setColor(request.color());
        wine.setVintage(request.vintage());
        wine.setGrapes(request.grapes());
        wine.setDescription(request.description());
        wine.setCuvee(request.cuvee());
        applyFormats(wine, request.formats());
    }

    /**
     * Aligne les formats du vin sur ceux de la requête, en se basant sur le volume :
     * même volume → on met le prix à jour ; nouveau volume → on l'ajoute ; volume absent → on le supprime.
     * (Mettre à jour plutôt que tout supprimer/recréer évite de heurter la contrainte unique (wine_id, volume_cl).)
     */
    private void applyFormats(Wine wine, List<WineFormatRequest> requested) {
        // TreeMap compare les BigDecimal par valeur : 75 et 75.0 sont le même volume
        Map<BigDecimal, BigDecimal> priceByVolume = new TreeMap<>();
        for (WineFormatRequest format : requested) {
            if (priceByVolume.put(format.volumeCl(), format.price()) != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Le format " + format.volumeCl().stripTrailingZeros().toPlainString() + " cl est présent deux fois");
            }
        }

        wine.getFormats().removeIf(existing -> !priceByVolume.containsKey(existing.getVolumeCl()));

        priceByVolume.forEach((volume, price) -> wine.getFormats().stream()
                .filter(existing -> existing.getVolumeCl().compareTo(volume) == 0)
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setPrice(price),
                        () -> {
                            WineFormat format = new WineFormat();
                            format.setWine(wine);
                            format.setVolumeCl(volume);
                            format.setPrice(price);
                            wine.getFormats().add(format);
                        }));
    }

    private List<WineFormatResponse> toFormats(Wine wine) {
        return wine.getFormats().stream()
                .sorted(Comparator.comparing(WineFormat::getVolumeCl))
                .map(format -> new WineFormatResponse(format.getVolumeCl(), format.getPrice()))
                .toList();
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
    private Wine findWine(Long id){
        return repository.findById(id)
                .orElseThrow(() -> new WineNotFoundException("Vin introuvable (id " + id + ")"));
    }

}
