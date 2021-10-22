package de.pcfreak9000.pixelsimtest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class ElementWater extends Element {
    public ElementWater() {
        this.density = 2;
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
        vel.add(0f, -4.0f);
        int x1 = (int) (state.x + Gdx.graphics.getDeltaTime() * vel.x);
        int y1 = (int) (state.y + Gdx.graphics.getDeltaTime() * vel.y);
        bresenham(state.x, state.y, x1, y1, mat, state);
    }
    
    private void bresenham(int x0, int y0, int x1, int y1, ElementMatrix mat, ElementState state) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2; /* error value e_xy */
        int ox = x0;
        int oy = y0;
        while (true) {
            if (ox != x0 || oy != y0) {
//                if (mat.checkBounds(x0, y0)
//                        && (!mat.hasElement(x0, y0) || mat.getState(x0, y0).getElement().density < this.density)) {//somewhere apply archimedes force
//                    mat.switchStates(state.x, state.y, x0, y0);
//                } else {
//                    float f = state.getVelocity().len();
//                    double angle = Math.atan2(state.getVelocity().y, state.getVelocity().x);
//                    Direction d = Direction.ofAngle(angle);
//                    if (d == Direction.Up || d == Direction.Down) {
//                        Direction mov = Math.random() > 0.5 ? d.orth0() : d.orth1();
//                        //TODO check the direction
//                        state.getVelocity().set(f * mov.dx * 1, f * mov.dy * 1);
//                    }else {
//                        state.getVelocity().set(0, 0);
//                    }
//                    //                    int xdif = x0 - ox;
//                    //                    int ydif = y0 - oy;
//                    //                    if (ydif >= xdif) {
//                    //                        
//                    //                    } else {
//                    //                        
//                    //                    }
//                    //TODO: spread in random direction?
//                    break;
//                }
            }
            if (x0 == x1 && y0 == y1) {
                break;
            }
            e2 = 2 * err;
            if (e2 > dy) {
                err += dy;
                x0 += sx;
            } /* e_xy+e_x > 0 */
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            } /* e_xy+e_y < 0 */
        }
    }
}
