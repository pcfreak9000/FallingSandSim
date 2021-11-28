package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ElementState {
    
    private Element element;
    int x, y;
    
    private Color color;
    //possibly move that into a subclass?
    private Vector2 velocity;
    private Vector2 acceleration;
    
    public float ahyes;
    
    public ElementState(int x, int y, Element element) {
        this.x = x;
        this.y = y;
        this.element = element;
        this.color = element.c;
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
    }
    
    public void update(ElementMatrix matrix) {
        this.element.update(this, matrix);
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public Vector2 getAcceleration() {
        return acceleration;
    }
    
    public Color getColor() {
        return color;
    }
    
    public Element getElement() {
        return element;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public float getFriction() {
        return element.getFriction();
    }

    public void applyForces(ElementMatrix elementMatrix) {
        this.element.applyForces(this, elementMatrix);
    }
}
