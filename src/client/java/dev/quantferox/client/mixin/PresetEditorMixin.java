package dev.quantferox.client.mixin;

import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Mixin(PresetEditor.class)
public interface PresetEditorMixin {

	@Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
	private static void makeEditorsMutable(CallbackInfo ci) {
		try {
			Field field = PresetEditor.class.getDeclaredField("EDITORS");
			field.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<Optional<ResourceKey<WorldPreset>>, PresetEditor> current =
					(Map<Optional<ResourceKey<WorldPreset>>, PresetEditor>) field.get(null);
			field.set(null, new HashMap<>(current));
		} catch (Exception e) {
			throw new RuntimeException("Failed to make PresetEditor.EDITORS mutable", e);
		}
	}
}
