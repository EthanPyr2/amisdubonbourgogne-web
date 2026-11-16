package be.amisdubonbourgogne.web.wine.dto;

import be.amisdubonbourgogne.web.wine.entity.WineColor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record WineRequest(    @NotBlank @Size(max = 150) String producer,
                              @NotBlank @Size(max = 120) String appellation,
                              @NotBlank @Size(max = 80) String region,
                              @NotNull WineColor color,
                              @Min(1900) @Max(2100) Integer vintage,
                              String grapes,
                              String description,
                              String cuvee,
                              // Au moins un format ; @Valid vérifie aussi chaque format de la liste
                              @NotEmpty List<@Valid @NotNull WineFormatRequest> formats
) {}
