package be.amisdubonbourgogne.web.wine.entity;

import lombok.Getter;

@Getter
public enum Region {
    BOURGOGNE ("Bourgogne"),
    BEAUJOLAIS ("Beaujolais"),
    RHONENORD ("Vallée du Rhône Nord"),
    RHONESUD ("Vallée du Rhône Sud"),
    CHAMPAGNE ("Champagne"),
    CAVA ("CAVA"),
    CREMANT ("Crémant"),
    LOIRE ("Loire"),
    LGDCROUSSILLON ("Languedoc Roussillon"),
    BORDEAUX ("Bordeaux"),
    PAYS ("Vin de pays"),
    JURA ("Jura"),
    ALSACE ("Alsace"),
    AUTRE ("Autres");

    private final String label;
    Region(String label) {this.label = label;}
}
