package de.pcfreak9000.pixelsimtest.elements;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.Element;
import de.pcfreak9000.pixelsimtest.ElementMatrix;
import de.pcfreak9000.pixelsimtest.ElementState;
import de.pcfreak9000.pixelsimtest.IColorDef;
import de.pcfreak9000.pixelsimtest.PixelSimTest;

public class ElementPlasma extends Element {
	public ElementPlasma() {
		this.colorDef = IColorDef.cnst(Color.PINK);
		this.fluidLike = true;
		this.density = 0.3f;
		this.friction = 0.01f;
		this.frictionInternal = 0;
		this.heatTransferCoefficient = 1000;
		this.specificHeat = 0.1f;
	}

	@Override
	public void update(ElementState state, ElementMatrix mat, int frame) {
		super.update(state, mat, frame);
		if (state.getTemperature() < 500) {
			state = mat.convertState(state.getX(), state.getY(), PixelSimTest.watervapor);
			// state.heat += 5 / state.specificheat;
		}
	}

}
