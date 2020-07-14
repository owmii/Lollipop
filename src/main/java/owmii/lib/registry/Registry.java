package owmii.lib.registry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ForgeRegistryEntry;
import owmii.lib.block.AbstractBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Registry<T extends ForgeRegistryEntry<T>> implements IRegistry<T> {
    private final List<T> list;
    @Nullable
    private ItemGroup group;

    public Registry() {
        this(new ArrayList<>());
    }

    public Registry(List<T> list) {
        this.list = list;
    }

    public Registry(Registry<T> registry) {
        this.list = registry.list;
    }

    protected T register(String name, T o) {
        o.setRegistryName(name);
        this.list.add(o);
        return o;
    }

    public List<Item> getItems() {
        return this.list.stream()
                .filter(o -> o instanceof AbstractBlock<?>)
                .map(o -> ((AbstractBlock<?>) o).getBlockItem(new Item.Properties(), this.group))
                .collect(Collectors.toList());
    }

    @Override
    public void register(RegistryEvent.Register<T> event) {
        this.list.forEach(event.getRegistry()::register);
    }

    @Nullable
    public ItemGroup getGroup() {
        return this.group;
    }

    public void setGroup(@Nullable ItemGroup group) {
        this.group = group;
    }

    public List<T> getList() {
        return this.list;
    }
}
