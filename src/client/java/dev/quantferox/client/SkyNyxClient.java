package dev.quantferox.client;

import dev.quantferox.SkyNyx;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;

import java.util.HashMap;
import java.util.Optional;

public class SkyNyxClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceKey<WorldPreset> key = ResourceKey.create(
				Registries.WORLD_PRESET,
				SkyNyx.id("skyblock")
		);

		// EDITORS is made mutable via PresetEditorMixin — just add our entry
		PresetEditor.EDITORS.put(Optional.of(key), SkyNyxSettingsScreen.create());
	}
}
