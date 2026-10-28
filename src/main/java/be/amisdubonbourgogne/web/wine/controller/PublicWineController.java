package be.amisdubonbourgogne.web.wine.controller;

import be.amisdubonbourgogne.web.wine.dto.WinePublicResponse;
import be.amisdubonbourgogne.web.wine.service.WineService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

}
