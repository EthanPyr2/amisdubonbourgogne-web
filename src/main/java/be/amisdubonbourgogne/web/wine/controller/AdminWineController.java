package be.amisdubonbourgogne.web.wine.controller;

import be.amisdubonbourgogne.web.wine.dto.WineAdminResponse;
import be.amisdubonbourgogne.web.wine.dto.WineRequest;
import be.amisdubonbourgogne.web.wine.service.WineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/wines")
@RequiredArgsConstructor
public class AdminWineController {
    private final WineService service;
    @GetMapping
    public List<WineAdminResponse> listAdmin(){
        return service.findAllAdmin();
    }
    @GetMapping("/{id}")
    public WineAdminResponse detailAdmin(@PathVariable Long id){
        return service.findByIdAdmin(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WineAdminResponse create(@Valid @RequestBody WineRequest request){
        return service.create(request);
    }
    @PutMapping("/{id}")
    public WineAdminResponse update(@PathVariable Long id, @Valid @RequestBody WineRequest request){
        return service.update(id, request);
    }
    @PatchMapping("/{id}/availability")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setAvailability(@PathVariable Long id, @RequestParam boolean available){
        service.setAvailability(id, available);
    }
    @PatchMapping("/{id}/featured")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setFeatured(@PathVariable Long id, @RequestParam boolean featured){
        service.setFeatured(id, featured);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id){
        service.delete(id);
    }

}
