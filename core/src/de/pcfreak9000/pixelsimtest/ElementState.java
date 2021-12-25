package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ElementState {
    
    private Element element;
    
    //can the color system be improved? a Color object however seems to be too much
    private float colorPacked;
    
    //Do v and a need to be vectors?
    private Vector2 velocity;
    private Vector2 acceleration;
    
    private float heat;
    
    protected int lastframe = 0;
    
    protected float timepart;
    int x, y;
    
    public ElementState(int x, int y, Element element) {
        this.x = x;
        this.y = y;
        this.element = element;
        Color color = element.getColorDef().getColor(x, y);
        this.colorPacked = color == null ? Float.NaN : color.toFloatBits();
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
    
    public float getColorPacked() {
        return colorPacked;
    }
    
    public boolean hasColor() {
        return !Float.isNaN(colorPacked);
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
