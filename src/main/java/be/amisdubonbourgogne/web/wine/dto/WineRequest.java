package be.amisdubonbourgogne.web.wine.dto;

import be.amisdubonbourgogne.web.wine.entity.WineColor;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;


import javax.swing.plaf.synth.Region;
import java.math.BigDecimal;

public record WineRequest(    @NotBlank @Size(max = 150) String producer,
                              @NotBlank @Size(max = 120) String appellation,
                              @NotNull Region region,
                              @NotNull WineColor color,
                              @Min(1900) @Max(2100) Integer vintage,
                              @NotNull @Positive BigDecimal price,
                              String grapes,
                              String description) {}
