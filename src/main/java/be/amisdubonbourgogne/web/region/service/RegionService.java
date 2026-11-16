package be.amisdubonbourgogne.web.region.service;

import be.amisdubonbourgogne.web.region.dto.RegionAdminResponse;
import be.amisdubonbourgogne.web.region.dto.RegionRequest;
import be.amisdubonbourgogne.web.region.dto.RegionResponse;
import be.amisdubonbourgogne.web.region.entity.Region;
import be.amisdubonbourgogne.web.region.exception.RegionNotFoundException;
import be.amisdubonbourgogne.web.region.repository.RegionRepository;
import be.amisdubonbourgogne.web.wine.repository.WineRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;

@Service
@AllArgsConstructor
public class RegionService {
    private final RegionRepository repository;
    private final WineRepository wineRepository;

    //-------PUBLIC-----//
    public List<RegionResponse> findActive() {
        return repository.findByActiveTrueOrderByDisplayOrderAscNameAsc().stream()
                .map(RegionService::toPublic)
                .toList();
    }

    //-------ADMIN-----//
    public List<RegionAdminResponse> findAllAdmin() {
        return repository.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .map(this::toAdmin)
                .toList();
    }

    public RegionAdminResponse findByIdAdmin(Long id) {
        return toAdmin(findRegion(id));
    }

    @Transactional
    public RegionAdminResponse create(RegionRequest request) {
        Region region = new Region();
        applyRequest(region, request);
        if (repository.existsBySlug(region.getSlug())) {
            throw conflict(region.getName());
        }
        return toAdmin(repository.save(region));
    }

    @Transactional
    public RegionAdminResponse update(Long id, RegionRequest request) {
        Region region = findRegion(id);
        applyRequest(region, request);
        if (repository.existsBySlugAndIdNot(region.getSlug(), id)) {
            throw conflict(region.getName());
        }
        return toAdmin(region);
    }

    /**
     * Supprime une région uniquement si aucun vin ne l'utilise.
     * Sinon : 409, il faut la désactiver (active = false) ou réassigner les vins.
     */
    @Transactional
    public void delete(Long id) {
        Region region = findRegion(id);
        if (wineRepository.existsByRegionId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La région \"" + region.getName() + "\" est utilisée par des vins : désactivez-la plutôt.");
        }
        repository.delete(region);
    }

    /**
     * Utilisé par WineService : retrouve une région à partir de son nom.
     * Insensible aux accents et aux majuscules (« vallee du rhone nord » trouve « Vallée du Rhône Nord »).
     */
    public Region getByName(String name) {
        return repository.findBySlug(toSlug(name))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Région inconnue : " + name));
    }

    public static RegionResponse toPublic(Region region) {
        return new RegionResponse(region.getSlug(), region.getName());
    }

    //-------Intern------//
    private RegionAdminResponse toAdmin(Region region) {
        return new RegionAdminResponse(
                region.getId(),
                region.getSlug(),
                region.getName(),
                region.getDisplayOrder(),
                region.isActive()
        );
    }

    private void applyRequest(Region region, RegionRequest request) {
        String name = request.name().trim();
        region.setName(name);
        region.setSlug(toSlug(name));
        region.setDisplayOrder(request.displayOrder());
        if (request.active() != null) {
            region.setActive(request.active());
        }
    }

    private Region findRegion(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RegionNotFoundException("Région introuvable (id " + id + ")"));
    }

    private ResponseStatusException conflict(String name) {
        return new ResponseStatusException(HttpStatus.CONFLICT, "La région \"" + name + "\" existe déjà");
    }

    private String toSlug(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }
}
