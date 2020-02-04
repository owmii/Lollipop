package owmii.lib.util;

import net.minecraft.nbt.CompoundNBT;

public interface IVariant<E extends Enum<?> & IVariant> {
    @SuppressWarnings("unchecked")
    default E get() {
        return (E) this;
    }

    default E from(int i) {
        return getAll()[i];
    }

    E[] getAll();

    default E read(CompoundNBT nbt, String key) {
        return from(nbt.getInt(key));
    }

    default CompoundNBT write(CompoundNBT nbt, E e, String key) {
        nbt.putInt(key, get().ordinal());
        return nbt;
    }

    default boolean isEmpty() {
        return this instanceof IVariant.Single || getAll().length == 0;
    }

    @SuppressWarnings("unchecked")
    static <T extends IVariant> T getEmpty() {
        return (T) new Single();
    }

    class Single implements IVariant {
        @Override
        public Enum<?>[] getAll() {
            return new Enum[0];
        }
    }
}
