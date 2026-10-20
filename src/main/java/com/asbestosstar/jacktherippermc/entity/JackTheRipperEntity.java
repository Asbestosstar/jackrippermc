package com.asbestosstar.jacktherippermc.entity;

import java.util.ArrayList;
import java.util.List;

import com.asbestosstar.jacktherippermc.entity.goals.jack.ChestRaidGoal;
import com.asbestosstar.jacktherippermc.entity.goals.jack.StalkAndSlashGoal;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

public class JackTheRipperEntity extends PathfinderMob {

	public static long NIGHT_START = 13000L;
	public static long NIGHT_END = 23000L;
	public static long DAY_LENGTH = 24000L;

	public static double MAX_GAP = 3.0D;
	public static double TELEPORT_RANGE = 32.0D;
	public static double BASE_SLASH_DAMAGE = 1.0D;

	public static int CHEST_SEARCH_RADIUS = 16;
	public static int CHEST_SEARCH_INTERVAL = 200;
	public static int CHEST_STAND_TICKS = 3 * 60 * 20;
	public static double CHEST_DISTURB_RANGE = 10.0D;
	public static int ESCAPE_TICKS = 100;

	/**
	 * Items Jack has stolen from chests. Dropped if he is killed before escaping.
	 */
	public List<ItemStack> stolenItems = new ArrayList<>();

	public JackTheRipperEntity(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
	}

	/**
	 * Some Jack nights also have chest raids, decided per world day.
	 */
	public static boolean isChestRaidNight(ServerLevel level) {
		if (!isJackNight(level))
			return false;

		long day = level.getOverworldClockTime() / DAY_LENGTH;
		long worldSeed = level.getSeed();

		return RandomSource.create(worldSeed + day).nextFloat() < 0.5F;
	}

	public static BlockPos findLockedChest(ServerLevel level, BlockPos origin) {
		BlockPos closest = null;
		double bestDistance = Double.MAX_VALUE;

		for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-CHEST_SEARCH_RADIUS, -4, -CHEST_SEARCH_RADIUS),
				origin.offset(CHEST_SEARCH_RADIUS, 4, CHEST_SEARCH_RADIUS))) {
			if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest && chest.isLocked()) {
				double distance = pos.distSqr(origin);

				if (distance < bestDistance) {
					bestDistance = distance;
					closest = pos.immutable();
				}
			}
		}

		return closest;
	}

	@Override
	public void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
		super.dropCustomDeathLoot(level, source, recentlyHit);

		for (ItemStack stack : this.stolenItems) {
			this.spawnAtLocation(level, stack);
		}

		this.stolenItems.clear();
	}

	/**
	 * Natural spawns only happen at night, on nights chosen for the world day, and
	 * never within 16 blocks of an anime girl. Commands bypass these additional
	 * restrictions.
	 */
	public static boolean checkSpawnRules(EntityType<JackTheRipperEntity> type, ServerLevelAccessor level,
			EntitySpawnReason reason, BlockPos pos, RandomSource random) {

		if (reason == EntitySpawnReason.NATURAL) {
			if (!isJackNight(level.getLevel())) {
				return false;
			}

			AABB area = new AABB(pos).inflate(16.0D);

			if (!level.getLevel().getEntitiesOfClass(AnimeGirlEntity.class, area).isEmpty()) {
				return false;
			}
		}

		return Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
	}

	public static boolean isJackNight(ServerLevel level) {
		long time = level.getOverworldClockTime();
		long timeOfDay = time % DAY_LENGTH;

		if (timeOfDay < NIGHT_START || timeOfDay >= NIGHT_END) {
			return false;
		}

		long day = time / DAY_LENGTH;

		RandomSource random = RandomSource.create(level.getSeed() ^ (day * 0x9E3779B97F4A7C15L));

		return random.nextFloat() < 0.3F;
	}

	/**
	 * Returns true if the entity is wearing anything in its chest slot.
	 */
	public static boolean hasChestplate(LivingEntity entity) {
		return !entity.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
	}

	/**
	 * Jack only hunts players who are alive, not in creative or spectator mode, and
	 * not wearing a chestplate.
	 */
	public static boolean isHuntable(Player player) {
		return player.isAlive() && !player.isCreative() && !player.isSpectator() && !hasChestplate(player);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.25D)
				.add(Attributes.ATTACK_DAMAGE, BASE_SLASH_DAMAGE);
	}

	@Override
	public void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));

		this.goalSelector.addGoal(1, new StalkAndSlashGoal(this));

		this.goalSelector.addGoal(2, new ChestRaidGoal(this));

		this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));

		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));

		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

		this.targetSelector.addGoal(0, new HurtByTargetGoal(this));

		this.targetSelector.addGoal(1,
				new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::canHuntTarget));
	}

	public boolean canHuntTarget(LivingEntity target, ServerLevel level) {
		if (!(target instanceof Player)) {
			return false;
		}

		Player player = (Player) target;

		return isHuntable(player);
	}

}