package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementWaterVapor extends Element {
    public ElementWaterVapor() {
        this.density = 0.5f;
        this.fluid = true;
        this.c = Color.CYAN;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        if (state.lastframe == frame) {
            return;
        }
        state.lastframe = frame;
        ElementStateKinematics.apply(state, mat);
        if (state.getTemperature() < 100) {
            state = mat.convertState(state.x, state.y, PixelSimTest.water);
            //state.heat += 5 / state.specificheat;
        }
    }
    
    @Override
    public float getFriction() {
        return 0.01f;
    }
}
