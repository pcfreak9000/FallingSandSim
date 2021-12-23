package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementSand extends Element {
    
    public ElementSand() {
        this.colorDef = IColorDef.cnst(Color.YELLOW);
        this.density = 50;
        this.friction = 0.9f;
        this.frictionInternal = 0.9f;
        this.heatProduction = 10;
        this.pulledAlongChance = 0.07f;
        this.pulledAlongStrength = 0.3f;
        this.heatTransferCoefficient = 1000;
        this.specificHeat = 0.1f;
    }
    
}
