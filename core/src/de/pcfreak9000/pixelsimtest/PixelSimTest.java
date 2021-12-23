package de.pcfreak9000.pixelsimtest;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PixelSimTest extends ApplicationAdapter {
    private SpriteBatch batch;
    private ElementMatrix mat;
    private Viewport vp;
    
    private Element air;
    private Element stone;
    public static Element water;
    private Element sand;
    public static Element watervapor;
    private Element[] elements = new Element[5];
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        vp = new FitViewport(ElementMatrix.SIZE, ElementMatrix.SIZE);
        setupElements();
        mat = new ElementMatrix(air);
        // mat.createCircle(120, 120, 30, water);
        //        mat.createCircle(0, 0, 20, gas);
        //        mat.createCircle(0, 99, 20, gravel);
        
        //mat.createCircle(50, 50, 30, stone);
        for (int i = 0; i < 110; i++) {
            mat.createState(i + 20, 50, stone);
        }
        for (int i = 0; i < ElementMatrix.SIZE; i++) {
            mat.createState(i, 0, stone);
        }
    }
    
    private void setupElements() {
        air = new ElementAir();
        elements[0] = air;
        stone = new ElementStone();
        elements[1] = stone;
        water = new ElementWater();
        elements[2] = water;
        sand = new ElementSand();
        elements[3] = sand;
        watervapor = new ElementWaterVapor();
        elements[4] = watervapor;
    }
    
    private static class Spout {
        Element element;
        int x, y;
        int rad;
    }
    
    List<Spout> spouts = new ArrayList<>();
    Element current;
    
    @Override
    public void render() {
        long t0 = System.currentTimeMillis();
        if (current == null) {
            current = water;
        }
        for (int i = 0; i < elements.length; i++) {
            if (Gdx.input.isKeyJustPressed(Keys.NUM_0 + i)) {
                current = elements[i];
                break;
            }
        }
        if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Vector2 vec = vp.unproject(new Vector2(x, y));
            int ax = (int) Math.floor(vec.x);
            int ay = (int) Math.floor(vec.y);
            if (!Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) {
                mat.createCircle(ax, ay, 15, current);
            } else {
                Spout s = new Spout();
                s.x = ax;
                s.y = ay;
                s.rad = 3;
                s.element = current;
                spouts.add(s);
            }
        }
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Vector2 vec = vp.unproject(new Vector2(x, y));
            int ax = (int) Math.floor(vec.x);
            int ay = (int) Math.floor(vec.y);
            for (int i = -7; i <= 7; i++) {
                for (int j = -7; j <= 7; j++) {
                    if (mat.checkBounds(ax + i, ay + j)) {
                        mat.killState(ax + i, ay + j);
                    }
                }
            }
        }
        for (Spout s : spouts) {
            mat.createCircle(s.x, s.y, s.rad, s.element);
        }
        long t1 = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            mat.update();
        }
        long t2 = System.currentTimeMillis();
        ScreenUtils.clear(0, 0, 0, 1);
        vp.apply();
        batch.setProjectionMatrix(vp.getCamera().combined);
        batch.begin();
        mat.render(batch);
        batch.end();
        long t3 = System.currentTimeMillis();
        System.out.println("Stuff time: " + (t1 - t0));
        System.out.println("Update time: " + (t2 - t1));
        System.out.println("Render time: " + (t3 - t2));
    }
    
    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        vp.update(width, height);
        vp.getCamera().position.set(ElementMatrix.SIZE / 2, ElementMatrix.SIZE / 2, 0);
    }
    
    @Override
    public void dispose() {
        batch.dispose();
    }
}
