package be.amisdubonbourgogne.web.wine.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.cache.spi.Region;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Data

public class Wine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    @Column(nullable = false, length = 150)
    private String producer;
    @Column(nullable = false, length = 120)
    private String appellation;
    @Column(length = 150)
    private String cuvee;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Region region;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WineColor color;
    private Integer vintage;
    @Column(nullable = false)
    private BigDecimal volumeCl;
    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;
    @Column(length = 150)
    private String grapes;
    @Column(columnDefinition = "TEXT")
    private String description;
    private boolean available = true;
    private boolean featured = false;
    @Column(nullable = false,unique = true,length = 200)
    private String slug;
    @CreationTimestamp private Instant createdAt;
    @UpdateTimestamp private Instant updateAt;

}
