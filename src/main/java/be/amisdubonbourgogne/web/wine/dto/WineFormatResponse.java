package be.amisdubonbourgogne.web.wine.dto;

import java.math.BigDecimal;

public record WineFormatResponse(
        BigDecimal volumeCl,
        BigDecimal price
) {
}
