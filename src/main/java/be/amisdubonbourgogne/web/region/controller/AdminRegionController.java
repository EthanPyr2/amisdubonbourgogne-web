package be.amisdubonbourgogne.web.region.controller;

import be.amisdubonbourgogne.web.region.dto.RegionAdminResponse;
import be.amisdubonbourgogne.web.region.dto.RegionRequest;
import be.amisdubonbourgogne.web.region.service.RegionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/regions")
@RequiredArgsConstructor
public class AdminRegionController {
    private final RegionService service;

    @GetMapping
    public List<RegionAdminResponse> listAdmin() {
        return service.findAllAdmin();
    }

    @GetMapping("/{id}")
    public RegionAdminResponse detailAdmin(@PathVariable Long id) {
        return service.findByIdAdmin(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegionAdminResponse create(@Valid @RequestBody RegionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public RegionAdminResponse update(@PathVariable Long id, @Valid @RequestBody RegionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
