package pcs;

public abstract class AComponent {
    public final int id = ComponentMapper.getId(this.getClass());
}
