package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementWater extends Element {
    public ElementWater() {
        this.density = 1.0001f;
        this.c = Color.BLUE;
        this.fluid = true;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        if (state.lastframe == frame) {
            return;
        }
        state.lastframe = frame;
        ElementStateKinematics.apply(state, mat);
        if (state.getTemperature() > 200 && !(this instanceof ElementSand)) {
            //state.heat -= 5 / state.specificheat;
            state = mat.convertState(state.x, state.y, PixelSimTest.watervapor);
        }
    }
    
    @Override
    public float getFriction() {
        return 0.05f;
    }
}
