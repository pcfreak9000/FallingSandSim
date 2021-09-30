package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class PixelSimTest extends ApplicationAdapter {
    private SpriteBatch batch;
    private ElementMatrix mat;
    private Viewport vp;
    
    private Element water;
    private Element gravel;
    private Element gas;
    private Element stone;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        vp = new FitViewport(ElementMatrix.SIZE, ElementMatrix.SIZE);
        setupElements();
        mat = new ElementMatrix();
        mat.setCircle(120, 120, 30, water);
        mat.setCircle(0, 0, 20, gas);
        mat.setCircle(0, 99, 20, gravel);
        mat.setCircle(50, 50, 30, stone);
        for(int i=0; i<110; i++) {
            //mat.setElement(i+20, 50, stone);
        }
    }
    
    private void setupElements() {
        water = new Water();
        gravel = new Gravel();
        gas = new Gas();
        stone = new Element();
        stone.setColor(Color.DARK_GRAY);
        stone.isFixed = true;
    }
    
    @Override
    public void render() {
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