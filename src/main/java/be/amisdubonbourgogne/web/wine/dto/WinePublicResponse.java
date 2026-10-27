package be.amisdubonbourgogne.web.wine.dto;

import be.amisdubonbourgogne.web.wine.entity.Region;
import be.amisdubonbourgogne.web.wine.entity.WineColor;


import java.math.BigDecimal;

public record WinePublicResponse(
        String slug,
        String producer,
        String appellation,
        String cuvee,
        Region region,
        WineColor color,
        Integer vintage,
        BigDecimal price,
        String grapes,
        String description
) {
}
