package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementAir extends Element {
    public ElementAir() {
        this.density = 1;
        this.fluid = true;
        this.c = Color.GREEN;
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
        return 0.01f;
    }
}
