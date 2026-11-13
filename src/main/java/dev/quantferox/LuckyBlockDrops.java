package dev.quantferox;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class LuckyBlockDrops {

	private record OreDrop(Item item, int weight, int min, int max) {}
	private static final OreDrop[] ORE_TABLE = {
			new OreDrop(Items.COAL,           25, 1, 1),
			new OreDrop(Items.RAW_IRON,        20, 1, 1),
			new OreDrop(Items.RAW_COPPER,      15, 1, 1),
			new OreDrop(Items.RAW_GOLD,        10, 1, 1),
			new OreDrop(Items.REDSTONE,        10, 1, 4),
			new OreDrop(Items.LAPIS_LAZULI,    7, 1, 4),
			new OreDrop(Items.DIAMOND,          5, 1, 1),
			new OreDrop(Items.EMERALD,          4, 1, 1),
			new OreDrop(Items.ANCIENT_DEBRIS,   4, 1, 1),
	};
	private static final int ORE_TOTAL = 100;

	private static List<Item> allItems;

	private LuckyBlockDrops() {}

	public static void onLuckyBlockBreak(Level world, Player player, BlockPos pos, BlockState state) {
		ItemStack drop = generateDrop(state.getBlock(), world.getRandom());
		if (drop.isEmpty()) return;

		if (!player.getInventory().add(drop)) {
			Block.popResource(world, pos, drop);
		}

		world.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.5f, 1.0f);
	}

	private static ItemStack generateDrop(Block block, RandomSource rand) {
		if (block == SkyNyx.LUCKY_ORE) return rollOre(rand);
		if (block == SkyNyx.LUCKY_UNIVERSAL) return rollUniversal(rand);
		return ItemStack.EMPTY;
	}

	private static ItemStack rollOre(RandomSource rand) {
		int roll = rand.nextInt(ORE_TOTAL);
		int cum = 0;
		for (OreDrop drop : ORE_TABLE) {
			cum += drop.weight;
			if (roll < cum) {
				int count = drop.min == drop.max ? drop.min : drop.min + rand.nextInt(drop.max - drop.min + 1);
				return new ItemStack(drop.item, count);
			}
		}
		return new ItemStack(Items.COAL);
	}

	private static ItemStack rollUniversal(RandomSource rand) {
		List<Item> items = getAllItems();
		return new ItemStack(items.get(rand.nextInt(items.size())), 1);
	}

	private static List<Item> getAllItems() {
		if (allItems == null) {
			Set<Item> oreItems = Set.of(
					Items.COAL, Items.RAW_IRON, Items.RAW_COPPER, Items.RAW_GOLD,
					Items.REDSTONE, Items.LAPIS_LAZULI, Items.DIAMOND, Items.EMERALD, Items.ANCIENT_DEBRIS
			);
			allItems = new ArrayList<>();
			for (Item item : BuiltInRegistries.ITEM) {
				if (item == Items.AIR) continue;
				if (item == SkyNyx.LUCKY_ORE_ITEM || item == SkyNyx.LUCKY_UNIVERSAL_ITEM) continue;
				if (oreItems.contains(item)) continue;
				allItems.add(item);
			}
		}
		return allItems;
	}
}
