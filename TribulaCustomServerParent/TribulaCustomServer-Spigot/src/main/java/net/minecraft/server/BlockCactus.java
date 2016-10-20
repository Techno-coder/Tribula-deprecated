package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory;

import java.util.Iterator;
import java.util.Random;

public class BlockCactus extends Block {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 15);
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 0.9375D, 0.9375D);
    @SuppressWarnings("unused")
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);

    protected BlockCactus() {
        super(Material.CACTUS);
        this.w(this.blockStateList.getBlockData().set(BlockCactus.AGE, 0));
        this.a(true);
        this.a(CreativeModeTab.c);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        BlockPosition blockposition1 = blockposition.up();

        if (world.isEmpty(blockposition1)) {
            int i;

            for (i = 1; world.getType(blockposition.down(i)).getBlock() == this; ++i) {
            }

            if (i < 3) {
                int j = iblockdata.get(BlockCactus.AGE);

                if (j >= (byte) range(3, ((100.0F / world.spigotConfig.cactusModifier) * 15) + 0.5F, 15)) { // Spigot
                    // world.setTypeUpdate(blockposition1, this.getBlockData()); // CraftBukkit
                    IBlockData iblockdata1 = iblockdata.set(BlockCactus.AGE, 0);

                    CraftEventFactory.handleBlockGrowEvent(world, blockposition1.getX(), blockposition1.getY(), blockposition1.getZ(), this, 0); // CraftBukkit
                    world.setTypeAndData(blockposition, iblockdata1, 4);
                    iblockdata1.doPhysics(world, blockposition1, this);
                } else {
                    world.setTypeAndData(blockposition, iblockdata.set(BlockCactus.AGE, j + 1), 4);
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    public AxisAlignedBB a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return BlockCactus.b;
    }

    @SuppressWarnings("deprecation")
    public boolean c(IBlockData iblockdata) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        return super.canPlace(world, blockposition) && this.b(world, blockposition);
    }

    @SuppressWarnings("deprecation")
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
        if (!this.b(world, blockposition)) {
            world.setAir(blockposition, true);
        }

    }

    public boolean b(World world, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        Material material;

        do {
            if (!iterator.hasNext()) {
                Block block = world.getType(blockposition.down()).getBlock();

                return block == Blocks.CACTUS || block == Blocks.SAND && !world.getType(blockposition.up()).getMaterial().isLiquid();
            }

            EnumDirection enumdirection = (EnumDirection) iterator.next();

            material = world.getType(blockposition.shift(enumdirection)).getMaterial();
        } while (!material.isBuildable() && material != Material.LAVA);

        return false;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        CraftEventFactory.blockDamage = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()); // CraftBukkit
        entity.damageEntity(DamageSource.CACTUS, 1.0F);
        CraftEventFactory.blockDamage = null; // CraftBukkit
    }

    @SuppressWarnings("deprecation")
    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCactus.AGE, i);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockCactus.AGE);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockCactus.AGE);
    }
}
