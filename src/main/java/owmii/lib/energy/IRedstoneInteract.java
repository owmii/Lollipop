package owmii.lib.energy;

public interface IRedstoneInteract {
    Redstone getRedstone();

    void setRedstone(Redstone mode);

    default void nextRedstoneMode() {
        setRedstone(getRedstone().next());
    }
}
