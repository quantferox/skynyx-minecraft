package dev.quantferox.client;

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

	public enum WorldKind {
		VOID ("\u2726 Void",   0x55FFFF),
		WATER("\u25C6 Water",  0x5555FF),
		GRASS("\u25C6 Grass",  0x55FF55);

		public final String label;
		public final int color;
		WorldKind(String label, int color) { this.label = label; this.color = color; }
	}

	// Remembered across screen opens so the World Type button updates
	public static WorldKind lastSelected = WorldKind.VOID;

	private static final int ROW_W = 200;
	private static final int ROW_H = 28;
	private static final int GAP   = 8;

	private final CreateWorldScreen parent;
	private final WorldCreationContext context;
	private WorldKind selected;

	private SkyNyxSettingsScreen(CreateWorldScreen parent, WorldCreationContext context) {
		super(Component.literal("\u00a7bSkyNyx \u00a77\u2014 World Type"));
		this.parent  = parent;
		this.context = context;
		this.selected = lastSelected;
	}

	public static PresetEditor create() {
		return SkyNyxSettingsScreen::new;
	}

	@Override
	protected void init() {
		int cx = width / 2;
		int rows = WorldKind.values().length;
		int totalH = rows * (ROW_H + GAP) - GAP + 36;
		int startY = height / 2 - totalH / 2;

		for (int i = 0; i < rows; i++) {
			WorldKind kind = WorldKind.values()[i];
			int y = startY + i * (ROW_H + GAP);
			addRenderableWidget(Button.builder(
							Component.literal(kind.label),
							b -> { selected = kind; rebuildWidgets(); })
					.bounds(cx - ROW_W / 2, y, ROW_W, ROW_H).build());
		}

		int doneY = startY + rows * (ROW_H + GAP) + 8;
		addRenderableWidget(Button.builder(Component.literal("Done"),   b -> apply())
				.bounds(cx - 56, doneY, 108, 24).build());
		addRenderableWidget(Button.builder(Component.literal("Cancel"), b -> minecraft.setScreenAndShow(parent))
				.bounds(cx + 56, doneY, 76, 24).build());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor gui, int mouseX, int mouseY, float partialTick) {
		extractMenuBackground(gui);

		int cx = width / 2;
		int rows = WorldKind.values().length;
		int totalH = rows * (ROW_H + GAP) - GAP + 36;
		int startY = height / 2 - totalH / 2;

		gui.centeredText(font, title, cx, startY - 22, 0xFFFFFF);

		for (int i = 0; i < rows; i++) {
			WorldKind kind = WorldKind.values()[i];
			int y = startY + i * (ROW_H + GAP);
			boolean isSel = kind == selected;

			if (isSel) {
				gui.fill(cx - ROW_W / 2 - 2, y - 2, cx + ROW_W / 2 + 2, y + ROW_H + 2, kind.color | 0xFF000000);
				gui.fill(cx - ROW_W / 2 - 1, y - 1, cx + ROW_W / 2 + 1, y + ROW_H + 1, 0xFF111111);
			}
			String radio = isSel ? "\u00a7a\u25CF" : "\u00a77\u25CB";
			gui.text(font, radio, cx - ROW_W / 2 - 14, y + (ROW_H - 8) / 2, 0xFFFFFF);
		}

		super.extractRenderState(gui, mouseX, mouseY, partialTick);
	}

	private void apply() {
		lastSelected = selected;
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
