package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

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
