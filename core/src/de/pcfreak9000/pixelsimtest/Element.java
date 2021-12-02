package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public abstract class Element {
    
    public float density;
    public boolean isFixed;
    
    public Color c;
    
    public void update(ElementState state, ElementMatrix mat) {
        
    }
    
    public float getStartResistance(ElementMatrix mat, ElementState state, Direction dir) {
        return 0;
    }
    
    public ElementState createElementState(int x, int y) {
        return new ElementState(x, y, this);
    }
    
    public float getFriction() {
        return 0;
    }

    public float getStickyness() {
        return 0;
    }
}
