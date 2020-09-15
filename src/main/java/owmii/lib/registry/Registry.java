package owmii.lib.registry;

import com.google.common.collect.Lists;
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
import owmii.lib.item.ItemBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings({"unchecked", "ConstantConditions"})
public class Registry<T extends IForgeRegistryEntry<T>> {
    private final LinkedHashMap<ResourceLocation, List<T>> objects;
    private final Class<T> clazz;
    private final String id;
    private boolean frozen;

    public Registry(Class<T> clazz, String id) {
        this(clazz, new LinkedHashMap<>(), id);
    }

    public Registry(Class<T> clazz, Registry<T> registry, String id) {
        this(clazz, registry.objects, id);
    }

    public Registry(Class<T> clazz, LinkedHashMap<ResourceLocation, List<T>> objects, String id) {
        this.clazz = clazz;
        this.id = id;
        this.objects = new LinkedHashMap<>(objects);
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

    public <O extends T> O register(String name, O o) {
        boolean flag = true;
        if (o instanceof IVariantEntry) {
            IVariantEntry v = (IVariantEntry) o;
            if (!(v.getVariant() instanceof IVariant.Single)) {
                if (!(o instanceof ItemBlock))
                    name += "_" + v.getVariant().getName();
                o.setRegistryName(new ResourceLocation(this.id, name));
                ResourceLocation key = v.getSiblingsKey((IVariantEntry) o);
                List<T> sibling = getSiblings(key);
                sibling.add(o);
                this.objects.put(key, sibling);
                flag = false;
            }
        }
        if (o instanceof IRegistryObject) {
            ((IRegistryObject) o).setRegistry(this);
        }
        if (flag) {
            o.setRegistryName(new ResourceLocation(this.id, name));
            this.objects.put(o.getRegistryName(), Lists.newArrayList(o));
        }
        return o;
    }

    public List<T> getSiblings(String name) {
        return getSiblings(new ResourceLocation(this.id, name));
    }

    public List<T> getSiblings(ResourceLocation key) {
        return this.objects.getOrDefault(key, new ArrayList<>());
    }

    public void init() {
        if (this.frozen) return;
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(this.clazz, this::registerAll);
        this.frozen = true;
    }

    public void registerAll(RegistryEvent.Register<T> event) {
        forEach(t -> event.getRegistry().register(t));
    }

    public Registry<Item> getBlockItems(@Nullable ItemGroup group) {
        Registry<Item> reg = new Registry<>(Item.class, this.id);
        forEach(object -> {
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
            }
        });
        return reg;
    }

    public void forEach(Consumer<T> action) {
        this.objects.forEach((rl, ts) -> ts.forEach(action));
    }

    public LinkedHashMap<ResourceLocation, List<T>> getObjects() {
        return this.objects;
    }

    public String getId() {
        return this.id;
    }
}
