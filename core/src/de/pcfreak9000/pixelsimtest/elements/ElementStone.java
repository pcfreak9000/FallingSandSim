package de.pcfreak9000.pixelsimtest.elements;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.Element;
import de.pcfreak9000.pixelsimtest.IColorDef;

public class ElementStone extends Element {
    public ElementStone() {
        this.colorDef = IColorDef.cnst(Color.GRAY);
        this.fixed = true;
        this.density = 10;
        this.friction = 0.5f;
        this.frictionInternal = 0.5f;
        this.heatTransferCoefficient = 1000;
    }
    
}
