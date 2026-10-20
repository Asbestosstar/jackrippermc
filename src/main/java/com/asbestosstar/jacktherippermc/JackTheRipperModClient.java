package com.asbestosstar.jacktherippermc;

import com.asbestosstar.jacktherippermc.client.AnimeGirlRenderer;
import com.asbestosstar.jacktherippermc.client.JackTheRipperRenderer;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;

@Mod(value = JackTheRipperMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = JackTheRipperMod.MODID, value = Dist.CLIENT)
public class JackTheRipperModClient {
	public JackTheRipperModClient(ModContainer container, IEventBus modBus) {
		modBus.addListener(JackTheRipperRenderer::registerLayerDefinitions);
		modBus.addListener(JackTheRipperRenderer::registerRenderers);
		modBus.addListener(AnimeGirlRenderer::registerLayerDefinitions);
		modBus.addListener(AnimeGirlRenderer::registerRenderers);
	}

}
