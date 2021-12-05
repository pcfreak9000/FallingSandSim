package de.pcfreak9000.pixelsimtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Array;

public class ElementMatrix {
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
    }
    private static final Texture WHITE;
    
    public static final int SIZE = 300;
    
    private ElementState[][] matrix = new ElementState[SIZE][SIZE];
    private Array<ElementState> activeStates = new Array<>();
    
    private List<Integer> indizes = new ArrayList<>();
    private List<Integer> indizes2 = new ArrayList<>();
    
    private Element base;
    
    private RandomXS128 random = new RandomXS128();
    
    public ElementMatrix(Element base) {
        this.base = base;
        for (int i = 0; i < SIZE; i++) {
            indizes.add(i);
        }
        Collections.shuffle(indizes);
        for (int i = 0; i < SIZE; i++) {
            indizes2.add(i);
        }
        Collections.shuffle(indizes2);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = base.createElementState(i, j);
            }
        }
    }
    
    public void update() {
        Collections.shuffle(indizes);
        Collections.shuffle(indizes2);
        for (int j : indizes2) {
            for (int i : indizes) {
                ElementState t = matrix[i][j];
                if (t != null) {
                    t.update(this);
                }
            }
        }
    }
    
    public double random() {
        return random.nextDouble();
    }
    
    public void switchStates(ElementState s0, ElementState s1) {
        if (s0 == s1) {
            return;
        }
        switchStates(s0.x, s0.y, s1.x, s1.y);
    }
    
    public void switchStates(int x0, int y0, int x1, int y1) {
        ElementState s0 = matrix[x0][y0];
        ElementState s1 = matrix[x1][y1];
        if (s0.x != x0 || s0.y != y0 || s1.x != x1 || s1.y != y1) {
            throw new RuntimeException();
        }
        if (s0.getElement().isFixed || s1.getElement().isFixed) {
            throw new RuntimeException();
        }
        matrix[x0][y0] = s1;
        s1.x = x0;
        s1.y = y0;
        matrix[x1][y1] = s0;
        s0.x = x1;
        s0.y = y1;
    }
    
    public void killState(int x, int y) {
        ElementState s = matrix[x][y];
        matrix[x][y] = base.createElementState(x, y);
        if (s != null) {
            activeStates.removeValue(s, true);
        }
    }
    
    public void createState(int x, int y, Element e) {
        matrix[x][y] = e.createElementState(x, y);
        activeStates.add(matrix[x][y]);
    }
    
    public ElementState getState(int x, int y) {
        if (checkBounds(x, y)) {
            return matrix[x][y];
        }
        return null;
    }
    
    public boolean hasElement(int x, int y) {
        return getState(x, y).getElement() != base;
    }
    
    public void createCircle(int x, int y, int radius, Element e) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int ax = x + i;
                int ay = y + j;
                if (i * i + j * j < radius * radius) {
                    if (checkBounds(ax, ay) && !hasElement(ax, ay)) {
                        createState(ax, ay, e);
                    }
                }
            }
        }
    }
    
    public boolean checkBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < SIZE && y < SIZE;
    }
    
    public void render(SpriteBatch batch) {
        for (ElementState t : activeStates) {
            batch.setColor(t.getColor());
            //            Vector2 v = t.getVelocity();
            //            if(v.len2()>100) {
            //                batch.setColor(t.getColor());   
            //            }else if(v.len2()>0&&v.len2()<=100) {
            //                batch.setColor(Color.CORAL);
            //            }else {
            //                batch.setColor(Color.RED);
            //            }
            batch.draw(WHITE, t.getX(), t.getY(), 1, 1);
        }
    }
}
