package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementWater extends Element {
    public ElementWater() {
        this.density = 49f;
        this.c = Color.BLUE;
        this.fluid = true;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat) {
        ElementStateKinematics.apply(state, mat);
    }
    
    @Override
    public float getFriction() {
        return 0.05f;
    }
}
