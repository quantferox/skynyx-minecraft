package dev.quantferox.client.mixin;

import dev.quantferox.SkyNyx;
import dev.quantferox.client.SkyNyxSettingsScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.WorldTypeEntry.class)
public class WorldTypeEntryMixin {

	private static final ResourceKey<WorldPreset> SKYBLOCK_KEY = ResourceKey.create(
			net.minecraft.core.registries.Registries.WORLD_PRESET,
			SkyNyx.id("skyblock")
	);

	@Inject(method = "describePreset", at = @At("RETURN"), cancellable = true)
	private void skyNyxDescribe(CallbackInfoReturnable<Component> cir) {
		WorldCreationUiState.WorldTypeEntry self = (WorldCreationUiState.WorldTypeEntry) (Object) this;
		if (self.preset().is(SKYBLOCK_KEY)) {
			SkyNyxSettingsScreen.WorldKind kind = SkyNyxSettingsScreen.lastSelected;
			cir.setReturnValue(Component.literal("\u00a7b" + kind.label));
		}
	}
}
