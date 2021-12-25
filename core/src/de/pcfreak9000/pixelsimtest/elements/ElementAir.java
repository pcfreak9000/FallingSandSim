package de.pcfreak9000.pixelsimtest.elements;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.Element;
import de.pcfreak9000.pixelsimtest.IColorDef;

public class ElementAir extends Element {
    public ElementAir() {
        this.colorDef = IColorDef.cnst(Color.GREEN);
        this.fluidLike = true;
        this.density = 1;
        this.friction = 0.01f;
        this.heatTransferCoefficient = 1000;
        this.specificHeat = 0.1f;
    }
    
}
