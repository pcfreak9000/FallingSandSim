package de.pcfreak9000.pixelsimtest;

public abstract class Element {
    
    public float density;
    public boolean isFixed;
    
    public void update(ElementState state, ElementMatrix mat) {
        
    }
    
    public ElementState createElementState(int x, int y) {
        return new ElementState(x, y, this);
    }
}
