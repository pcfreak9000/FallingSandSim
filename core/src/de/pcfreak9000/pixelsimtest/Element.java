package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.Gdx;

public abstract class Element {
    
    protected float density = 1;
    protected float friction = 0;
    protected float frictionInternal = 0;
    protected float pulledAlongChance = 0;
    protected float pulledAlongStrength = 0;
    protected float heatTransferCoefficient = 0;
    protected float specificHeat = 1;
    protected float heatProduction = 0;
    protected boolean fixed = false;
    protected boolean fluidLike = false;
    protected IColorDef colorDef = IColorDef.NONE;
    
    public void update(ElementState state, ElementMatrix mat, int frame) {
        float dt = Gdx.graphics.getDeltaTime();
        if (state.isFixed()) {
            ElementStateThermo.apply(state, mat, dt);
        } else {
            ElementStateKinematics.apply(state, mat, dt);//includes thermo stuff
        }
    }
    
    public ElementState createElementState(int x, int y) {
        return new ElementState(x, y, this);
    }
    
    public float getDensity() {
        return density;
    }
    
    public float getFriction() {
        return friction;
    }
    
    public float getInternalFriction() {
        return frictionInternal;
    }
    
    public float getPulledAlongChance() {
        return pulledAlongChance;
    }
    
    public float getPulledAlongStrength() {
        return pulledAlongStrength;
    }
    
    public float getHeatTransferCoefficient() {
        return heatTransferCoefficient;
    }
    
    public float getSpecificHeat() {
        return specificHeat;
    }
    
    public float getHeatProduction() {
        return heatProduction;
    }
    
    public boolean isFixed() {
        return fixed;
    }
    
    public boolean isFluidLike() {
        return fluidLike;
    }
    
    public IColorDef getColorDef() {
        return colorDef;
    }
}
