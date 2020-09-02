package owmii.lib.registry;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import owmii.lib.block.IBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class Registry<T extends IForgeRegistryEntry<T>> {
    private final List<T> objects;
    private final Class<T> clazz;
    private final String id;
    private boolean frozen;

    public Registry(Class<T> clazz, String id) {
        this(clazz, new ArrayList<>(), id);
    }

    public Registry(Class<T> clazz, Registry<T> registry, String id) {
        this(clazz, registry.objects, id);
    }

    public Registry(Class<T> clazz, List<T> objects, String id) {
        this.clazz = clazz;
        this.id = id;
        this.objects = new ArrayList<>(objects);
    }

    public <E extends Entity> EntityType<E> register(String name, EntityType.IFactory<E> factory, EntityClassification classification, float width, float height, int updateInterval, int range, boolean sendVelocity) {
        EntityType<E> entityType = EntityType.Builder.create(factory, classification).size(width, height).setUpdateInterval(updateInterval).setTrackingRange(range).setShouldReceiveVelocityUpdates(sendVelocity).build(name);
        register(name, (T) entityType);
        return entityType;
    }

    public <E extends TileEntity> TileEntityType<E> register(String name, Supplier<? extends E> factory, Block... blocks) {
        TileEntityType<E> type = TileEntityType.Builder.create((Supplier) factory, blocks).build(null);
        register(name, (T) type);
        return type;
    }

    public <O extends T> O register(String name, O object) {
        object.setRegistryName(new ResourceLocation(this.id, name));
        this.objects.add(object);
        return object;
    }

    public void init() {
        if (this.frozen) return;
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(this.clazz, this::registerAll);
        this.frozen = true;
    }

    public void registerAll(RegistryEvent.Register<T> event) {
        this.objects.forEach(t -> event.getRegistry().register(t));
    }

    public void forEach(Consumer<T> action) {
        this.objects.forEach(action);
    }

    public Registry<Item> getBlockItems(@Nullable ItemGroup group) {
        Registry<Item> reg = new Registry<>(Item.class, this.id);
        for (T object : this.objects) {
            if (object instanceof Block) {
                Block block = (Block) object;
                ResourceLocation rl = block.getRegistryName();
                if (block instanceof IBlock) {
                    reg.register(rl.getPath(), ((IBlock) object).getBlockItem(new Item.Properties(), group));
                } else {
                    Item.Properties properties = new Item.Properties();
                    if (group != null) properties.group(group);
                    reg.register(rl.getPath(), new BlockItem((Block) object, properties));
                }
            } else break;
        }
        return reg;
    }

    public List<T> getObjects() {
        return this.objects;
    }
}
