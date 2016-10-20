package net.minecraft.server;

import org.bukkit.craftbukkit.event.CraftEventFactory;

import java.util.Random;

public class BlockCocoa extends BlockFacingHorizontal implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockStateInteger.of("age", 0, 2);
    protected static final AxisAlignedBB[] b = new AxisAlignedBB[] { new AxisAlignedBB(0.6875D, 0.4375D, 0.375D, 0.9375D, 0.75D, 0.625D), new AxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D), new AxisAlignedBB(0.5625D, 0.3125D, 0.3125D, 0.9375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] c = new AxisAlignedBB[] { new AxisAlignedBB(0.0625D, 0.4375D, 0.375D, 0.3125D, 0.75D, 0.625D), new AxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D), new AxisAlignedBB(0.0625D, 0.3125D, 0.3125D, 0.4375D, 0.75D, 0.6875D)};
    protected static final AxisAlignedBB[] d = new AxisAlignedBB[] { new AxisAlignedBB(0.375D, 0.4375D, 0.0625D, 0.625D, 0.75D, 0.3125D), new AxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D), new AxisAlignedBB(0.3125D, 0.3125D, 0.0625D, 0.6875D, 0.75D, 0.4375D)};
    protected static final AxisAlignedBB[] e = new AxisAlignedBB[] { new AxisAlignedBB(0.375D, 0.4375D, 0.6875D, 0.625D, 0.75D, 0.9375D), new AxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D), new AxisAlignedBB(0.3125D, 0.3125D, 0.5625D, 0.6875D, 0.75D, 0.9375D)};

    public BlockCocoa() {
        super(Material.PLANT);
        this.w(this.blockStateList.getBlockData().set(BlockCocoa.FACING, EnumDirection.NORTH).set(BlockCocoa.AGE, 0));
        this.a(true);
    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (this.e(world, blockposition, iblockdata)) {
            this.f(world, blockposition, iblockdata);
        } else if (world.random.nextInt(Math.max(1, (int) (100.0F / world.spigotConfig.cocoaModifier) * 5)) == 0) { // Spigot
            int i = iblockdata.get(BlockCocoa.AGE);

            if (i < 2) {
                // CraftBukkit start
                IBlockData data = iblockdata.set(AGE, i + 1);
                CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, toLegacyData(data));
                // CraftBukkit end
            }
        }

    }

    public boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        blockposition = blockposition.shift(iblockdata.get(BlockCocoa.FACING));
        IBlockData iblockdata1 = world.getType(blockposition);

        return iblockdata1.getBlock() != Blocks.LOG || iblockdata1.get(BlockLog1.VARIANT) != BlockWood.EnumLogVariant.JUNGLE;
    }

    @SuppressWarnings("deprecation")
    public boolean c(IBlockData iblockdata) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean b(IBlockData iblockdata) {
        return false;
    }

    @SuppressWarnings("deprecation")
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i = iblockdata.get(BlockCocoa.AGE);

        switch (BlockCocoa.SyntheticClass_1.a[iblockdata.get(BlockCocoa.FACING).ordinal()]) {
        case 1:
            return BlockCocoa.e[i];

        case 2:
        default:
            return BlockCocoa.d[i];

        case 3:
            return BlockCocoa.c[i];

        case 4:
            return BlockCocoa.b[i];
        }
    }

    @SuppressWarnings("deprecation")
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockCocoa.FACING, enumblockrotation.a(iblockdata.get(BlockCocoa.FACING)));
    }

    @SuppressWarnings("deprecation")
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a(iblockdata.get(BlockCocoa.FACING)));
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        EnumDirection enumdirection = EnumDirection.fromAngle((double) entityliving.yaw);

        world.setTypeAndData(blockposition, iblockdata.set(BlockCocoa.FACING, enumdirection), 2);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        if (!enumdirection.k().c()) {
            enumdirection = EnumDirection.NORTH;
        }

        return this.getBlockData().set(BlockCocoa.FACING, enumdirection.opposite()).set(BlockCocoa.AGE, 0);
    }

    @SuppressWarnings("deprecation")
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
        if (this.e(world, blockposition, iblockdata)) {
            this.f(world, blockposition, iblockdata);
        }

    }

    private void f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
        this.b(world, blockposition, iblockdata, 0);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        int j = iblockdata.get(BlockCocoa.AGE);
        byte b0 = 1;

        if (j >= 2) {
            b0 = 3;
        }

        for (int k = 0; k < b0; ++k) {
            a(world, blockposition, new ItemStack(Items.DYE, 1, EnumColor.BROWN.getInvColorIndex()));
        }

    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.DYE, 1, EnumColor.BROWN.getInvColorIndex());
    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return iblockdata.get(BlockCocoa.AGE) < 2;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        // CraftBukkit start
        IBlockData data = iblockdata.set(AGE, iblockdata.get(AGE) + 1);
        CraftEventFactory.handleBlockGrowEvent(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this, toLegacyData(data));
        // CraftBukkit end
    }

    @SuppressWarnings("deprecation")
    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockCocoa.FACING, EnumDirection.fromType2(i)).set(BlockCocoa.AGE, (i & 15) >> 2);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | iblockdata.get(BlockCocoa.FACING).get2DRotationValue();

        i |= iblockdata.get(BlockCocoa.AGE) << 2;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockCocoa.FACING, BlockCocoa.AGE);
    }

    @SuppressWarnings("unused")
    static class SyntheticClass_1 {

        @SuppressWarnings("unused")
        static final int[] a = new int[EnumDirection.values().length];

        static {
            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.SOUTH.ordinal()] = 1;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.NORTH.ordinal()] = 2;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.WEST.ordinal()] = 3;
            } catch (NoSuchFieldError ignored) {
            }

            try {
                BlockCocoa.SyntheticClass_1.a[EnumDirection.EAST.ordinal()] = 4;
            } catch (NoSuchFieldError ignored) {
            }

        }
    }
}
