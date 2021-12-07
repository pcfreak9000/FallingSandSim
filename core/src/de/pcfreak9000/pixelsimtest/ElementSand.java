package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementSand extends ElementWater {
    
    public ElementSand() {
        this.c = Color.YELLOW;
        this.density = 0.9f;
        this.fluid = false;
    }
    
    @Override
    public float getFriction() {
        return 0.9f;
    }
    
    @Override
    public float getPulledAlongChance() {
        return 0.7f;
    }
    
    @Override
    public float getPulledAlongStrength() {
        return 0.3f;
    }
}
