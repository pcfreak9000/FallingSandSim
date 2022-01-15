package de.pcfreak9000.pixelsimtest;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.IntArray;

public class ChunkLoader2 {
    private static int max(IntArray array) {
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < array.size; i++) {
            max = Math.max(max, array.items[i]);
        }
        return max;
    }
    
    public static class ChunkData {
        private Chunk chunk;
        private int levelActual;
        private IntArray levels = new IntArray();
        
        public Chunk getChunk() {
            return chunk;
        }
        
        public int getLevel() {
            return levelActual;
        }
    }
    
    private Map<IntCoordKey, ChunkData> chunks = new HashMap<>();
    
    public ChunkData get(int x, int y) {
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        return d;
    }
    
    public void removeLevel(int x, int y, int level) {
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        if (d == null)
            return;
        boolean b = d.levels.removeValue(level);
        if (!b)
            return;
        if (d.levels.isEmpty()) {
            chunks.remove(key);
            //save chunk, schedule unloading, whatever
        } else {
            d.levelActual = max(d.levels);
        }
        for (Direction dir : Direction.MOORE_NEIGHBOURS) {
            removeLevel(x + dir.dx, y + dir.dy, level - 1);
        }
    }
    
    public void addLevel(int x, int y, int level) {
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        if (d == null) {
            d = new ChunkData();
            chunks.put(key, d);
            //load chunk
        }
        d.levels.add(level);
        d.levelActual = level > d.levelActual ? level : max(d.levels);
        for (Direction dir : Direction.MOORE_NEIGHBOURS) {
            addLevel(x + dir.dx, y + dir.dy, level - 1);
        }
    }
}
