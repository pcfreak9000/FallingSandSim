package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class ElementWater extends Element {
    public ElementWater() {
        this.density = 2;
        this.c = Color.BLUE;
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat) {
        //        if(!mat.checkBounds(state.getX(), state.getY()-1)) {
        //            return;
        //        }
        //        ElementState below = mat.getState(state.getX(), state.getY() - 1);
        //        if (!mat.hasElement(state.x, state.y - 1) && below.getElement().density < this.density) {
        //            mat.switchStates(state.x, state.y, state.x, state.y - 1);
        //        }
        Vector2 vel = state.getVelocity();
        vel.add(0f, -40.0f);
        applyFriction(state, mat);
        int x1 = (int) (state.x + Gdx.graphics.getDeltaTime() * vel.x);
        int y1 = (int) (state.y + Gdx.graphics.getDeltaTime() * vel.y);
        bresenham(state.x, state.y, x1, y1, mat, state);
    }
    
    private void applyFriction(ElementState state, ElementMatrix mat) {
        float friction = 0;
        if (state.getFriction() != 0) {
            for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                int dx = state.getX() + d.dx;
                int dy = state.getY() + d.dy;
                if (mat.checkBounds(dx, dy)) {
                    float otherfriction = mat.getState(dx, dy).getFriction();
                    friction += otherfriction * state.getFriction();
                }
            }
            friction = friction / 3;
        }
        state.getVelocity().scl(1 - friction);
    }
    
    //get how much time there is to move (frame + time fraction from last frame)
    //calculate direction to move in
    //calculate the next move (check if there is an obstacle and the direction needs to change or find out with bresenham the next line tile)
    //check if there is enough time to do that move (if not, accumulate the left over time fraction). use an effective velocity in which friction is accounted for 
    //if enough time, do that move and subtract the needed time and apply the friction for that move, then go to step 3 
    
    private void bresenham(int startX, int startY, int destX, int destY, ElementMatrix mat, ElementState state) {
        int dx = Math.abs(destX - startX), sx = startX < destX ? 1 : -1;
        int dy = -Math.abs(destY - startY), sy = startY < destY ? 1 : -1;
        int err = dx + dy, e2; /* error value e_xy */
        int x = startX;
        int y = startY;
        while (true) {
            if (startX != x || startY != y) {
                if (mat.checkBounds(x, y)) {
                    ElementState next = mat.getState(x, y);
                    boolean stopped = movement(mat, state, next);
                    if (stopped) {
                        break;
                    }
                }
            }
            if (x == destX && y == destY) {
                //destination reached
                break;
            }
            e2 = 2 * err;
            if (e2 > dy) {
                err += dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }
    
    private boolean movement(ElementMatrix mat, ElementState state, ElementState next) {
        if (next.getElement().density <= state.getElement().density && !next.getElement().isFixed) {
            mat.switchStates(state, next);
            return false;
        } else {
            //state.getVelocity().set(
            //         state.getVelocity().x + (mat.random() > 0.5 ? 1 : -1) * Math.abs(state.getVelocity().y) * 0.8f, 0);
            return true;
        }
        //return true;
    }
}
