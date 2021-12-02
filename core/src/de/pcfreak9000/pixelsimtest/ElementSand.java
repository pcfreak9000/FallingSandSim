package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementSand extends ElementWater {
    
    public ElementSand() {
        this.c = Color.YELLOW;
        this.density = 1.11f;
    }
    
    @Override
    public float getFriction() {
        return 0.4f;
    }
}
