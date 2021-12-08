package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementWater extends Element {
    public ElementWater() {
        this.density = 10;
        this.c = Color.BLUE;
        this.fluid = true;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        if(state.lastframe == frame) {
            return;
        }
        state.lastframe = frame;
        ElementStateKinematics.apply(state, mat);
    }
    
    @Override
    public float getFriction() {
        return 0.05f;
    }
}
