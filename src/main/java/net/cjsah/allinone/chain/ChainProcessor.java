package net.cjsah.allinone.chain;

import com.google.common.collect.Lists;
import net.cjsah.allinone.scoreboard.Criterion;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;

public class ChainProcessor extends Thread {
    private static final List<BlockPos> OFFSETS = BlockPos.stream(-1, -1, -1, 1, 1, 1).map(BlockPos::toImmutable).filter(pos -> !BlockPos.ORIGIN.equals(pos)).collect(Collectors.toList());
    /**
     * 范围
     */
    public static final int SIZE = 32;
    /**
     * 最大连锁数量
     */
    public static final int MAX = 800;
    private final List<BlockPos> mines = Lists.newArrayList();
    private final List<ItemStack> drops = Lists.newArrayList();
    private final World world;
    private final PlayerEntity player;
    private final ItemStack tool;
    private final BlockPos pos;
    private final Block block;
    private int max;

    public ChainProcessor(World world, PlayerEntity player, BlockPos pos, Block block) {
        this(world, player, pos, block, MAX);
    }

    public ChainProcessor(World world, PlayerEntity player, BlockPos pos, Block block, int max) {
        super("Chain Processor");
        this.world = world;
        this.player = player;
        this.tool = player.getMainHandStack();
        this.pos = pos;
        this.block = block;
        this.max = max;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void run() {
        BlockPos blockPos = this.pos;
        do {
            if (!this.tool.isEmpty() && this.tool.isDamageable() && this.tool.getDamage() + 1 >= this.tool.getMaxDamage()) break;
            try {
                blockPos = this.mines.remove(0);
                BlockState state = this.world.getBlockState(blockPos);
                BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
                this.block.onBreak(this.world, blockPos, state, this.player);
                this.block.onBroken(this.world, blockPos, state);
                state.onStacksDropped((ServerWorld) this.world, blockPos, this.tool);
                this.tool.postMine(this.world, state, blockPos, this.player);
                this.player.getServer().getScoreboard().forEachScore(Criterion.MINED_COUNT, player.getEntityName(), ScoreboardPlayerScore::incrementScore);
                this.player.incrementStat(Stats.MINED.getOrCreateStat(this.block));
                this.player.addExhaustion(0.02F);
                this.drops.addAll(Block.getDroppedStacks(state, (ServerWorld) this.world, blockPos, blockEntity, this.player, this.tool));
                this.world.removeBlock(blockPos, false);
                this.max--;
            }catch (IndexOutOfBoundsException ignore) {}
            for (BlockPos offset : OFFSETS) {
                BlockPos targetPos = blockPos.add(offset);
                if (this.canChain(targetPos)) this.mines.add(targetPos);
            }
        }while (!this.mines.isEmpty() && (!this.tool.isDamageable() || this.tool.getDamage() + 1 < this.tool.getMaxDamage()) && this.max > 0);

        if (!this.player.isCreative()) this.dropItems();
    }

    private boolean contains(BlockPos target) {
        return this.mines.stream().anyMatch(pos -> pos.getX() == target.getX() && pos.getY() == target.getY() && pos.getZ() == target.getZ());
    }

    private boolean canChain(BlockPos target) {
        BlockState state = this.world.getBlockState(target);
        return state.getBlock() == this.block && !this.contains(target) && this.inDistance(target) && target.getY() >=5 && this.player.canHarvest(state);
    }

    private boolean inDistance(BlockPos target) {
        double squareX = MathHelper.square(target.getX() - this.pos.getX());
        double squareY = MathHelper.square(target.getY() - this.pos.getY());
        double squareZ = MathHelper.square(target.getZ() - this.pos.getZ());
        return Math.sqrt(squareX + squareY + squareZ) <= SIZE;
    }

    private void dropItems() {
        // 合并物品防止一次性ItemStack过多溢出导致崩溃
        List<ItemStack> list = Lists.newArrayList();
        for (ItemStack stack : this.drops) {
            if (stack.isStackable()) {
                int index = this.getCombineIndex(list, stack);
                if (index >= 0) {
                    ItemStack itemStack = list.get(index);
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= itemStack.getMaxCount()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                    }else if (itemStack.getCount() < itemStack.getMaxCount()) {
                        stack.decrement(itemStack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(itemStack.getMaxCount());
                    }
                }else if (!stack.isEmpty()) {
                    list.add(stack);
                }
            }
        }
        for (ItemStack drop : list) {
            Block.dropStack(this.world, this.pos, drop);
        }
    }

    private int getCombineIndex(List<ItemStack> list, ItemStack target) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            if (stack.getCount() < stack.getMaxCount() && ItemStack.canCombine(list.get(i), target)) {
                return i;
            }
        }
        return -1;
    }

}
