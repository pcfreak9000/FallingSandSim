package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class ElementStone extends Element {
    public ElementStone() {
        this.isFixed = true;
        this.density = 10;
        this.c = Color.GRAY;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        if (state.lastframe == frame) {
            return;
        }
        state.lastframe = frame;
        ElementStateThermo.produceHeat(state, mat, Gdx.graphics.getDeltaTime());
        ElementStateThermo.spreadHeat(state, mat, Gdx.graphics.getDeltaTime());
    }
    
    @Override
    public ElementState createElementState(int x, int y) {
        ElementState s = super.createElementState(x, y);
        s.heattransfercoefficient = 1000;
        s.specificheat = 1;
        return s;
    }
    
    @Override
    public float getFriction() {
        return 0.5f;
    }
    
}
