package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
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
    private Element water;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        vp = new FitViewport(ElementMatrix.SIZE, ElementMatrix.SIZE);
        setupElements();
        mat = new ElementMatrix(air);
        //mat.createCircle(120, 120, 30, water);
        //        mat.createCircle(0, 0, 20, gas);
        //        mat.createCircle(0, 99, 20, gravel);
        //        mat.createCircle(50, 50, 30, stone);
        for (int i = 0; i < 110; i++) {
            mat.createState(i + 20, 50, stone);
        }
    }
    
    private void setupElements() {
        air = new ElementAir();
        stone = new ElementStone();
        water = new ElementWater();
    }
    
    @Override
    public void render() {
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();
            Vector2 vec = vp.unproject(new Vector2(x, y));
            int ax = (int) Math.floor(vec.x);
            int ay = (int) Math.floor(vec.y);
            if (mat.checkBounds(ax, ay) && !mat.hasElement(ax, ay)) {
                mat.createState(ax, ay, water);
            }
        }
        for (int i = 0; i < 1; i++) {
            mat.update();
        }
        ScreenUtils.clear(0, 0, 0, 1);
        vp.apply();
        batch.setProjectionMatrix(vp.getCamera().combined);
        batch.begin();
        mat.render(batch);
        batch.end();
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
