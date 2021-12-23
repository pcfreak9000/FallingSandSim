package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;

public interface IColorDef {
    
    public static IColorDef cnst(Color c) {
        return (x, y) -> c;
    }
    
    public static final IColorDef NONE = (x, y) -> null;
    
    Color get(int x, int y);
    
}
