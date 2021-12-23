package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public class ElementWater extends Element {
    public ElementWater() {
        this.colorDef = IColorDef.cnst(Color.BLUE);
        this.fluidLike = true;
        this.density = 1.0001f;
        this.friction = 0.05f;
        this.frictionInternal = 0;
        this.heatTransferCoefficient = 1000;
        this.specificHeat = 0.1f;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat, int frame) {
        super.update(state, mat, frame);
        if (state.getTemperature() > 200) {
            //state.heat -= 5 / state.specificheat;
            state = mat.convertState(state.x, state.y, PixelSimTest.watervapor);
        }
    }
}
