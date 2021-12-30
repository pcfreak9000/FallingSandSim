package de.pcfreak9000.pixelsimtest;

import java.util.stream.IntStream;

import com.badlogic.gdx.utils.IntArray;

public class Chunk {
    
    public static final int CHUNK_SIZE = 64;
    private static int[] xShuffled = shuffledIndizes(CHUNK_SIZE);
    private static int[] yShuffled = shuffledIndizes(CHUNK_SIZE);
    
    public static int toChunk(int x) {
        return x / CHUNK_SIZE;
    }
    
    public static int[] shuffledIndizes(int len) {
        IntArray ob = new IntArray(IntStream.range(0, len).toArray());
        ob.shuffle();
        return ob.items;
    }
    
    private ElementState[][] matrix = new ElementState[CHUNK_SIZE][CHUNK_SIZE];
    private int chunkX, chunkY;
    
    public Chunk(int cx, int cy) {
        this.chunkX = cx;
        this.chunkY = cy;
    }
    
    void update(ElementMatrix mat, int frame) {
        for (int x : xShuffled) {
            for (int y : yShuffled) {
                ElementState t = matrix[x][y];
                if (t != null) {
                    t.updateChecked(mat, frame);
                }
            }
        }
    }
    
    ElementState get(int x, int y) {
        return matrix[x - chunkX][y - chunkY];
    }
    
    void set(int x, int y, ElementState state) {
        matrix[x - chunkX][y - chunkY] = state;
        state.x = x;
        state.y = y;
    }
}
