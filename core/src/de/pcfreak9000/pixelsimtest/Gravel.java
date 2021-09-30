package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

import de.pcfreak9000.pixelsimtest.ElementMatrix.Direction;
import de.pcfreak9000.pixelsimtest.ElementMatrix.State;

public class Gravel extends Element {
    public Gravel() {
        setColor(Color.GRAY);
        density = 10f;
    }
    
    @Override
    public Color getColor() {
        return Math.random() > 0.5 ? new Color(0.45f, 0.45f, 0.45f, 1) : Color.GRAY;
    }
    
    @Override
    public void update(State s, ElementMatrix mat) {
        boolean b = mat.move(s, Direction.Down, true) || mat.move(s, Direction.DownLeft, true)
                || mat.move(s, Direction.DownRight, true);
    }
}
