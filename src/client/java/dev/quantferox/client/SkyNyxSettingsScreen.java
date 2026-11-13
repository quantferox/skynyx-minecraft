package dev.quantferox.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;

public final class SkyNyxSettingsScreen extends Screen {

	private static final int BTN_W = 220;
	private static final int BTN_H = 24;
	private static final int GAP = 8;

	private final CreateWorldScreen parent;

	private SkyNyxSettingsScreen(CreateWorldScreen parent, WorldCreationContext context) {
		super(Component.literal("\u00a7bSkyNyx \u00a77\u2014 World Type"));
		this.parent = parent;
	}

	public static PresetEditor create() {
		return SkyNyxSettingsScreen::new;
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int startY = height / 2 - (3 * (BTN_H + GAP)) / 2;

		addRenderableWidget(Button.builder(
				Component.literal("\u00a7f\u2726 \u00a7fVoid  \u00a77\u2014 Floating island above nothing"),
				b -> apply(WorldKind.VOID))
				.bounds(cx - BTN_W / 2, startY, BTN_W, BTN_H).build());

		addRenderableWidget(Button.builder(
				Component.literal("\u00a79\u2726 \u00a7fWater  \u00a77\u2014 Island surrounded by ocean"),
				b -> apply(WorldKind.WATER))
				.bounds(cx - BTN_W / 2, startY + BTN_H + GAP, BTN_W, BTN_H).build());

		addRenderableWidget(Button.builder(
				Component.literal("\u00a7a\u2726 \u00a7fGrass  \u00a77\u2014 Island on flat plains"),
				b -> apply(WorldKind.GRASS))
				.bounds(cx - BTN_W / 2, startY + 2 * (BTN_H + GAP), BTN_W, BTN_H).build());

		addRenderableWidget(Button.builder(
				Component.literal("Cancel"),
				b -> minecraft.setScreenAndShow(parent))
				.bounds(cx - 50, startY + 3 * (BTN_H + GAP) + 8, 100, 20).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		extractMenuBackground(gui);
		int cx = width / 2;
		int startY = height / 2 - (3 * (BTN_H + GAP)) / 2;
		gui.centeredText(font, title, cx, startY - 28, 0xFFFFFF);
		gui.centeredText(font, Component.literal("\u00a77Choose the world environment for your island"), cx, startY - 14, 0xAAAAAA);
		super.extractRenderState(gui, mouseX, mouseY, partialTick);
	}

	private void apply(WorldKind kind) {
		parent.getUiState().updateDimensions(buildUpdater(kind));
		minecraft.setScreenAndShow(parent);
	}

	private WorldCreationContext.DimensionsUpdater buildUpdater(WorldKind kind) {
		return (registryAccess, currentDims) -> {
			var biomeSource = registryAccess.lookupOrThrow(Registries.BIOME);

			FlatLevelGeneratorSettings flat = switch (kind) {
				case VOID -> new FlatLevelGeneratorSettings(
						Optional.empty(),
						biomeSource.getOrThrow(Biomes.THE_VOID),
						List.of()
				);
				case WATER -> new FlatLevelGeneratorSettings(
						Optional.empty(),
						biomeSource.getOrThrow(Biomes.DEEP_OCEAN),
						List.of()
				).withBiomeAndLayers(List.of(
						new FlatLayerInfo(1, Blocks.BEDROCK),
						new FlatLayerInfo(128, Blocks.WATER)
				), Optional.empty(), biomeSource.getOrThrow(Biomes.DEEP_OCEAN));
				case GRASS -> new FlatLevelGeneratorSettings(
						Optional.empty(),
						biomeSource.getOrThrow(Biomes.PLAINS),
						List.of()
				).withBiomeAndLayers(List.of(
						new FlatLayerInfo(1, Blocks.BEDROCK),
						new FlatLayerInfo(127, Blocks.DIRT),
						new FlatLayerInfo(1, Blocks.GRASS_BLOCK)
				), Optional.empty(), biomeSource.getOrThrow(Biomes.PLAINS));
			};

			flat.updateLayers();
			return currentDims.replaceOverworldGenerator(registryAccess, new FlatLevelSource(flat));
		};
	}

	private enum WorldKind { VOID, WATER, GRASS }
}
