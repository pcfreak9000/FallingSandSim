package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;

public class ElementMatrix {
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
    }
    private static final Texture WHITE;
    
    public static void dispose() {
        WHITE.dispose();
    }
    
    public static final int SIZE = 500;
    
    private static final int[] xs = Chunk.shuffledIndizes(Chunk.toChunk(SIZE) + 1);
    private static final int[] ys = Chunk.shuffledIndizes(Chunk.toChunk(SIZE) + 1);
    
    private Chunk[][] chunks = new Chunk[Chunk.toChunk(SIZE) + 1][Chunk.toChunk(SIZE) + 1];
    
    private Element base;
    private int frame = 0;
    
    private RandomXS128 random = new RandomXS128();
    
    public ElementMatrix(Element base) {
        this.base = base;
        for (int i = 0; i < chunks.length; i++) {
            for (int j = 0; j < chunks[i].length; j++) {
                chunks[i][j] = new Chunk(i * Chunk.CHUNK_SIZE, j * Chunk.CHUNK_SIZE);
            }
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                createState(i, j, base);
            }
        }
    }
    
    public void update() {
        frame++;
        for (int j : ys) {
            for (int i : xs) {
                chunks[i][j].update(this, frame);
            }
        }
    }
    
    public double random() {
        return random.nextDouble();
    }
    
    public Element base() {
        return base;
    }
    
    public void switchStates(ElementState s0, ElementState s1) {
        if (s0 == s1) {
            return;
        }
        switchStates(s0.x, s0.y, s1.x, s1.y);
    }
    
    private ElementState get(int x, int y) {
        int cx = Chunk.toChunk(x);
        int cy = Chunk.toChunk(y);
        return chunks[cx][cy].get(x, y);
    }
    
    private void set(int x, int y, ElementState state) {
        int cx = Chunk.toChunk(x);
        int cy = Chunk.toChunk(y);
        chunks[cx][cy].set(x, y, state);
    }
    
    public void switchStates(int x0, int y0, int x1, int y1) {
        ElementState s0 = get(x0, y0);
        ElementState s1 = get(x1, y1);
        if (s0.x != x0 || s0.y != y0 || s1.x != x1 || s1.y != y1) {
            throw new RuntimeException("Mismatch between internal- and matrixcoordinates");
        }
        if (s0.isFixed() || s1.isFixed()) {
            throw new RuntimeException("Can't switch fixed states");
        }
        set(x0, y0, s1);
        set(x1, y1, s0);
    }
    
    public void killState(int x, int y) {
        set(x, y, base.createElementState(x, y));
    }
    
    public void createState(int x, int y, Element e) {
        set(x, y, e.createElementState(x, y));
    }
    
    public ElementState convertState(int x, int y, Element e) {
        ElementState current = get(x, y);
        ElementState newstate = e.createElementState(x, y);
        newstate.timepart = current.timepart;
        newstate.setHeat(current.getTemperature() * newstate.getSpecificHeat());
        set(x, y, newstate);
        return newstate;
    }
    
    public ElementState getState(int x, int y) {
        if (checkBounds(x, y)) {
            return get(x, y);
        }
        return null;
    }
    
    public boolean hasElement(int x, int y) {
        ElementState s = getState(x, y);
        if (s == null) {
            return false;
        }
        return s.getElement() != base;
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
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ElementState t = getState(i, j);
                if (t.hasColor()) {
                    batch.setPackedColor(t.getColorPacked());
                    batch.draw(WHITE, t.getX(), t.getY(), 1, 1);
                }
            }
        }
    }
}
