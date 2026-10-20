package com.asbestosstar.jacktherippermc.entity.goals.jack;

import java.util.EnumSet;

import com.asbestosstar.jacktherippermc.JackTheRipperMod;
import com.asbestosstar.jacktherippermc.entity.AnimeGirlEntity;
import com.asbestosstar.jacktherippermc.entity.JackTheRipperEntity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

/**
 * Appears in front of the victim and slashes rapidly.
 *
 * Every slash adds a scar through the bleed mark amplifier. Damage and hit
 * chance scale with the victim's attack damage.
 */
public class StalkAndSlashGoal extends Goal {

	public JackTheRipperEntity jack;
	public int attackCooldown;

	public StalkAndSlashGoal(JackTheRipperEntity jack) {
		this.jack = jack;

		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public LivingEntity victim() {
		LivingEntity target = this.jack.getTarget();

		if (target instanceof AnimeGirlEntity girl) {
			if (girl.isAlive()) {
				return girl;
			}

			return null;
		}

		if (target instanceof Player player) {
			if (JackTheRipperEntity.isHuntable(player)) {
				return player;
			}
		}

		return null;
	}

	@Override
	public boolean canUse() {
		LivingEntity victim = victim();

		if (victim == null) {
			return false;
		}

		return this.jack.distanceToSqr(victim) <= JackTheRipperEntity.TELEPORT_RANGE
				* JackTheRipperEntity.TELEPORT_RANGE;
	}

	@Override
	public boolean canContinueToUse() {
		return victim() != null;
	}

	@Override
	public void start() {
		this.attackCooldown = 0;

		teleportInFront(victim());
	}

	@Override
	public void tick() {
		LivingEntity victim = victim();

		if (victim == null) {
			return;
		}

		this.jack.getLookControl().setLookAt(victim);

		if (this.jack.distanceToSqr(victim) > JackTheRipperEntity.MAX_GAP * JackTheRipperEntity.MAX_GAP) {
			teleportInFront(victim);
		}

		if (this.attackCooldown > 0) {
			this.attackCooldown--;
			return;
		}

		if (this.jack.level() instanceof ServerLevel serverLevel) {

			// power is determined by the victims attack damage
			double power = Math.max(0.0D, victim.getAttributeValue(Attributes.ATTACK_DAMAGE) - 1.0D);

			float chance = Math.min(1.0F, 0.3F + (float) power * 0.05F);

			if (this.jack.getRandom().nextFloat() < chance) {
				float damage = (float) (JackTheRipperEntity.BASE_SLASH_DAMAGE + power * 0.6D);

				victim.hurtServer(serverLevel, this.jack.damageSources().mobAttack(this.jack), damage);
			}

			if (victim instanceof Player player) {
				addSlashMark(player);
			}

			this.attackCooldown = 4;
		}
	}

	@Override
	public void stop() {
		this.attackCooldown = 0;

		this.jack.getNavigation().stop();
	}

	/**
	 * Teleports Jack one block in front of the victims facing direction
	 */
	public void teleportInFront(LivingEntity target) {
		if (target == null) {
			return;
		}

		double radians = Math.toRadians(target.getYRot());

		double x = target.getX() - Math.sin(radians);
		double z = target.getZ() + Math.cos(radians);

		this.jack.teleportTo(x, target.getY(), z);

		this.jack.getNavigation().stop();
	}

	/**
	 * Adds a slash mark to the screen
	 * 
	 * @param player
	 */
	public static void addSlashMark(Player player) {
		MobEffectInstance existing = player.getEffect(JackTheRipperMod.BLEED_MARK);

		int amplifier = 0;

		if (existing != null) {
			amplifier = existing.getAmplifier() + 1;

		}

		player.addEffect(new MobEffectInstance(JackTheRipperMod.BLEED_MARK, 200, amplifier));
	}
}
