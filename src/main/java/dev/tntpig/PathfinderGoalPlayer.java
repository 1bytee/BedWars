package dev.tntpig;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;

public class PathfinderGoalPlayer extends PathfinderGoalMeleeAttack {

    private EntityCreature creature;

    public PathfinderGoalPlayer(EntityCreature entityCreature, Class<? extends Entity> aClass, double v, boolean b) {
        super(entityCreature, aClass, v, b);
        creature = entityCreature;
    }

    @Override
    public void e() {
        creature.getNavigation().a(creature.getGoalTarget());
    }
}
