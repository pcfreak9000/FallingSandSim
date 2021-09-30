package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.ElementMatrix.Direction;
import de.pcfreak9000.pixelsimtest.ElementMatrix.State;

public class Water extends Element {
    public Water() {
        setColor(Color.BLUE);
        density = 1;
    }
    
    private final Color color = new Color(0, 0.1f, 0.99f, 1);
    
    private static final Direction[] MOVEMENT = { Direction.Down, Direction.DownRight, Direction.DownLeft, Direction.Right, Direction.Left };
    private static final boolean[] CHECK_DENSITY = { true, false, false, false, false };
    
    @Override
    public Color getColor() {
        return Math.random() > 0.5 ? color : Color.BLUE;
    }
    
    @Override
    public void update(State s, ElementMatrix mat) {
        for (int i = 0; i < 10; i++) {
            if (!mat.move(s, CHECK_DENSITY, MOVEMENT)) {
                break;
            }
        }
    }
    
}
