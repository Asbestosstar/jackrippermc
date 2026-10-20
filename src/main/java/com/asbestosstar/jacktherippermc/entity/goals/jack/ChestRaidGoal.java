package com.asbestosstar.jacktherippermc.entity.goals.jack;

import java.util.EnumSet;

import com.asbestosstar.jacktherippermc.entity.JackTheRipperEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

/**
 * On chest raid nights, Jack walks to a nearby locked chest and stands by it.
 * If left undisturbed, he gets inside, steals the contents and escapes. Stolen
 * items are lost if he escapes, but dropped if he is killed first.
 */
public class ChestRaidGoal extends Goal {

	public static int APPROACH = 0;
	public static int WAITING = 1;
	public static int ESCAPING = 2;

	public JackTheRipperEntity jack;
	public BlockPos chest;
	public int phase;
	public int ticks;
	public int searchCooldown;

	public ChestRaidGoal(JackTheRipperEntity jack) {
		this.jack = jack;

		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (!(this.jack.level() instanceof ServerLevel level)) {
			return false;
		}

		if (this.jack.getTarget() != null || !JackTheRipperEntity.isChestRaidNight(level)) {
			return false;
		}

		if (this.searchCooldown > 0) {
			this.searchCooldown--;
			return false;
		}

		this.searchCooldown = JackTheRipperEntity.CHEST_SEARCH_INTERVAL;

		this.chest = JackTheRipperEntity.findLockedChest(level, this.jack.blockPosition());

		if (this.chest == null) {
			return false;
		}

		this.phase = APPROACH;
		this.ticks = 0;

		return true;
	}

	@Override
	public boolean canContinueToUse() {
		if (this.phase == ESCAPING) {
			return true;
		}

		return this.chest != null && !this.isDisturbed();
	}

	@Override
	public void start() {
		this.jack.getNavigation().moveTo(this.chest.getX() + 0.5D, this.chest.getY(), this.chest.getZ() + 0.5D, 1.0D);
	}

	@Override
	public void tick() {
		if (!(this.jack.level() instanceof ServerLevel level)) {
			return;
		}

		if (this.phase == ESCAPING) {
			if (--this.ticks <= 0) {
				this.jack.discard();
			}
			return;
		}

		if (this.phase == APPROACH) {
			if (this.jack.blockPosition().distSqr(this.chest) <= 4.0D) {
				this.phase = WAITING;
				this.ticks = 0;
				this.jack.getNavigation().stop();
			} else if (this.jack.getNavigation().isDone()) {
				this.start();
			}
			return;
		}

		// WAITING
		this.jack.getLookControl().setLookAt(this.chest.getX() + 0.5D, this.chest.getY() + 0.5D,
				this.chest.getZ() + 0.5D);

		if (++this.ticks < JackTheRipperEntity.CHEST_STAND_TICKS) {
			return;
		}

		this.steal(level);

		this.jack.teleportTo(this.chest.getX() + 0.5D, this.chest.getY(), this.chest.getZ() + 0.5D);

		this.phase = ESCAPING;
		this.ticks = JackTheRipperEntity.ESCAPE_TICKS;
	}

	@Override
	public void stop() {
		if (this.phase != ESCAPING) {
			this.jack.getNavigation().stop();
			this.chest = null;
		}
	}

	public boolean isDisturbed() {
		if (this.jack.getTarget() != null || this.jack.hurtTime > 0) {
			return true;
		}

		if (!(this.jack.level() instanceof ServerLevel level)) {
			return true;
		}

		if (!(level.getBlockEntity(this.chest) instanceof ChestBlockEntity chestEntity) || !chestEntity.isLocked()) {
			return true;
		}

		if (this.phase == WAITING) {
			AABB area = new AABB(this.chest).inflate(JackTheRipperEntity.CHEST_DISTURB_RANGE);

			return !this.jack.level().getEntitiesOfClass(Player.class, area, p -> !p.isCreative() && !p.isSpectator())
					.isEmpty();
		}

		return false;
	}

	public void steal(ServerLevel level) {
		if (!(level.getBlockEntity(this.chest) instanceof ChestBlockEntity chestEntity)) {
			return;
		}

		for (int i = 0; i < chestEntity.getContainerSize(); i++) {
			ItemStack stack = chestEntity.getItem(i);

			if (!stack.isEmpty()) {
				this.jack.stolenItems.add(stack.copy());
				chestEntity.setItem(i, ItemStack.EMPTY);
			}
		}

		chestEntity.setChanged();
	}
}
