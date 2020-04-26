package owmii.lib.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.World;

public abstract class MonsterBase extends MonsterEntity implements IMob {
    protected MonsterBase(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);
    }
}
