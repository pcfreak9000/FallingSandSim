package de.pcfreak9000.pixelsimtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class ElementMatrix {
    
    public static enum Direction {
        Up(0, 1), Down(0, -1), Left(-1, 0), Right(1, 0), UpLeft(-1, 1), UpRight(1, 1), DownLeft(-1, -1),
        DownRight(1, -1);
        
        public final int dx;
        public final int dy;
        
        private Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
        
        public Direction opposite() {
            switch (this) {
            case Down:
                return Up;
            case DownLeft:
                return UpRight;
            case DownRight:
                return UpLeft;
            case Left:
                return Right;
            case Right:
                return Left;
            case Up:
                return Down;
            case UpLeft:
                return DownRight;
            case UpRight:
                return DownLeft;
            default:
                return null;
            }
        }
    }
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
    }
    private static final Texture WHITE;
    
    public static final int SIZE = 150;
    
    private static class ElementType {
        private State state;
        private Element element;
    }
    
    public static class State {
        private int x, y;
        public Color c;
        public boolean active;
        
        public int x() {
            return x;
        }
        
        public int y() {
            return y;
        }
    }
    
    private ElementType[][] matrix = new ElementType[SIZE][SIZE];
    private Array<ElementType> activeStates = new Array<>();
    
    private List<Integer> indizes = new ArrayList<>();
    
    public ElementMatrix() {
        for (int i = 0; i < SIZE; i++) {
            indizes.add(i);
        }
        Collections.shuffle(indizes);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                matrix[i][j] = new ElementType();
                matrix[i][j].state = new State();
                State s = matrix[i][j].state;
                s.active = true;
                s.x = i;
                s.y = j;
            }
        }
    }
    
    public void update() {
        //Collections.shuffle(indizes);
        for (int j = 0; j < SIZE; j++) {
            for (int i : indizes) {
                ElementType t = matrix[i][j];
                if (t.element != null) {
                    t.element.update(t.state, this);
                }
            }
        }
    }
    
    public boolean move(State s, boolean[] checkdensity, Direction[] dirsToGo) {
        for (int i = 0; i < dirsToGo.length; i++) {
            if (move(s, dirsToGo[i], checkdensity[i])) {
                return true;
            }
        }
        return false;
    }
    
    public boolean move(State s, Direction dir, boolean checkdensity) {
        if (!hasElement(s.x, s.y)) {
            throw new IllegalStateException();
        }
        int tx = s.x + dir.dx;
        int ty = s.y + dir.dy;
        if (!checkBounds(tx, ty)) {
            return false;
        }
        ElementType net = matrix[tx][ty];
        ElementType cur = matrix[s.x][s.y];
        if (net.element == null
                || (checkdensity && !net.element.isFixed && net.element.density < cur.element.density)) {
            matrix[tx][ty] = cur;
            matrix[s.x][s.y] = net;
            net.state.x = s.x;
            net.state.y = s.y;
            s.x = tx;
            s.y = ty;
            return true;
        }
        return false;
    }
    
    public void setElement(int x, int y, Element e) {
        matrix[x][y].element = e;
        if (e != null) {
            matrix[x][y].state.c = e.getColor();
        }
        activeStates.add(matrix[x][y]);
    }
    
    public Element getElement(int x, int y) {
        if (checkBounds(x, y)) {
            return matrix[x][y].element;
        }
        return null;
    }
    
    public boolean hasElement(int x, int y) {
        return getElement(x, y) != null;
    }
    
    public void setCircle(int x, int y, int radius, Element e) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int ax = x + i;
                int ay = y + j;
                if (i * i + j * j < radius * radius) {
                    if (checkBounds(ax, ay)) {
                        setElement(ax, ay, e);
                    }
                }
            }
        }
    }
    
    public boolean checkBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < SIZE && y < SIZE;
    }
    
    public void render(SpriteBatch batch) {
        for (ElementType t : activeStates) {
            batch.setColor(t.state.c);
            batch.draw(WHITE, t.state.x, t.state.y, 1, 1);
        }
    }
}
