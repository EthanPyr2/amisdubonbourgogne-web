package be.amisdubonbourgogne.web.wine.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class WineFormat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id", nullable = false)
    private Wine wine;

    @Column(nullable = false, precision = 5, scale = 1)
    private BigDecimal volumeCl;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;
}
