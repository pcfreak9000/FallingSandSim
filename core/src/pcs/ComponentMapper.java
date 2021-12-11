package pcs;

import com.badlogic.gdx.utils.ObjectIntMap;

import de.pcfreak9000.pixelsimtest.ElementState;

public class ComponentMapper<C extends AComponent> {
    private static int next = 0;
    private static ObjectIntMap<Class<? extends AComponent>> mappings = new ObjectIntMap<>();
    
    public static int getId(final Class<? extends AComponent> clazz) {
        int m = mappings.get(clazz, -1);
        if (m == -1) {
            m = next++;
            mappings.put(clazz, m);
        }
        return m;
    }
    
    private final int id;
    
    public ComponentMapper(Class<C> component) {
        this.id = getId(component);
    }
    
    public C get(ElementState state) {
      //  return (C) state.getComponent(id);
    }
    
}
