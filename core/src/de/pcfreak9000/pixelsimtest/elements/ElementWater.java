package de.pcfreak9000.pixelsimtest.elements;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.Element;
import de.pcfreak9000.pixelsimtest.ElementMatrix;
import de.pcfreak9000.pixelsimtest.ElementState;
import de.pcfreak9000.pixelsimtest.IColorDef;
import de.pcfreak9000.pixelsimtest.PixelSimTest;

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
            state = mat.convertState(state.getX(), state.getY(), PixelSimTest.watervapor);
        }
    }
}
