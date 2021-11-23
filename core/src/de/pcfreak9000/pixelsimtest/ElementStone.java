package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementStone extends Element {
    public ElementStone() {
        this.isFixed = true;
        this.density = 10;
        this.c = Color.GRAY;
    }
    
    @Override
    public float getFriction() {
        return 1f;
    }
}
