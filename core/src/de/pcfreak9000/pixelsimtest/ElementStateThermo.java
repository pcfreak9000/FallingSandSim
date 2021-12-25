package de.pcfreak9000.pixelsimtest;

public class ElementStateThermo {
    
    private static final float MIN_TEMP_DELTA = 0.0001f;
    
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
                if (temp - stem > MIN_TEMP_DELTA) {
                    float effectiveLambda = Math.min(state.getHeatTransferCoefficient(),
                            side.getHeatTransferCoefficient());
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
        float maxheattransferByColdest = (state.getHeat() * coldestState.getSpecificHeat()
                - coldestState.getHeat() * state.getSpecificHeat())
                / (coldestState.getSpecificHeat() + state.getSpecificHeat());
        float maxheatexchanged = Math.min(transferHeatSum, maxheattransferByColdest);
        for (int i = 0; i < index; i++) {
            float relative = transfer[i] / transferHeatSum;
            float deltaHeat = relative * maxheatexchanged;
            state.setHeat(state.getHeat() - deltaHeat);
            affected[i].setHeat(affected[i].getHeat() + deltaHeat);
        }
    }
    
    public static void produceHeat(ElementState state, ElementMatrix mat, float dt) {
        state.setHeat(state.getHeat() + state.getHeatProduction() * dt);
    }
    
    public static void apply(ElementState state, ElementMatrix mat, float dt) {
        produceHeat(state, mat, dt);
        spreadHeat(state, mat, dt);
    }
}
