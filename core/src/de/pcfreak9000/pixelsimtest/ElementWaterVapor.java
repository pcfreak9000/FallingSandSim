package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementWaterVapor extends Element {
    public ElementWaterVapor() {
        this.colorDef = IColorDef.cnst(Color.CYAN);
        this.fluidLike = true;
        this.density = 0.5f;
        this.friction = 0.01f;
        this.frictionInternal = 0;
        this.heatTransferCoefficient = 1000;
        this.specificHeat = 0.1f;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        super.update(state, mat, frame);
        if (state.getTemperature() < 100) {
            state = mat.convertState(state.x, state.y, PixelSimTest.water);
            //state.heat += 5 / state.specificheat;
        }
    }
    
}
