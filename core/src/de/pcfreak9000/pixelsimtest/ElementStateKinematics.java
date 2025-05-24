package de.pcfreak9000.pixelsimtest;

import java.util.Objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ElementStateKinematics {

	private static final int MAX_ITERATIONS = 3;

	// kk internal friction stuff
	// buoyancy
	// configure stuff
	// randomness
	// inelastic bounces?
	// clean up

	static float t;

	public static void apply(ElementState state, ElementMatrix mat, float dt) {
		ElementStateThermo.produceHeat(state, mat, dt);
		int x = state.getX();
		int y = state.getY();
		if (state.isFluidLike()) {
			water(x, y, state, mat);
		} else {
			sand(x, y, state, mat);
		}
		ElementStateThermo.spreadHeat(state, mat, dt);
	}

	private static void move(int x0, int y0, int x1, int y1, ElementMatrix mat) {
		int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
		int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
		int err = dx + dy, e2;
		int ox, oy;
		while (x0 != x1 && y0 != y1) {
			e2 = 2 * err;
			ox = x0;
			oy = y0;
			if (e2 > dy) {
				err += dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
			if (canSwitch(mat.getState(ox, oy), mat.getState(x0, y0), mat)) {
				mat.switchStates(ox, oy, x0, y0);
			} else {
				break;
			}
		}
	}

	private static void sand(int x, int y, ElementState state, ElementMatrix mat) {
		boolean switched = checkAndMove(x, y, state, mat, Direction.Down);
		if (!switched && !state.enabled) {
			return;
		} else if (switched && !state.enabled) {
			state.enabled = true;
		}
		if (!switched && state.mov == Direction.Down) {
			if (mat.random() > 1.0) {
				state.enabled = false;
				return;
			}
			state.mov = mat.random() > 0.5 ? Direction.DownLeft : Direction.DownRight;
		}
		if (!switched) {
			switched = checkAndMove2(x, y, state, mat, state.mov);
		}
		if (!switched) {
			switched = checkAndMove2(x, y, state, mat,
					state.mov == Direction.DownLeft ? Direction.DownRight : Direction.DownLeft);
		}
	}

	private static void water(int x, int y, ElementState state, ElementMatrix mat) {
		ElementState ab = mat.getState(x, y + 1);
		if (ab == null || ab.getElement() == state.getElement()) {
			if (mat.random() < 0.0) {
				return;
			}
		}
		boolean switched = checkAndMove(x, y, state, mat, Direction.Down);
		if (!switched && state.mov == Direction.Down) {
			state.mov = mat.random() > 0.5 ? Direction.Left : Direction.Right;
		}
		if (mat.random() < 0.0) {
			return;
		}
		if (!switched) {
			switched = checkAndMove(x, y, state, mat, state.mov);
		}
		if (!switched) {
			switched = checkAndMove(x, y, state, mat, state.mov == Direction.Left ? Direction.Right : Direction.Left);
		}
	}

	private static boolean checkAndMove(int x, int y, ElementState state, ElementMatrix mat, Direction d) {
		if (canSwitch(state, mat.getState(x + d.dx, y + d.dy), mat)) {
			mat.switchStates(x, y, x + d.dx, y + d.dy);
			state.mov = d;
			return true;
		}
		return false;
	}

	private static boolean checkAndMove2(int x, int y, ElementState state, ElementMatrix mat, Direction d) {
		if (canSwitch(state, mat.getState(x + d.dx, y), mat)
				&& canSwitch(state, mat.getState(x + d.dx, y + d.dy), mat)) {
			mat.switchStates(x, y, x + d.dx, y);
			mat.switchStates(x + d.dx, y, x, y + d.dy);
			state.mov = d;
			return true;
		}
		return false;
	}

	private static boolean canSwitch(ElementState state, ElementState next, ElementMatrix mat) {
		if (next == null) {
			return false;
		}
		if (next.isFixed()) {
			return false;
		}
		if (next.isFluidLike()) {
			return state.getDensity() > next.getDensity();
		}
		return !mat.hasElement(next.x, next.y);
	}

}
