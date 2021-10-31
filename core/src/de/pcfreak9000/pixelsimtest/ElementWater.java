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
        float value = state.ahyes + Gdx.graphics.getDeltaTime();
        // int i = 0;
        for (int i = 0; i < 10; i++) {
            value = move(value, mat, state);
            if (value < 0) {
                value *= -1;
                state.ahyes = value;
                break;
            } else {
                state.ahyes = value;
            }
            //            i++;
            //            if (i > 10) {
            //                System.out.println("peter");
            //                if (i > 12) {
            //                    break;
            //                }
            //            }
        }
    }
    
    private float getFriction(ElementMatrix mat, ElementState state) {
        float friction = 0;
        if (state.getFriction() != 0) {
            for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
                int dx = state.getX() + d.dx;
                int dy = state.getY() + d.dy;
                if (mat.checkBounds(dx, dy)) {
                    ElementState otherstate = mat.getState(dx, dy);
                    if (otherstate != state) {//ah yes the big stupid...
                        float otherfriction = otherstate.getFriction();
                        friction += otherfriction * state.getFriction();
                    }
                }
            }
            friction = friction / 3;
        }
        return friction;
    }
    
    private float square(float f) {
        return f * f;
    }
    //get how much time there is to move (frame + time fraction from last frame)
    //calculate direction to move in
    //calculate the next move (check if there is an obstacle and the direction needs to change or find out with bresenham the next line tile)
    //check if there is enough time to do that move (if not, accumulate the left over time fraction). use an effective velocity in which friction is accounted for 
    //if enough time, do that move and subtract the needed time and apply the friction for that move, then go to step 3 
    
    private float move(float timeleft, ElementMatrix mat, ElementState state) {
        Vector2 velocity = state.getVelocity();
        float speed = velocity.len();
        if (speed != speed) {
            System.out.println(velocity);
            throw new IllegalStateException();
        }
        Vector2 offset = velocity.cpy().scl(timeleft);
        /*
         * Based on the video "Super Fast Ray Casting in Tiled Worlds using DDA" by
         * javidx9 (2021, https://www.youtube.com/watch?v=NbSee-XM7WA).
         */
        //constants
        final int txStart = state.getX();
        final int tyStart = state.getY();
        final int txTarget = txStart + (int) Math.floor(offset.x);
        final int tyTarget = tyStart + (int) Math.floor(offset.y);
        if (txStart == txTarget && tyStart == tyTarget) {
            return timeleft * -1;
        }
        final float dx = offset.x;
        final float dy = offset.y;
        final float rayUnitStepSizeX = (float) Math.sqrt(1 + square(dy / dx));
        final float rayUnitStepSizeY = (float) Math.sqrt(1 + square(dx / dy));
        final int stepX = (int) Math.signum(dx);
        final int stepY = (int) Math.signum(dy);
        //prep loop vars
        float lenx, leny;
        if (dx < 0) {
            lenx = 0;
        } else {
            lenx = 1;
        }
        if (dy < 0) {
            leny = 0;
        } else {
            leny = 1;
        }
        lenx *= rayUnitStepSizeX;
        leny *= rayUnitStepSizeY;
        int tx = txStart;
        int ty = tyStart;
        Direction dir;
        while (true) {
            if (tx == txTarget && ty == tyTarget) {
                return timeleft * -1;
            }
            if (lenx < leny) {
                tx += stepX;
                lenx += rayUnitStepSizeX;
                dir = stepX < 0 ? Direction.Left : Direction.Right;
            } else {
                ty += stepY;
                leny += rayUnitStepSizeY;
                dir = stepY < 0 ? Direction.Down : Direction.Up;
            }
            ElementState next = mat.checkBounds(tx, ty) == false ? null : mat.getState(tx, ty);
            float friction = 0;//next == null ? 0 : getFriction(mat, next);
            float cost = 1f / ((1 - friction) * speed);
            if (timeleft - cost < 0) {
                timeleft *= -1;//this marks the result of the function as time out rather than an obstacle which could be circumvented
                break;
            }
            boolean stoppedBefore = movement(mat, state, next, dir);
            if (stoppedBefore) {
                break;
            }
            state.getVelocity().scl(1 - friction);
            speed *= (1 - friction);
            timeleft -= cost;
        }
        return timeleft;
    }
    
    private boolean movement(ElementMatrix mat, ElementState state, ElementState next, Direction dir) {
        if (next != null && next.getElement().density <= state.getElement().density && !next.getElement().isFixed) {
            mat.switchStates(state, next);
            return false;
        } else {
            //            if (mat.random() < 0.05) {
            //                state.getVelocity().scl(-0.5f);
            //                return true;
            //            }
            Direction d;
            Vector2 mov = new Vector2(dir.dx * state.getVelocity().x, dir.dy * state.getVelocity().y);
            if ((dir.dx == 0 && state.getVelocity().x == 0) || (dir.dy == 0 && state.getVelocity().y == 0)) {
                d = mat.random() > 0.5 ? dir.orth0() : dir.orth1();
            } else {
                if (dir.dx == 0) {
                    d = state.getVelocity().x > 0 ? Direction.Right : Direction.Left;
                } else if (dir.dy == 0) {
                    d = state.getVelocity().y > 0 ? Direction.Up : Direction.Down;
                } else {
                    throw new IllegalStateException();
                }
            }
            //v-(v dot n)*n
            Vector2 v = state.getVelocity();
            if (dir.dx != 0) {
                v.x = 0;
            } else if (dir.dy != 0) {
                v.y = 0;
            }
            float f = mov.x + mov.y;
            //f *= 0.5f;
            if (d.dx != 0) {
                v.x = d.dx * f;
            } else if (d.dy != 0) {
                v.y = d.dy * f;
            }
            // state.getVelocity().clamp(-2000, 2000);
            return true;
        }
        //return true;
    }
    
    @Override
    public float getFriction() {
        return 0.05f;
    }
}
