package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.ElementMatrix.Direction;
import de.pcfreak9000.pixelsimtest.ElementMatrix.State;

public class Gas extends Element {
    public Gas() {
        density = 0.1f;
    }
    
    @Override
    public Color getColor() {
        return Color.CORAL;
    }
    
    private static final Direction[] MOVEMENT = { Direction.Up, Direction.Right, Direction.Left };
    private static final boolean[] CHECK_DENSITY = { true, false, false, false, false };
    
    @Override
    public void update(State s, ElementMatrix mat) {
        for (int i = 0; i < 1; i++) {
            if (!mat.move(s, CHECK_DENSITY, MOVEMENT)) {
                break;
            }
        }
    }
    
}
