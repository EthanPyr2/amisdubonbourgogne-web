package be.amisdubonbourgogne.web.wine.dto;

import java.math.BigDecimal;

/** Un format renvoyé au front : ex. { "volumeCl": 75.0, "price": 32.00 } */
public record WineFormatResponse(
        BigDecimal volumeCl,
        BigDecimal price
) {
}
