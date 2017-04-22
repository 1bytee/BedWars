package dev.tntpig;

import net.minecraft.server.v1_9_R1.EntityCreature;
import net.minecraft.server.v1_9_R1.PathfinderGoalMeleeAttack;

public class PathfinderGoalPlayer extends PathfinderGoalMeleeAttack {

    private EntityCreature creature;

    public PathfinderGoalPlayer(EntityCreature entityCreature, double v, boolean b) {
        super(entityCreature, v, b);
        creature = entityCreature;
    }

    @Override
    public void e() {
        creature.getNavigation().a(creature.getGoalTarget());
    }
}
