package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import pcs.AComponent;

public class ElementState {
    
    private Array<AComponent> comps = new Array<>();
    
    private Element element;
    int x, y;
    
    public boolean moving;
    
    public Color color;
    
    protected int lastframe = 0;
    
    private Vector2 velocity;
    private Vector2 acceleration;
    
    public float timepart;
    
    public final float heattransfercoefficient = 1000;
    public final float specificheat = 0.1f;
    public float heatproduction;
    public float heat;
    
    public float getTemperature() {
        return heat / specificheat;
    }
    
    public ElementState(int x, int y, Element element) {
        this.x = x;
        this.y = y;
        this.element = element;
        this.color = element.c;
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
    }
    
    public void setComponent(AComponent comp) {
        comps.insert(comp.id, comp);
    }
    
    public AComponent getComponent(int id) {
        return id >= comps.size ? null : comps.get(id);
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public Vector2 getAcceleration() {
        return acceleration;
    }
    
    public void update(ElementMatrix matrix, int frame) {
        this.element.update(this, matrix, frame);
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
    
    public float getInternalFriction() {
        return element.getInternalFriction();
    }
    
    public float getPulledAlongChance() {
        return element.getPulledAlongChance();
    }
    
    public float getPulledAlongStrength() {
        return element.getPulledAlongStrength();
    }
    
}
