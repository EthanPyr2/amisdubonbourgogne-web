package be.amisdubonbourgogne.web.region.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RegionRequest(
        @NotBlank @Size(max = 80) String name,
        @NotNull @PositiveOrZero Integer displayOrder,
        Boolean active
) {
}
