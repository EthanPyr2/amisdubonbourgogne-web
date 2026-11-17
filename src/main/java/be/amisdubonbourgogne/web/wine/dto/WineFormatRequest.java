package be.amisdubonbourgogne.web.wine.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WineFormatRequest(
        @NotNull @Positive @Digits(integer = 4, fraction = 1) BigDecimal volumeCl,
        @NotNull @Positive @Digits(integer = 6, fraction = 2) BigDecimal price
) {
}
