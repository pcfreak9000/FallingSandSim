package de.pcfreak9000.pixelsimtest;

public class ElementAir extends Element {
    public ElementAir() {
        this.density = 1;
    }
    
    @Override
    public float getFriction() {
        return 0.01f;
    }
}
