package de.pcfreak9000.pixelsimtest;

public class ElementStateThermo {
    
    public static void spreadHeat(ElementState state, ElementMatrix mat, float dt) {
        Direction[] ds = Direction.VONNEUMANN_NEIGHBOURS;
        float temp = state.getTemperature();
        Direction smallestTemp = Direction.Zero;
        float cursmall = Float.POSITIVE_INFINITY;
        float transferHeatSum = 0;
        ElementState[] affected = new ElementState[4];
        float[] transfer = new float[4];
        int index = 0;
        for (Direction d : ds) {
            int x = state.x + d.dx;
            int y = state.y + d.dy;
            if (mat.checkBounds(x, y)) {
                ElementState side = mat.getState(x, y);
                float stem = side.getTemperature();
                if (cursmall > stem) {
                    cursmall = stem;
                    smallestTemp = d;
                }
                if (temp - stem > 0.0001f) {
                    float effectiveLambda = Math.min(state.heattransfercoefficient, side.heattransfercoefficient);
                    float local = effectiveLambda * (temp - stem);
                    transfer[index] = local * dt;
                    transferHeatSum += transfer[index];
                    affected[index] = side;
                    index++;
                }
            }
        }
        if (cursmall > temp || index == 0) {
            return;
        }
        ElementState coldestState = mat.getState(state.x + smallestTemp.dx, state.y + smallestTemp.dy);
        float maxheattransferByColdest = (state.heat * coldestState.specificheat
                - coldestState.heat * state.specificheat) / (coldestState.specificheat + state.specificheat);
        float maxheatexchanged = Math.min(transferHeatSum, maxheattransferByColdest);
        for (int i = 0; i < index; i++) {
            float relative = transfer[i] / transferHeatSum;
            float deltaHeat = relative * maxheatexchanged;
            state.heat -= deltaHeat;
            affected[i].heat += deltaHeat;
        }
    }
    
    public static void produceHeat(ElementState state, ElementMatrix mat, float dt) {
        state.heat += state.heatproduction * dt;
    }
}
