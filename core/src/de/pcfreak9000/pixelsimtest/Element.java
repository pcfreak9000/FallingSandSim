package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.ElementMatrix.State;

public class Element {
    
    private Color color;
    public float density;
    public float freq;
    public boolean isFixed;
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color c) {
        this.color = c;
    }
    
    public void update(State s, ElementMatrix mat) {
        
    }
    
}
