package be.amisdubonbourgogne.web.wine.controller;

import be.amisdubonbourgogne.web.wine.dto.WinePublicResponse;
import be.amisdubonbourgogne.web.wine.service.WineService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/api/wines")
@AllArgsConstructor
public class PublicWineController {
    private final WineService service;

    @GetMapping
    public List<WinePublicResponse> list() {
        return service.findAvailable();
    }

    @GetMapping("/featured")
    public List<WinePublicResponse> listFeatured(){
        return service.findFeatured();
    }
    @GetMapping("/{slug}")
    public WinePublicResponse wineSlug(@PathVariable String slug){
        return service.findBySlug(slug);
    }
}

