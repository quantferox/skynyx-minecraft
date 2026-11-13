package dev.quantferox;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkyNyx implements ModInitializer {
	public static final String MOD_ID = "skynyx";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Block LUCKY_ORE = registerBlock("lucky_ore",
			BlockBehaviour.Properties.of()
					.strength(0.5f, 3600000.0f)
					.noLootTable()
					.sound(SoundType.STONE)
					.lightLevel(s -> 7));

	public static final Block LUCKY_UNIVERSAL = registerBlock("lucky_universal",
			BlockBehaviour.Properties.of()
					.strength(0.5f, 3600000.0f)
					.noLootTable()
					.sound(SoundType.NETHER_ORE)
					.lightLevel(s -> 7));

	public static final Item LUCKY_ORE_ITEM = registerBlockItem("lucky_ore", LUCKY_ORE);
	public static final Item LUCKY_UNIVERSAL_ITEM = registerBlockItem("lucky_universal", LUCKY_UNIVERSAL);

	@Override
	public void onInitialize() {
		LOGGER.info("SkyNyx mod initializing...");

		ServerPlayerEvents.JOIN.register(SkyblockIslandGenerator::onPlayerJoin);

		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			Block block = state.getBlock();
			if (block == LUCKY_ORE || block == LUCKY_UNIVERSAL) {
				LuckyBlockDrops.onLuckyBlockBreak(world, player, pos, state);
				return false;
			}
			return true;
		});

	}

	private static Block registerBlock(String name, BlockBehaviour.Properties properties) {
		Identifier blockId = id(name);
		properties.setId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), blockId));
		return Registry.register(BuiltInRegistries.BLOCK, blockId, new Block(properties));
	}

	private static Item registerBlockItem(String name, Block block) {
		Identifier itemId = id(name);
		Item.Properties props = new Item.Properties();
		props.setId(ResourceKey.create(BuiltInRegistries.ITEM.key(), itemId));
		return Registry.register(BuiltInRegistries.ITEM, itemId, new BlockItem(block, props));
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
