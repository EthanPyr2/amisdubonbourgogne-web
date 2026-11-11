package be.amisdubonbourgogne.web.wine.dto;

import be.amisdubonbourgogne.web.region.dto.RegionResponse;
import be.amisdubonbourgogne.web.wine.entity.WineColor;


import java.math.BigDecimal;

public record WinePublicResponse(
        String slug,
        String producer,
        String appellation,
        String cuvee,
        RegionResponse region,
        WineColor color,
        Integer vintage,
        BigDecimal price,
        String grapes,
        String description
) {
}
