package be.amisdubonbourgogne.web.wine.entity;

import be.amisdubonbourgogne.web.region.entity.Region;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WineColor color;
    private Integer vintage;
    // Formats de bouteille (volume + prix), du plus petit au plus grand
    @OneToMany(mappedBy = "wine", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("volumeCl ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<WineFormat> formats = new ArrayList<>();
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
