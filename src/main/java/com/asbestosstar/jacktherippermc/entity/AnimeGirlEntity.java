package com.asbestosstar.jacktherippermc.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AnimeGirlEntity extends PathfinderMob {

	public AnimeGirlEntity(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4.0D);
	}

	@Override
	public void registerGoals() {
		this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
		this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, JackTheRipperEntity.class, 10, true,
				false, this::canHelpAgainstJack));
	}

	boolean canHelpAgainstJack(LivingEntity target, ServerLevel world) {
		if (!(target instanceof JackTheRipperEntity)) {
			return false;
		}
		JackTheRipperEntity jack = (JackTheRipperEntity) target;
		if (!(jack.getTarget() instanceof Player victim)) {
			return false;
		}

		double distanceToVictim = victim.distanceToSqr(this);
		double distanceToJack = jack.distanceToSqr(this);

		double minimumVictimDistance = 100.0D; // If the player is within 10 blocks of the anime girl, Jack will not
												// target that player.
		double maximumJackDistance = 425.0D; // If Jack is within 20 blocks of the anime girl, the anime girl will
												// target Jack to protect the player.

		if (distanceToVictim < minimumVictimDistance) {
			return false;
		}

		if (distanceToJack > maximumJackDistance) {
			return false;
		}

		return true;
	}
}
