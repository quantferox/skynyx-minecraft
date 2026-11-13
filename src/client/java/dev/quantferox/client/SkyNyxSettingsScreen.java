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
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;

public final class SkyNyxSettingsScreen extends Screen {

	private enum WorldKind {
		VOID  ("\u2726 Void",   "\u00a77Floating island above the abyss", 0x55FFFF),
		WATER ("\u25C6 Water",  "\u00a77Island surrounded by deep ocean",  0x5555FF),
		GRASS ("\u25C6 Grass",  "\u00a77Island on infinite flat plains",   0x55FF55);

		final String label;
		final String desc;
		final int color;
		WorldKind(String label, String desc, int color) {
			this.label = label; this.desc = desc; this.color = color;
		}
	}

	private static final int ROW_W = 220;
	private static final int ROW_H = 28;
	private static final int GAP   = 6;

	private final CreateWorldScreen parent;
	private final WorldCreationContext context;
	private WorldKind selected = WorldKind.VOID;

	private SkyNyxSettingsScreen(CreateWorldScreen parent, WorldCreationContext context) {
		super(Component.literal("\u00a7bSkyNyx \u00a77\u2014 Choose World Type"));
		this.parent  = parent;
		this.context = context;
	}

	public static PresetEditor create() {
		return SkyNyxSettingsScreen::new;
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int totalH = WorldKind.values().length * (ROW_H + GAP) - GAP + 36;
		int startY = height / 2 - totalH / 2;

		for (int i = 0; i < WorldKind.values().length; i++) {
			WorldKind kind = WorldKind.values()[i];
			int y = startY + i * (ROW_H + GAP);
			addRenderableWidget(Button.builder(
							Component.literal(kind.label),
							b -> { selected = kind; rebuildWidgets(); })
					.bounds(cx - ROW_W / 2, y, ROW_W, ROW_H).build());
		}

		int doneY = startY + WorldKind.values().length * (ROW_H + GAP) + 8;
		addRenderableWidget(Button.builder(
						Component.literal("Done"),
						b -> apply())
				.bounds(cx - 55, doneY, 110, 24).build());
		addRenderableWidget(Button.builder(
						Component.literal("Cancel"),
						b -> minecraft.setScreenAndShow(parent))
				.bounds(cx + 60, doneY, 70, 24).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		extractMenuBackground(gui);

		int cx = width / 2;
		int totalH = WorldKind.values().length * (ROW_H + GAP) - GAP + 36;
		int startY = height / 2 - totalH / 2;

		gui.centeredText(font, title, cx, startY - 24, 0xFFFFFF);

		for (int i = 0; i < WorldKind.values().length; i++) {
			WorldKind kind = WorldKind.values()[i];
			int y = startY + i * (ROW_H + GAP);
			boolean isSelected = kind == selected;

			// highlight border around selected row
			if (isSelected) {
				gui.fill(cx - ROW_W / 2 - 2, y - 2, cx + ROW_W / 2 + 2, y + ROW_H + 2, kind.color | 0xFF000000);
				gui.fill(cx - ROW_W / 2 - 1, y - 1, cx + ROW_W / 2 + 1, y + ROW_H + 1, 0xFF111111);
			}

			// radio indicator
			String radio = isSelected ? "\u00a7a\u25CF" : "\u00a77\u25CB";
			gui.text(font, radio, cx - ROW_W / 2 - 14, y + (ROW_H - 8) / 2, 0xFFFFFF);

			// description under button
			gui.centeredText(font, Component.literal(kind.desc), cx + 20, y + ROW_H + 2 - GAP, isSelected ? kind.color | 0xFF000000 : 0x888888);
		}

		super.extractRenderState(gui, mouseX, mouseY, partialTick);
	}

	private void apply() {
		parent.getUiState().updateDimensions((registryAccess, currentDims) -> {
			var biomes = registryAccess.lookupOrThrow(Registries.BIOME);
			FlatLevelGeneratorSettings flat = switch (selected) {
				case VOID -> new FlatLevelGeneratorSettings(
						Optional.empty(), biomes.getOrThrow(Biomes.THE_VOID), List.of());
				case WATER -> new FlatLevelGeneratorSettings(
						Optional.empty(), biomes.getOrThrow(Biomes.DEEP_OCEAN), List.of()
				).withBiomeAndLayers(List.of(
						new FlatLayerInfo(1, Blocks.BEDROCK),
						new FlatLayerInfo(128, Blocks.WATER)
				), Optional.empty(), biomes.getOrThrow(Biomes.DEEP_OCEAN));
				case GRASS -> new FlatLevelGeneratorSettings(
						Optional.empty(), biomes.getOrThrow(Biomes.PLAINS), List.of()
				).withBiomeAndLayers(List.of(
						new FlatLayerInfo(1, Blocks.BEDROCK),
						new FlatLayerInfo(127, Blocks.DIRT),
						new FlatLayerInfo(1, Blocks.GRASS_BLOCK)
				), Optional.empty(), biomes.getOrThrow(Biomes.PLAINS));
			};
			flat.updateLayers();
			return currentDims.replaceOverworldGenerator(registryAccess, new FlatLevelSource(flat));
		});
		minecraft.setScreenAndShow(parent);
	}
}
