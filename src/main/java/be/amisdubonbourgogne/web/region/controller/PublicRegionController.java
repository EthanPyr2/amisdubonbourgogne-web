package be.amisdubonbourgogne.web.region.controller;

import be.amisdubonbourgogne.web.region.dto.RegionResponse;
import be.amisdubonbourgogne.web.region.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class PublicRegionController {
    private final RegionService service;

    @GetMapping
    public List<RegionResponse> list() {
        return service.findActive();
    }
}
