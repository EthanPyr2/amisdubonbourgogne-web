package be.amisdubonbourgogne.web.wine.dto;

import be.amisdubonbourgogne.web.region.dto.RegionResponse;
import be.amisdubonbourgogne.web.wine.entity.WineColor;


import java.math.BigDecimal;
import java.time.Instant;

public record WineAdminResponse(
        Long id,
        String slug,
        String producer,
        String appellation,
        String cuvee,
        RegionResponse region,
        WineColor color,
        Integer vintage,
        BigDecimal volumeCl,
        BigDecimal price,
        String grapes,
        String description,
        boolean available,
        boolean featured,
        Instant createdAt,
        Instant updateAt
) {
}
