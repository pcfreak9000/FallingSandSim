package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ElementWater extends Element {
    public ElementWater() {
        this.density = 1.1f;
        this.c = Color.BLUE;
    }
    
    private float getBuoyancyAccel(ElementMatrix mat, ElementState state, float g) {
        int x = state.x;
        int y = state.y;
        //        if (mat.checkBounds(x, y + 1) && !mat.getState(x, y + 1).getElement().isFixed) {
        //            return g * mat.getState(x, y + 1).getElement().density / state.getElement().density;
        //        }
        //        return 0;
        float accel = 0;
        int f = 0;
        Direction[] ds = { Direction.Down};//{ Direction.Left, Direction.Right, Direction.Up };
        for (Direction d : ds) {
            int ax = x + d.dx;
            int ay = y + d.dy;
            if (mat.checkBounds(ax, ay) && !mat.getState(ax, ay).getElement().isFixed) {
                accel += mat.getState(ax, ay).getElement().density;
                f += 1;
            }
        } //FIXME buoyancy
        if (f == 0) {
            return 0;
        }
        accel = g * accel / state.getElement().density * 1f / f;
        return accel;
    }
    //TODO consider testing a from bot to top update strategy?
    
    private void correctAcceleration(ElementState state, ElementMatrix mat, Vector2 acl, Direction d) {
        if (acl.x * d.dx + acl.y * d.dy > 0) {
            if (mat.checkBounds(state.x + d.dx, state.y + d.dy)) {
                ElementState e = mat.getState(state.x + d.dx, state.y + d.dy);
                if (!canSwitch(state, e, mat)) {
                    if (d.dx != 0) {
                        acl.x = 0;
                    } else if (d.dy != 0) {
                        acl.y = 0;
                    }
                }
            }
        }
    }
    
    @Override
    public void update(ElementState state, ElementMatrix mat) {
        Vector2 vel = state.getVelocity();
        Vector2 acl = state.getAcceleration();
        float g = 40;
        acl.add(0, -g + getBuoyancyAccel(mat, state, g));
        for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {
           // correctAcceleration(state, mat, acl, d);
        }
        float stickyness = getStickyness(mat, state);
        if (vel.len2() > 0) {
            vel.add(acl);
        } else if (acl.len2() > square(stickyness)) {
            float a = square(stickyness) / acl.len2();
            acl.scl(1 - a * 0.5f);
            vel.add(acl);
        }
        acl.set(0, 0);
        if (vel.len2() > 0) {
            // state.color = Color.GREEN;
            float time = state.ahyes + Gdx.graphics.getDeltaTime();
            for (int i = 0; i < 8; i++) {
                time = move(time, mat, state);
                if (time == Float.NEGATIVE_INFINITY) {
                    state.ahyes = 0;
                    break;
                } else if (time < 0) {
                    time *= -1;
                    state.ahyes = time;
                    break;
                } else {
                    state.ahyes = time;
                }
            }
        } else {
            // state.color = Color.RED;
            state.ahyes = 0;
        }
        
    }
    
    private float getStickyness(ElementMatrix mat, ElementState state) {
        float sticky = 0;
        Direction[] ds = Direction.VONNEUMANN_NEIGHBOURS;
        for (Direction d : ds) {
            int dx = state.getX() + d.dx;
            int dy = state.getY() + d.dy;
            if (mat.checkBounds(dx, dy)) {
                ElementState otherstate = mat.getState(dx, dy);
                if (otherstate != state) {
                    float otherStickyness = otherstate.getStickyness();
                    sticky += 0.5f * (otherStickyness + state.getStickyness());
                }
            }
        }
        return sticky;
    }
    
    private float getFriction(ElementMatrix mat, ElementState state, Direction dir) {
        float friction = 0;
        if (state.getFriction() != 0) {
            Direction[] ds = { dir.orth0(), dir.orth1() };
            for (Direction d : ds) {
                int dx = state.getX() + d.dx + dir.dx;
                int dy = state.getY() + d.dy + dir.dy;
                if (mat.checkBounds(dx, dy)) {
                    ElementState otherstate = mat.getState(dx, dy);
                    if (otherstate != state) {//ah yes the big stupid...
                        float otherfriction = otherstate.getFriction();
                        friction += otherfriction * state.getFriction();
                    }
                }
            }
            friction = friction / 2f;
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
        float xoff = velocity.x * timeleft;
        float yoff = velocity.y * timeleft;
        //Vector2 offset = velocity.cpy().scl(timeleft);
        /*
         * Based on the video "Super Fast Ray Casting in Tiled Worlds using DDA" by
         * javidx9 (2021, https://www.youtube.com/watch?v=NbSee-XM7WA).
         */
        //constants
        final int txStart = state.getX();
        final int tyStart = state.getY();
        final int txTarget = txStart + (int) Math.floor(xoff);
        final int tyTarget = tyStart + (int) Math.floor(yoff);
        if (txStart == txTarget && tyStart == tyTarget) {
            return timeleft * -1;
        }
        final float rayUnitStepSizeX = (float) Math.sqrt(1 + square(yoff / xoff));
        final float rayUnitStepSizeY = (float) Math.sqrt(1 + square(xoff / yoff));
        final int stepX = (int) Math.signum(xoff);
        final int stepY = (int) Math.signum(yoff);
        //prep loop vars
        float lenx, leny;
        if (xoff < 0) {
            lenx = 0;
        } else {
            lenx = 1;
        }
        if (yoff < 0) {
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
//            if (speed < getStartResistance(mat, state, dir)) {
//                timeleft *= -1;
//                break;
//            }
            ElementState next = !mat.checkBounds(tx, ty) ? null : mat.getState(tx, ty);
            float friction = next == null ? 0 : getFriction(mat, state, dir);
            friction = MathUtils.clamp(friction, 0, 1);
            float cost = 1f / ((1 - friction) * speed);
            //            if (timeleft - cost < 0) {
            //                timeleft *= -1;//this marks the result of the function as time out rather than an obstacle which could be circumvented
            //                break;
            //            }
            boolean stoppedBefore = movement(mat, state, next, dir);
            if (stoppedBefore) {
                break;
            }
            state.getVelocity().scl(1 - friction);
            speed *= (1 - friction);
            timeleft -= cost;
            //the friction applies to movement so even though the budget didnt allow it, still made the step
            if (timeleft < 0) {
                return Float.NEGATIVE_INFINITY;
            }
        }
        return timeleft;
    }
    
    @Override
    public float getStickyness() {
        return 1;
    }
    
    private boolean canSwitch(ElementState state, ElementState next, ElementMatrix mat) {
        if (next == null) {
            return false;
        }
        if (next.getElement().isFixed) {
            return false;
        }
        float dens = state.getElement().density;
        float densityDiff = next == null ? -state.getElement().density
                : next.getElement().density - state.getElement().density;
        return (densityDiff < 0 && dens > 1) || (densityDiff > 0 && dens < 1);
    }
    
    private boolean movement(ElementMatrix mat, ElementState state, ElementState next, Direction dir) {
        float dens = state.getElement().density;
        
        // densityDiff = MathUtils.clamp(densityDiff, -3, 3);
        //        float diversion = densityDiff / 3 + 0.5f;
        //        diversion = MathUtils.clamp(diversion, 0, 1);
        //        if (next != null && next.getElement().density == 0) {
        //            diversion = 1;
        //        }
        float str = next == null ? 0 : next.getElement().getFriction();
        //boolean b = state.getVelocity().len2() >= square(str);
        Vector2 v = state.getVelocity();
        Vector2 other = next == null ? Vector2.Zero : next.getVelocity();
        Vector2 check = v.cpy().sub(other);
        // float factor = (densnex - dens) / (dens + densnex);
        //factor = factor * 0.5f + 0.5f;
        //boolean b = mat.random() > factor;
        if (next != null) {
            //b = b && next.getElement() != this;
        }
        //TODO try to not bounce of something which moves faster in the same direction
        //TODO generally try to use deltav instead of state.vel
        //        float gurke = check.x * dir.dx + check.y * dir.dy;
        //        if (gurke > 0) {
        //            //b = true;
        //        }
        //        float g0 = v.x * dir.dx + v.y * dir.dy;
        //        float g1 = other.x * dir.dx + other.y * dir.dy;
        //        if (g1 < g0) {
        //            b = true;
        //        }
        float value = other.x * v.x + other.y + v.y;
        //        value /= other.len() * v.len();
        //        value *= 0.5f;
        //        value += 0.5f;
        //        value = 1 - value;
        if (canSwitch(state, next, mat)) {
            mat.switchStates(state, next);
            return false;
        } else {
            Direction d;
            float f = v.x * dir.dx + v.y * dir.dy;
            if ((dir.dx == 0 && state.getVelocity().x == 0) || (dir.dy == 0 && state.getVelocity().y == 0)) {
                if (dir.dy == 0) {
                    d = dens > 1 ? Direction.Down : Direction.Up;
                } else {
                    d = mat.random() > 0.5 ? dir.orth0() : dir.orth1();
                }
            } else {
                if (dir.dx == 0) {
                    d = state.getVelocity().x > 0 ? Direction.Right : Direction.Left;
                } else if (dir.dy == 0) {
                    d = state.getVelocity().y > 0 ? Direction.Up : Direction.Down;
                } else {
                    throw new IllegalStateException();
                }
            }
            if (dir.dx != 0) {
                v.x = 0;
            } else if (dir.dy != 0) {
                v.y = 0;
            }
            if (d.dx != 0) {
                v.x += d.dx * f;
            } else if (d.dy != 0) {
                v.y += d.dy * f;
            }
            return true;
        }
        
        //return true;
    }
    
    @Override
    public float getFriction() {
        return 0.01f;
    }
}
