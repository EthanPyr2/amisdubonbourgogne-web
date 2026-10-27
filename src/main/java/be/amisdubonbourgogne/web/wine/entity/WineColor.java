package be.amisdubonbourgogne.web.wine.entity;

import lombok.Getter;

@Getter
public enum WineColor {
    ROUGE ("Rouge"),
    BLANC ("blanc"),
    ROSE ("Rosé"),
    EFFERVESCENT ("Efferverscent"),
    LIQUOREUX("Moelleux & liquoreux"),
    AUTRE ("Autre");
    private final String label;
    WineColor(String label) {this.label = label;
    }
}
