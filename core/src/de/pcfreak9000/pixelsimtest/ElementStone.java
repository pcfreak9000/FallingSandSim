package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementStone extends Element {
    public ElementStone() {
        this.isFixed = true;
        this.density = 10;
        this.c = Color.GRAY;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        if(state.lastframe == frame) {
            return;
        }
        state.lastframe = frame;
        ThermoKinematics.apply(state, mat);
    }
    
    @Override
    public ElementState createElementState(int x, int y) {
        ElementState s = super.createElementState(x, y);
        s.heatproduction = 10;
        return s;
    }
    
    @Override
    public float getFriction() {
        return 0.5f;
    }
    
}
