package net.minecraft.server;

import javax.annotation.Nullable;

public class TileEntityCommand extends TileEntity {

    private final CommandBlockListenerAbstract i = new CommandBlockListenerAbstract() {
        {
            sender = new org.bukkit.craftbukkit.command.CraftBlockCommandSender(this); // CraftBukkit - add sender
        }
        public BlockPosition getChunkCoordinates() {
            return TileEntityCommand.this.position;
        }

        public Vec3D d() {
            return new Vec3D((double) TileEntityCommand.this.position.getX() + 0.5D, (double) TileEntityCommand.this.position.getY() + 0.5D, (double) TileEntityCommand.this.position.getZ() + 0.5D);
        }

        public World getWorld() {
            return TileEntityCommand.this.getWorld();
        }

        public void setCommand(String s) {
            super.setCommand(s);
            TileEntityCommand.this.update();
        }

        public void i() {
            IBlockData iblockdata = TileEntityCommand.this.world.getType(TileEntityCommand.this.position);

            TileEntityCommand.this.getWorld().notify(TileEntityCommand.this.position, iblockdata, iblockdata, 3);
        }

        public Entity f() {
            return null;
        }

        public MinecraftServer h() {
            return TileEntityCommand.this.world.getMinecraftServer();
        }
    };
    private boolean a;
    private boolean f;
    private boolean g;
    private boolean h;

    public TileEntityCommand() {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        this.i.a(nbttagcompound);
        nbttagcompound.setBoolean("powered", this.g());
        nbttagcompound.setBoolean("conditionMet", this.i());
        nbttagcompound.setBoolean("auto", this.h());
        return nbttagcompound;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.i.b(nbttagcompound);
        this.a(nbttagcompound.getBoolean("powered"));
        this.c(nbttagcompound.getBoolean("conditionMet"));
        this.b(nbttagcompound.getBoolean("auto"));
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        if (this.j()) {
            this.d(false);
            NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

            return new PacketPlayOutTileEntityData(this.position, 2, nbttagcompound);
        } else {
            return null;
        }
    }

    public boolean isFilteredNBT() {
        return true;
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.i;
    }

    @SuppressWarnings("unused")
    public CommandObjectiveExecutor e() {
        return this.i.o();
    }

    public void a(boolean flag) {
        this.a = flag;
    }

    public boolean g() {
        return this.a;
    }

    public boolean h() {
        return this.f;
    }

    public void b(boolean flag) {
        boolean flag1 = this.f;

        this.f = flag;
        if (!flag1 && flag && !this.a && this.world != null && this.k() != TileEntityCommand.Type.SEQUENCE) {
            Block block = this.getBlock();

            if (block instanceof BlockCommand) {
                BlockPosition blockposition = this.getPosition();
                BlockCommand blockcommand = (BlockCommand) block;

                this.g = this.l() || blockcommand.e(this.world, blockposition, this.world.getType(blockposition));
                this.world.a(blockposition, block, block.a(this.world));
                if (this.g) {
                    blockcommand.c(this.world, blockposition);
                }
            }
        }

    }

    public boolean i() {
        return this.g;
    }

    public void c(boolean flag) {
        this.g = flag;
    }

    public boolean j() {
        return this.h;
    }

    public void d(boolean flag) {
        this.h = flag;
    }

    public TileEntityCommand.Type k() {
        Block block = this.getBlock();

        return block == Blocks.COMMAND_BLOCK ? TileEntityCommand.Type.REDSTONE : (block == Blocks.dc ? TileEntityCommand.Type.AUTO : (block == Blocks.dd ? TileEntityCommand.Type.SEQUENCE : TileEntityCommand.Type.REDSTONE));
    }

    public boolean l() {
        IBlockData iblockdata = this.world.getType(this.getPosition());

        return !(iblockdata.getBlock() instanceof BlockCommand) || !iblockdata.get(BlockCommand.b);
    }

    public void z() {
        this.e = null;
        super.z();
    }

    public enum Type {

        SEQUENCE, AUTO, REDSTONE;

        @SuppressWarnings("unused")
        Type() {}
    }
}
