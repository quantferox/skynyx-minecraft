package dev.quantferox;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class SkyblockIslandGenerator {

	private static final int Y = 64;
	private static final int DIST = 16;

	private SkyblockIslandGenerator() {}

	public static void onPlayerJoin(ServerPlayer player) {
		ServerLevel world = player.level();
		if (isGenerated(world)) return;

		world.getChunk(0, 0);
		world.getChunk(1, 0);
		world.getChunk(0, -1);
		world.getChunk(0, 1);

		generateIsland(world);
		generateTree(world);
		generateDecorations(world);
		generateChest(world);
		generateOrePlatform(world);
		generateUniversalPlatform(world);
		generateEndPortalIsland(world);

		sendWelcomeMessage(player);
		player.teleportTo(0.5, Y + 1, 0.5);
	}

	private static boolean isGenerated(ServerLevel world) {
		return world.getBlockState(new BlockPos(0, Y - 6, 0)).getBlock() == Blocks.BEDROCK;
	}

	// ── Main island ─────────────────────────────────────────────

	private static void generateIsland(ServerLevel world) {
		fillCircle(world, Y,     0, 0, 3.5, Blocks.GRASS_BLOCK);
		fillCircle(world, Y - 1, 0, 0, 3.5, Blocks.DIRT);
		fillCircle(world, Y - 2, 0, 0, 3.5, Blocks.DIRT);
		fillCircle(world, Y - 3, 0, 0, 2.5, Blocks.STONE);
		fillCircle(world, Y - 4, 0, 0, 2.5, Blocks.STONE);
		fillCircle(world, Y - 5, 0, 0, 2.5, Blocks.STONE);
		fillCircle(world, Y - 6, 0, 0, 1.5, Blocks.BEDROCK);
	}

	private static void generateTree(ServerLevel world) {
		for (int dy = 1; dy <= 6; dy++) setBlock(world, 0, Y + dy, 0, Blocks.OAK_LOG);
		canopyCircle(world, Y + 4, 2.5);
		canopyCircle(world, Y + 5, 2.5);
		canopyCircle(world, Y + 6, 1.5);
		canopyCircle(world, Y + 7, 1.5);
		setBlock(world, 0, Y + 8, 0, Blocks.OAK_LEAVES);
	}

	private static void generateDecorations(ServerLevel world) {
		int y = Y + 1;
		setBlock(world, -3, y, -1, Blocks.DANDELION);
		setBlock(world,  3, y, -1, Blocks.POPPY);
		setBlock(world, -3, y,  1, Blocks.POPPY);
		setBlock(world,  3, y,  1, Blocks.DANDELION);
		setBlock(world, -2, y,  2, Blocks.SHORT_GRASS);
		setBlock(world,  2, y, -2, Blocks.SHORT_GRASS);
		setBlock(world, -1, y,  3, Blocks.ALLIUM);
		setBlock(world,  1, y, -3, Blocks.CORNFLOWER);
		setBlock(world, -2, y, -2, Blocks.FERN);
		setBlock(world,  2, y,  2, Blocks.OXEYE_DAISY);
		setBlock(world,  0, y,  3, Blocks.SHORT_GRASS);
		setBlock(world,  3, y,  0, Blocks.AZURE_BLUET);
		setBlock(world, -3, y,  0, Blocks.SHORT_GRASS);
		setBlock(world,  0, y, -3, Blocks.RED_TULIP);
		setBlock(world,  1, y,  2, Blocks.LILY_OF_THE_VALLEY);
		setBlock(world, -1, y, -2, Blocks.SHORT_GRASS);
		setBlock(world,  2, y,  1, Blocks.POPPY);
		setBlock(world, -2, y,  1, Blocks.DANDELION);
		setBlock(world,  1, y,  1, Blocks.SHORT_GRASS);
		setBlock(world, -1, y,  1, Blocks.SHORT_GRASS);
		setBlock(world,  0, y,  2, Blocks.WHITE_TULIP);
		setBlock(world,  0, y, -2, Blocks.SHORT_GRASS);
		setBlock(world,  2, y,  0, Blocks.SHORT_GRASS);
		setBlock(world, -2, y,  0, Blocks.FERN);
	}

	private static void generateChest(ServerLevel world) {
		BlockPos chestPos = new BlockPos(0, Y, 1);
		world.setBlockAndUpdate(chestPos, Blocks.CHEST.defaultBlockState());
		if (world.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
			chest.setItem(0, new ItemStack(Items.LAVA_BUCKET));
			chest.setItem(1, new ItemStack(Items.WATER_BUCKET));
		}
	}

	// ── Ore lucky block island (East, +X) ───────────────────────

	private static void generateOrePlatform(ServerLevel world) {
		int cx = DIST, cz = 0;
		fillCircle(world, Y - 3, cx, cz, 4.5, Blocks.STONE);
		fillCircle(world, Y - 2, cx, cz, 4.5, Blocks.SMOOTH_STONE);
		fillCircle(world, Y - 1, cx, cz, 3.5, Blocks.SMOOTH_STONE);
		fillCircle(world, Y,     cx, cz, 2.5, Blocks.STONE_BRICKS);
		setBlock(world, cx - 2, Y, cz, Blocks.SEA_LANTERN);
		setBlock(world, cx + 2, Y, cz, Blocks.SEA_LANTERN);
		setBlock(world, cx, Y, cz - 2, Blocks.SEA_LANTERN);
		setBlock(world, cx, Y, cz + 2, Blocks.SEA_LANTERN);
		setBlock(world, cx, Y + 1, cz, SkyNyx.LUCKY_ORE);
	}

	// ── Universal lucky block island (North, -Z) ────────────────

	private static void generateUniversalPlatform(ServerLevel world) {
		int cx = 0, cz = -DIST;
		fillCircle(world, Y - 3, cx, cz, 4.5, Blocks.DEEPSLATE);
		fillCircle(world, Y - 2, cx, cz, 4.5, Blocks.DEEPSLATE_BRICKS);
		fillCircle(world, Y - 1, cx, cz, 3.5, Blocks.DEEPSLATE_BRICKS);
		fillCircle(world, Y,     cx, cz, 2.5, Blocks.POLISHED_DEEPSLATE);
		setBlock(world, cx - 2, Y, cz, Blocks.GLOWSTONE);
		setBlock(world, cx + 2, Y, cz, Blocks.GLOWSTONE);
		setBlock(world, cx, Y, cz - 2, Blocks.GLOWSTONE);
		setBlock(world, cx, Y, cz + 2, Blocks.GLOWSTONE);
		setBlock(world, cx, Y + 1, cz, SkyNyx.LUCKY_UNIVERSAL);
	}

	// ── End portal island (South, +Z) ───────────────────────────

	private static void generateEndPortalIsland(ServerLevel world) {
		int cx = 0, cz = DIST;
		fillCircle(world, Y - 3, cx, cz, 4.5, Blocks.END_STONE);
		fillCircle(world, Y - 2, cx, cz, 4.5, Blocks.END_STONE_BRICKS);
		fillCircle(world, Y - 1, cx, cz, 4.5, Blocks.END_STONE_BRICKS);
		fillCircle(world, Y,     cx, cz, 3.5, Blocks.END_STONE_BRICKS);
		generateEndPortal(world, cx, cz);
		setBlock(world, cx - 3, Y + 1, cz, Blocks.END_ROD);
		setBlock(world, cx + 3, Y + 1, cz, Blocks.END_ROD);
		setBlock(world, cx, Y + 1, cz - 3, Blocks.END_ROD);
		setBlock(world, cx, Y + 1, cz + 3, Blocks.END_ROD);
	}

	private static void generateEndPortal(ServerLevel world, int cx, int cz) {
		int py = Y + 1;
		for (int dx = -1; dx <= 1; dx++) {
			placeFrame(world, cx + dx, py, cz - 2, Direction.SOUTH);
			placeFrame(world, cx + dx, py, cz + 2, Direction.NORTH);
		}
		for (int dz = -1; dz <= 1; dz++) {
			placeFrame(world, cx - 2, py, cz + dz, Direction.EAST);
			placeFrame(world, cx + 2, py, cz + dz, Direction.WEST);
		}
	}

	private static void placeFrame(ServerLevel world, int x, int y, int z, Direction facing) {
		world.setBlockAndUpdate(new BlockPos(x, y, z),
				Blocks.END_PORTAL_FRAME.defaultBlockState()
						.setValue(EndPortalFrameBlock.FACING, facing));
	}

	// ── Welcome message ────────────────────────────────────────

	private static void sendWelcomeMessage(ServerPlayer player) {
		player.sendSystemMessage(Component.literal("\u00a7f\u2726 \u00a7bSkyNyx \u00a7fSkyblock \u00a7f\u2726"));
		player.sendSystemMessage(Component.literal("\u00a77Your journey begins on a floating island in the void."));
		player.sendSystemMessage(Component.literal("\u00a77A chest rests beneath the great oak \u2014 open it to begin."));
		player.sendSystemMessage(Component.literal("\u00a77Lucky Ore: \u00a7aEast\u00a77 \u00b7 Lucky Blocks: \u00a7bNorth\u00a77 \u00b7 End Portal: \u00a7dSouth"));
	}

	// ── Helpers ────────────────────────────────────────────────

	private static void setBlock(ServerLevel world, int x, int y, int z, Block block) {
		world.setBlockAndUpdate(new BlockPos(x, y, z), block.defaultBlockState());
	}

	private static void fillCircle(ServerLevel world, int y, int cx, int cz, double radius, Block block) {
		BlockState state = block.defaultBlockState();
		int r = (int) Math.ceil(radius);
		double rSq = radius * radius;
		for (int dx = -r; dx <= r; dx++) {
			for (int dz = -r; dz <= r; dz++) {
				if (dx * dx + dz * dz <= rSq) {
					world.setBlockAndUpdate(new BlockPos(cx + dx, y, cz + dz), state);
				}
			}
		}
	}

	private static void canopyCircle(ServerLevel world, int y, double radius) {
		BlockState leaf = Blocks.OAK_LEAVES.defaultBlockState();
		int r = (int) Math.ceil(radius);
		double rSq = radius * radius;
		for (int dx = -r; dx <= r; dx++) {
			for (int dz = -r; dz <= r; dz++) {
				if (dx * dx + dz * dz > rSq) continue;
				BlockPos pos = new BlockPos(dx, y, dz);
				if (world.getBlockState(pos).getBlock() == Blocks.OAK_LOG) continue;
				world.setBlockAndUpdate(pos, leaf);
			}
		}
	}
}
