package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockJukeBox extends BlockTileEntity {

    public static final BlockStateBoolean HAS_RECORD = BlockStateBoolean.of("has_record");

    protected BlockJukeBox() {
        super(Material.WOOD, MaterialMapColor.l);
        this.w(this.blockStateList.getBlockData().set(BlockJukeBox.HAS_RECORD, Boolean.FALSE));
        this.a(CreativeModeTab.c);
    }

    @SuppressWarnings("unused")
    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.BLOCK_ENTITY, new DataInspectorItem("RecordPlayer", "RecordItem"));
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, @Nullable ItemStack itemstack, EnumDirection enumdirection, float f, float f1, float f2) {
        if (iblockdata.get(BlockJukeBox.HAS_RECORD)) {
            this.dropRecord(world, blockposition, iblockdata);
            iblockdata = iblockdata.set(BlockJukeBox.HAS_RECORD, Boolean.FALSE);
            world.setTypeAndData(blockposition, iblockdata, 2);
            return true;
        } else {
            return false;
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, ItemStack itemstack) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof BlockJukeBox.TileEntityRecordPlayer) {
                ((BlockJukeBox.TileEntityRecordPlayer) tileentity).setRecord(itemstack.cloneItemStack());
                world.setTypeAndData(blockposition, iblockdata.set(BlockJukeBox.HAS_RECORD, Boolean.TRUE), 2);
            }
        }
    }

    public void dropRecord(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof BlockJukeBox.TileEntityRecordPlayer) {
                BlockJukeBox.TileEntityRecordPlayer blockjukebox_tileentityrecordplayer = (BlockJukeBox.TileEntityRecordPlayer) tileentity;
                ItemStack itemstack = blockjukebox_tileentityrecordplayer.getRecord();

                if (itemstack != null) {
                    world.triggerEffect(1010, blockposition, 0);
                    world.a(blockposition, (SoundEffect) null);
                    blockjukebox_tileentityrecordplayer.setRecord(null);
                    float f = 0.7F;
                    double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                    double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                    ItemStack itemstack1 = itemstack.cloneItemStack();
                    EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack1);

                    entityitem.q();
                    world.addEntity(entityitem);
                }
            }
        }
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.dropRecord(world, blockposition, iblockdata);
        super.remove(world, blockposition, iblockdata);
    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {
        if (!world.isClientSide) {
            super.dropNaturally(world, blockposition, iblockdata, f, 0);
        }
    }

    public TileEntity a(World world, int i) {
        return new BlockJukeBox.TileEntityRecordPlayer();
    }

    @SuppressWarnings("deprecation")
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @SuppressWarnings("deprecation")
    public int d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof BlockJukeBox.TileEntityRecordPlayer) {
            ItemStack itemstack = ((BlockJukeBox.TileEntityRecordPlayer) tileentity).getRecord();

            if (itemstack != null) {
                return Item.getId(itemstack.getItem()) + 1 - Item.getId(Items.RECORD_13);
            }
        }

        return 0;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @SuppressWarnings("deprecation")
    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockJukeBox.HAS_RECORD, i > 0);
    }

    public int toLegacyData(IBlockData iblockdata) {
        return iblockdata.get(BlockJukeBox.HAS_RECORD) ? 1 : 0;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, BlockJukeBox.HAS_RECORD);
    }

    public static class TileEntityRecordPlayer extends TileEntity {

        private ItemStack record;

        public TileEntityRecordPlayer() {}

        public void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            if (nbttagcompound.hasKeyOfType("RecordItem", 10)) {
                this.setRecord(ItemStack.createStack(nbttagcompound.getCompound("RecordItem")));
            } else if (nbttagcompound.getInt("Record") > 0) {
                this.setRecord(new ItemStack(Item.getById(nbttagcompound.getInt("Record"))));
            }

        }

        public NBTTagCompound save(NBTTagCompound nbttagcompound) {
            super.save(nbttagcompound);
            if (this.getRecord() != null) {
                nbttagcompound.set("RecordItem", this.getRecord().save(new NBTTagCompound()));
            }

            return nbttagcompound;
        }

        @Nullable
        public ItemStack getRecord() {
            return this.record;
        }

        public void setRecord(@Nullable ItemStack itemstack) {
            // CraftBukkit start - There can only be one
            if (itemstack != null) {
                itemstack.count = 1;
            }
            // CraftBukkit end
            this.record = itemstack;
            this.update();
        }
    }
}
