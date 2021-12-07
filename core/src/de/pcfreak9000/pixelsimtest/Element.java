package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public abstract class Element {
    
    public float density;
    public boolean isFixed;
    public boolean fluid;
    
    public Color c;
    
    public void update(ElementState state, ElementMatrix mat) {
        
    }
    
    public ElementState createElementState(int x, int y) {
        return new ElementState(x, y, this);
    }
    
    public float getFriction() {
        return 0;
    }
    
    public float getInternalFriction() {
        return getFriction();
    }
    
    public float getPulledAlongChance() {
        return 0;
    }
    
    public float getPulledAlongStrength() {
        return 0;
    }
}
