package dev.quantferox.client.mixin;

import dev.quantferox.SkyNyx;
import dev.quantferox.client.SkyNyxSettingsScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public class PresetEditorMixin {

	private static final ResourceKey<WorldPreset> SKYBLOCK_KEY = ResourceKey.create(
			net.minecraft.core.registries.Registries.WORLD_PRESET,
			SkyNyx.id("skyblock")
	);

	@Inject(method = "getPresetEditor", at = @At("RETURN"), cancellable = true)
	private void injectSkyNyxEditor(CallbackInfoReturnable<PresetEditor> cir) {
		WorldCreationUiState self = (WorldCreationUiState) (Object) this;
		if (self.getWorldType().preset().is(SKYBLOCK_KEY)) {
			cir.setReturnValue(SkyNyxSettingsScreen.create());
		}
	}
}
