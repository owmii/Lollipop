package owmii.lib.registry;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
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
    private final String id;
    private boolean frozen;

    public Registry(String id) {
        this(id, new ArrayList<>());
    }

    public Registry(String id, Registry<T> registry) {
        this(id, registry.objects);
    }

    public Registry(String id, List<T> objects) {
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
        this.objects.forEach(o -> {
            if (o instanceof Block) ForgeRegistries.BLOCKS.register((Block) o);
            else if (o instanceof Fluid) ForgeRegistries.FLUIDS.register((Fluid) o);
            else if (o instanceof Item) ForgeRegistries.ITEMS.register((Item) o);
            else if (o instanceof Effect) ForgeRegistries.POTIONS.register((Effect) o);
            else if (o instanceof Potion) ForgeRegistries.POTION_TYPES.register((Potion) o);
                // else if (o instanceof Biome) ForgeRegistries.BIOMES.register((Biome) o);TODO
            else if (o instanceof SoundEvent) ForgeRegistries.SOUND_EVENTS.register((SoundEvent) o);
            else if (o instanceof Enchantment) ForgeRegistries.ENCHANTMENTS.register((Enchantment) o);
            else if (o instanceof EntityType<?>) ForgeRegistries.ENTITIES.register((EntityType<?>) o);
            else if (o instanceof TileEntityType<?>) ForgeRegistries.TILE_ENTITIES.register((TileEntityType<?>) o);
            else if (o instanceof ParticleType<?>) ForgeRegistries.PARTICLE_TYPES.register((ParticleType<?>) o);
            else if (o instanceof ContainerType<?>) ForgeRegistries.CONTAINERS.register((ContainerType<?>) o);
            else if (o instanceof Attribute) ForgeRegistries.ATTRIBUTES.register((Attribute) o);
        });
        this.frozen = true;
    }

    public void forEach(Consumer<T> action) {
        this.objects.forEach(action);
    }

    public Registry<Item> getBlockItems(@Nullable ItemGroup group) {
        Registry<Item> reg = new Registry<>(this.id);
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
