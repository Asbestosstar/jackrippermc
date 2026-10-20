package com.asbestosstar.jacktherippermc.client;

import com.asbestosstar.jacktherippermc.JackTheRipperMod;
import com.asbestosstar.jacktherippermc.client.model.JackTheRipperModel;
import com.asbestosstar.jacktherippermc.entity.JackTheRipperEntity;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class JackTheRipperRenderer
		extends HumanoidMobRenderer<JackTheRipperEntity, HumanoidRenderState, JackTheRipperModel> {

	public static ModelLayerLocation LAYER = new ModelLayerLocation(
			Identifier.fromNamespaceAndPath(JackTheRipperMod.MODID, "jack_the_ripper"), "main");

	static Identifier TEXTURE = Identifier.fromNamespaceAndPath(JackTheRipperMod.MODID,
			"textures/entity/jack_the_ripper.png");

	public JackTheRipperRenderer(EntityRendererProvider.Context context) {
		super(context, new JackTheRipperModel(context.bakeLayer(LAYER)), 0.5F);
	}

	@Override
	public HumanoidRenderState createRenderState() {
		return new HumanoidRenderState();
	}

	@Override
	public Identifier getTextureLocation(HumanoidRenderState state) {
		return TEXTURE;
	}

	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(LAYER, () -> JackTheRipperModel.createBodyLayer());
	}

	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(JackTheRipperMod.JACK_THE_RIPPER.get(), JackTheRipperRenderer::new);
	}
}
