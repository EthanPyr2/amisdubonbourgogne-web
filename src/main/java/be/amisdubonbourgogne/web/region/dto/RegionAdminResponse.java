package be.amisdubonbourgogne.web.region.dto;

public record RegionAdminResponse(
        Long id,
        String slug,
        String name,
        int displayOrder,
        boolean active
) {
}
