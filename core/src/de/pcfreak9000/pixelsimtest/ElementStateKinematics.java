package de.pcfreak9000.pixelsimtest;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ElementStateKinematics {
    
    private static final int MAX_ITERATIONS = 30;
    
    private static enum MovementResult {
        Passed, ChangedDirection, Waiting;
    }
    
    //kk internal friction stuff 
    //buoyancy
    //configure stuff
    //randomness
    //inelastic bounces?
    //clean up
    
    private static void correctAcceleration(ElementState state, ElementMatrix mat, Vector2 acl, Direction d) {
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
    
    public static void apply(ElementState state, ElementMatrix mat) {
        Vector2 vel = state.getVelocity();
        Vector2 acl = state.getAcceleration();
        float g = 40;
        float dens = state.getElement().density;
        float ref = mat.base().density;
        // float dif = dens - ref;
        // acl.add(0, -func(dif) * g);
        if (dens != ref) {
            acl.add(0, dens > ref ? -g : g);
        }
        if (!state.moving && !state.getElement().fluid) {//acceleration fluid threshold maybe? at higher accelerations, things behave like a fluid? i.e. cant hold themselfes together?
            for (Direction d : Direction.VONNEUMANN_NEIGHBOURS) {//or bring in some random x-errors?
                correctAcceleration(state, mat, acl, d);
            }
        }
        vel.add(acl);
        acl.set(0, 0);
        if (vel.len2() > 0) {
            float time = state.timepart + Gdx.graphics.getDeltaTime();
            move(time, mat, state);
        } else {
            state.timepart = 0;
        }
        
    }
    
    private static float getFriction(ElementMatrix mat, ElementState state, Direction dir) {
        float friction = 0;
        if (state.getFriction() != 0) {
            //left, right, front left, front right
            //current j=0, front j=1
            Direction[] ds = { dir.orth0(), dir.orth1() };
            for (int j = 0; j <= 1; j++) {
                for (Direction d : ds) {
                    int dx = state.getX() + d.dx + j * dir.dx;
                    int dy = state.getY() + d.dy + j * dir.dy;
                    if (mat.checkBounds(dx, dy)) {
                        ElementState otherstate = mat.getState(dx, dy);
                        if (otherstate.getElement() == state.getElement()) {
                            float internalfriction = state.getInternalFriction();
                            friction += square(internalfriction);
                        } else {
                            float otherfriction = otherstate.getFriction();
                            friction += otherfriction * state.getFriction();
                        }
                        
                    }
                }
            }
            friction = friction / 4f;
        }
        return MathUtils.clamp(friction, 0, 1);
    }
    
    private static float square(float f) {
        return f * f;
    }
    
    //get how much time there is to move (frame + time fraction from last frame)
    //calculate direction to move in
    //calculate the next move (check if there is an obstacle and the direction needs to change or find out with bresenham the next line tile)
    //check if there is enough time to do that move (if not, accumulate the left over time fraction). use an effective velocity in which friction is accounted for 
    //if enough time, do that move and subtract the needed time and apply the friction for that move, then go to step 3 
    private static void move(float time, ElementMatrix mat, ElementState state) {
        Vector2 velocity = state.getVelocity();
        float speed = velocity.len();
        outer: for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (Float.isNaN(speed)) {
                throw new IllegalStateException(Objects.toString(velocity));
            }
            float xoff = velocity.x * time;
            float yoff = velocity.y * time;
            /*
             * Based on the video "Super Fast Ray Casting in Tiled Worlds using DDA" by
             * javidx9 (2021, https://www.youtube.com/watch?v=NbSee-XM7WA).
             */
            int tx = state.getX();
            int ty = state.getY();
            final int txTarget = tx + (int) Math.floor(xoff);
            final int tyTarget = ty + (int) Math.floor(yoff);
            if (tx == txTarget && ty == tyTarget) {
                state.timepart = time;
                break outer;
            }
            final float rayUnitStepSizeX = (float) Math.sqrt(1 + square(yoff / xoff));
            final float rayUnitStepSizeY = (float) Math.sqrt(1 + square(xoff / yoff));
            final int stepX = (int) Math.signum(xoff);
            final int stepY = (int) Math.signum(yoff);
            float lenx = xoff < 0 ? 0 : rayUnitStepSizeX;
            float leny = yoff < 0 ? 0 : rayUnitStepSizeY;
            Direction dir;
            inner: while (true) {
                if (tx == txTarget && ty == tyTarget) {
                    state.timepart = time;
                    break outer;
                }
                if (!checkMovementAny(mat, state)) {
                    speed = 0;
                    break outer;
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
                ElementState next = !mat.checkBounds(tx, ty) ? null : mat.getState(tx, ty);
                float friction = next == null ? 0 : getFriction(mat, state, dir);
                float timecost = 1f / ((1 - friction) * speed);
                if (time - timecost < 0) {
                    state.timepart = 0;
                    break outer;
                }
                float movResult = movement(mat, state, next, dir);
                if (movResult == Float.NEGATIVE_INFINITY) {
                    break inner;
                }
                velocity.scl(movResult);
                speed *= movResult;
                velocity.scl(1 - friction);
                speed *= (1 - friction);
                time -= timecost;
            }
        }
        if (speed < 1) {
            velocity.setZero();
            speed = 0;
            state.timepart = 0;
        }
    }
    
    private static boolean checkMovementAny(ElementMatrix mat, ElementState state) {
        Direction[] ds = Direction.VONNEUMANN_NEIGHBOURS;
        for (Direction d : ds) {
            int x = state.getX() + d.dx;
            int y = state.getY() + d.dy;
            if (canSwitch(state, mat.getState(x, y), mat)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean canSwitch(ElementState state, ElementState next, ElementMatrix mat) {
        if (next == null) {
            return false;
        }
        if (next.getElement().isFixed) {
            return false;
        }
        float dens = state.getElement().density;
        float densityDiff = next == null ? -dens : next.getElement().density - dens;
        return (densityDiff < 0 && dens > mat.base().density) || (densityDiff > 0 && dens < mat.base().density);
        //return true;
        //return densityDiff < 0;
    }
    
    private static void applyPullAlong(ElementMatrix mat, ElementState state, Direction dir) {
        Direction[] ds = { dir.orth0(), dir.orth1() };
        for (Direction d : ds) {
            int x = state.getX() + d.dx;
            int y = state.getY() + d.dy;
            ElementState side = mat.getState(x, y);
            if (side != null && !side.getElement().isFixed && !side.getElement().fluid) {
                if (mat.random() < side.getPulledAlongChance()) {
                    side.getVelocity().mulAdd(state.getVelocity(), side.getPulledAlongStrength());//use a symmetric approach or just the state and not side for the strength?
                }
            }
        }
    }
    
    private static float movement(ElementMatrix mat, ElementState state, ElementState next, Direction dir) {
        Vector2 v = state.getVelocity();
        //try to not bounce of something which moves faster in the same direction
        //generally try to use deltav instead of state.vel -> both come with problems
        if (canSwitch(state, next, mat)) {
            applyPullAlong(mat, state, dir);
            //state.getVelocity().scl(0.9f);//this causes a left-tendency, weird
            mat.switchStates(state, next);
            return 1f;
        } else {
            Direction d;
            float f = v.x * dir.dx + v.y * dir.dy;
            if ((dir.dx == 0 && v.x == 0) || (dir.dy == 0 && v.y == 0)) {
                if (dir.dy == 0) {
                    d = state.getElement().density > mat.base().density ? Direction.Down : Direction.Up;//oof
                } else {
                    d = mat.random() < 0.5 ? Direction.Left : Direction.Right;
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
            return Float.NEGATIVE_INFINITY;
        }
    }
    
    //sucks
    private static float getBuoyancy(ElementMatrix mat, ElementState state, float g) {
        int x = state.x;
        int y = state.y;
        if (mat.checkBounds(x, y + 1) && !mat.getState(x, y + 1).getElement().isFixed) {
            ElementState state4 = mat.getState(x, y + 1);
            Vector2 v4 = state4.getVelocity();
            Vector2 v0 = state.getVelocity();
            Vector2 v1 = Vector2.Zero;
            if (mat.checkBounds(x, y - 1)) {
                v1 = mat.getState(x, y - 1).getVelocity();
            }
            //if (v4.y < v0.y) {
            return g * state4.getElement().density / state.getElement().density;
            //}
        }
        return 0;
    }
    
    //sucks
    private static float getBuoyancyAccel(ElementMatrix mat, ElementState state, float g) {
        int x = state.x;
        int y = state.y;
        //        if (mat.checkBounds(x, y + 1) && !mat.getState(x, y + 1).getElement().isFixed) {
        //            return g * mat.getState(x, y + 1).getElement().density / state.getElement().density;
        //        }
        //        return 0;
        float accel = 0;
        int f = 0;
        Direction[] ds = { Direction.Left, Direction.Right, Direction.Up };
        for (Direction d : ds) {
            int ax = x + d.dx;
            int ay = y + d.dy;
            if (mat.checkBounds(ax, ay) && !mat.getState(ax, ay).getElement().isFixed) {
                accel += mat.getState(ax, ay).getElement().density;
                f += 1;
            }
        }
        if (f == 0) {
            return 0;
        }
        accel = g * accel / state.getElement().density * 1f / f;
        return accel;
    }
    
    //yes, this is completely stupid
    private static final double A = -1;
    private static final double B = -Math.log(2.0);
    
    private static float func(float x) {
        return (float) (A * Math.exp(x * B) + 1);
    }
}
