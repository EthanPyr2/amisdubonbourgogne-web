package be.amisdubonbourgogne.web.wine.dto;

import be.amisdubonbourgogne.web.wine.entity.Region;
import be.amisdubonbourgogne.web.wine.entity.WineColor;


import java.math.BigDecimal;
import java.time.Instant;

public record WineAdminResponse(
        Long id,
        String slug,
        String producer,
        String appellation,
        String cuvee,
        Region region,
        WineColor color,
        Integer vintage,
        BigDecimal volumeCl,
        BigDecimal price,
        String grapes,
        String description,
        boolean available,
        boolean featured,
        Instant createdAt,
        Instant updatedAt
) {
}
