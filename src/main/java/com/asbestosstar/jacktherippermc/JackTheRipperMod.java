package com.asbestosstar.jacktherippermc;

import java.util.ArrayList;
import java.util.List;

import com.asbestosstar.jacktherippermc.entity.AnimeGirlEntity;
import com.asbestosstar.jacktherippermc.entity.JackTheRipperEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(JackTheRipperMod.MODID)
public class JackTheRipperMod {
	public static final String MODID = "jacktheripper";

	public static DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(MODID);

	public static DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);

	public static DeferredHolder<MobEffect, MobEffect> BLEED_MARK = EFFECTS.register("bleed_mark",
			() -> new MobEffect(MobEffectCategory.HARMFUL, 0x8B0000) {// red
			});

	public static DeferredHolder<EntityType<?>, EntityType<JackTheRipperEntity>> JACK_THE_RIPPER = ENTITIES
			.registerEntityType("jack_the_ripper", JackTheRipperEntity::new, MobCategory.MONSTER,
					b -> b.sized(0.6F, 1.8F));

	/**
	 * Every anime girl entity type. Each type's registry path (e.g. "anime_girl_0")
	 * is its variant name.
	 */
	public static List<DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>>> ANIME_GIRLS = new ArrayList<>();
	public static DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> ANIME_GIRL_0 = registerAnimeGirl(
			"anime_girl_0");
	public static DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> ANIME_GIRL_1 = registerAnimeGirl(
			"anime_girl_1");
	public static DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> ANIME_GIRL_2 = registerAnimeGirl(
			"anime_girl_2");

	// Anime girls protect us, they make everthing better, they are the best
	public static DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> registerAnimeGirl(String name) {
		DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> holder = ENTITIES.registerEntityType(name,
				AnimeGirlEntity::new, MobCategory.CREATURE, b -> b.sized(0.6F, 1.8F));
		ANIME_GIRLS.add(holder);
		return holder;
	}

	public JackTheRipperMod(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(this::registerAttributes);
		modEventBus.addListener(this::registerSpawnPlacements);

		ENTITIES.register(modEventBus);
		EFFECTS.register(modEventBus);

	}

	private void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
		event.register(JACK_THE_RIPPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				JackTheRipperEntity::checkSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
	}

	private void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(JACK_THE_RIPPER.get(), JackTheRipperEntity.createAttributes().build());
		for (DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> girl : ANIME_GIRLS) {
			event.put(girl.get(), AnimeGirlEntity.createAttributes().build());
		}
	}

}
