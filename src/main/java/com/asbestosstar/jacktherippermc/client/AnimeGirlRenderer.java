package com.asbestosstar.jacktherippermc.client;

import com.asbestosstar.jacktherippermc.JackTheRipperMod;
import com.asbestosstar.jacktherippermc.entity.AnimeGirlEntity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AnimeGirlRenderer
		extends HumanoidMobRenderer<AnimeGirlEntity, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {

	public Identifier texture;

	public AnimeGirlRenderer(EntityRendererProvider.Context context, String name) {
		super(context, new HumanoidModel<>(context.bakeLayer(layer(name))), 0.5F);
		this.texture = Identifier.fromNamespaceAndPath(JackTheRipperMod.MODID, "textures/entity/" + name + ".png");
	}

	public static ModelLayerLocation layer(String name) {
		return new ModelLayerLocation(Identifier.fromNamespaceAndPath(JackTheRipperMod.MODID, name), "main");
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public Identifier getTextureLocation(HumanoidRenderState state) {
		return this.texture;
	}

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		for (DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> girl : JackTheRipperMod.ANIME_GIRLS) {
			event.registerLayerDefinition(layer(girl.getId().getPath()),
					() -> LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64));
		}
	}

	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		for (DeferredHolder<EntityType<?>, EntityType<AnimeGirlEntity>> girl : JackTheRipperMod.ANIME_GIRLS) {
			String name = girl.getId().getPath();
			event.registerEntityRenderer(girl.get(), ctx -> new AnimeGirlRenderer(ctx, name));
		}
	}
}
