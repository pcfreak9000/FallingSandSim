package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ElementState {
    
    private Element element;
    protected int lastframe = 0;
    
    //hmm
    public Color color;
    
    private Vector2 velocity;
    private Vector2 acceleration;
    
    int x, y;
    float timepart;
    
    private float heat;
    
    public ElementState(int x, int y, Element element) {
        this.x = x;
        this.y = y;
        this.element = element;
        this.color = element.getColorDef().get(x, y);
        this.velocity = new Vector2();
        this.acceleration = new Vector2();
    }
    
    public Vector2 getVelocity() {
        return velocity;
    }
    
    public Vector2 getAcceleration() {
        return acceleration;
    }
    
    public boolean alreadyUpdated(int currentframe) {
        int old = lastframe;
        lastframe = currentframe;
        return old == currentframe;
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
    
    public void setHeat(float heat) {
        this.heat = heat;
    }
    
    public float getHeat() {
        return heat;
    }
    
    public float getTemperature() {
        return heat / getSpecificHeat();
    }
    
    public float getDensity() {
        return element.getDensity();
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
    
    public float getHeatTransferCoefficient() {
        return element.getHeatTransferCoefficient();
    }
    
    public float getSpecificHeat() {
        return element.getSpecificHeat();
    }
    
    public float getHeatProduction() {
        return element.getHeatProduction();
    }
    
    public boolean isFixed() {
        return element.isFixed();
    }
    
    public boolean isFluidLike() {
        return element.isFluidLike();
    }
}
