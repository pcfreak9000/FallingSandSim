package de.pcfreak9000.pixelsimtest;

import java.util.HashMap;
import java.util.Map;

public class ChunkLoader {
    
    private static class ChunkData {
        private Chunk chunk;
        private int levelActual;
        private Map<Object, LevelData> levels = new HashMap<>();
        //private ObjectIntMap<Object> levels = new ObjectIntMap<>();
    }
    
    private static class LevelData {
        private int level;
        private int lastTransaction;
    }
    
    private Map<IntCoordKey, ChunkData> chunks = new HashMap<>();
    
    public Chunk get(int x, int y) {
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData d = chunks.get(key);
        if (d == null)
            return null;
        return d.chunk;
    }
    
    public void save(int x, int y) {
        
    }
    
    private int transids = 0;
    
    public void load(int x, int y, int level, Object lock) {
        load(x, y, level, lock, transids++);
    }
    
    private void load(int x, int y, int level, Object lock, int transid) {
        if (level <= 0) {
            unload(x, y, lock);
            return;
        }
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData c = chunks.get(key);
        if (c == null) {
            c = new ChunkData();
            chunks.put(key, c);
            c.levelActual = level;
            //load chunk or generate it
        } else {
            //Chunk is loaded right now. add the locks level or update it
            Map<Object, LevelData> ldmap = c.levels;
            LevelData ld = ldmap.get(lock);
            boolean cont = true;
            if (ld == null) {
                ld = new LevelData();
                ldmap.put(lock, ld);
                cont = false;
            }
            if (cont) {
                if (ld.lastTransaction == transid) {
                    //has already been handled
                    return;
                }
            }
            ld.level = level;
            ld.lastTransaction = transid;
            if (level > c.levelActual) {
                //new max level
                c.levelActual = level;
            } else if (cont && level < c.levelActual) {
                //update the locks level on this chunk. this could lower the overall level, so caclulate the max. cant go below 1 because level is >= 1. 
                int max = 0;
                for (LevelData l : ldmap.values()) {
                    max = Math.max(max, l.level);
                }
                c.levelActual = max;
            }
        }
        for (Direction d : Direction.MOORE_NEIGHBOURS) {
            load(x + d.dx, y + d.dy, level - 1, lock, transid);//theres a problem if the level gets lowered by more than one - unload will unload other chunks as well
        }
        
    }
    
    public void unload(int x, int y, Object lock) {
        IntCoordKey key = new IntCoordKey(x, y);
        ChunkData c = chunks.get(key);
        if (c == null)
            return;
        Map<Object, LevelData> ldmap = c.levels;
        LevelData remLevel = ldmap.remove(lock);
        if (ldmap.isEmpty()) {
            c.levelActual = 0;
            chunks.remove(key);
            //save, schedule for removal, whatever
        } else if (remLevel.level >= c.levelActual) {
            int max = 0;
            for (LevelData l : ldmap.values()) {
                max = Math.max(max, l.level);
            }
            c.levelActual = max;
        }
        if (remLevel.level > 1) {
            for (Direction d : Direction.MOORE_NEIGHBOURS) {
                unload(x + d.dx, y + d.dy, lock);
            }
        }
    }
    
}
