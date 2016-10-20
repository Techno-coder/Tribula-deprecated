package net.minecraft.server;

import org.bukkit.craftbukkit.command.CraftBlockCommandSender;
import org.bukkit.craftbukkit.command.ProxiedNativeCommandSender;

import javax.annotation.Nullable;

public class TileEntitySign extends TileEntity {

    public final IChatBaseComponent[] lines = new IChatBaseComponent[] { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    private final CommandObjectiveExecutor i = new CommandObjectiveExecutor();
    @SuppressWarnings("unused")
    public int f = -1;
    public boolean isEditable = true;
    private EntityHuman h;

    public TileEntitySign() {}

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }

        // CraftBukkit start
        if (Boolean.getBoolean("convertLegacySigns")) {
            nbttagcompound.setBoolean("Bukkit.isConverted", true);
        }
        // CraftBukkit end

        this.i.b(nbttagcompound);
        return nbttagcompound;
    }

    protected void b(World world) {
        this.a(world);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.a(nbttagcompound);
        ICommandListener icommandlistener = new ICommandListener() {
            public String getName() {
                return "Sign";
            }

            public IChatBaseComponent getScoreboardDisplayName() {
                return new ChatComponentText(this.getName());
            }

            public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

            public boolean a(int i, String s) {
                return true;
            }

            public BlockPosition getChunkCoordinates() {
                return TileEntitySign.this.position;
            }

            public Vec3D d() {
                return new Vec3D((double) TileEntitySign.this.position.getX() + 0.5D, (double) TileEntitySign.this.position.getY() + 0.5D, (double) TileEntitySign.this.position.getZ() + 0.5D);
            }

            public World getWorld() {
                return TileEntitySign.this.world;
            }

            public Entity f() {
                return null;
            }

            public boolean getSendCommandFeedback() {
                return false;
            }

            public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {}

            public MinecraftServer h() {
                return TileEntitySign.this.world.getMinecraftServer();
            }
        };

        // CraftBukkit start - Add an option to convert signs correctly
        // This is done with a flag instead of all the time because
        // we have no way to tell whether a sign is from 1.7.10 or 1.8

        boolean oldSign = Boolean.getBoolean("convertLegacySigns") && !nbttagcompound.getBoolean("Bukkit.isConverted");

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            if (s != null && s.length() > 2048) {
                s = "\"\"";
            }

            try {
                IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);

                if (oldSign) {
                    lines[i] = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(s)[0];
                    continue;
                }
                // CraftBukkit end

                try {
                    this.lines[i] = ChatComponentUtils.filterForDisplay(icommandlistener, ichatbasecomponent, null);
                } catch (CommandException commandexception) {
                    this.lines[i] = ichatbasecomponent;
                }
            } catch (com.google.gson.JsonParseException jsonparseexception) {
                this.lines[i] = new ChatComponentText(s);
            }
        }

        this.i.a(nbttagcompound);
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 9, this.c());
    }

    public NBTTagCompound c() {
        return this.save(new NBTTagCompound());
    }

    public boolean isFilteredNBT() {
        return true;
    }

    public boolean d() {
        return this.isEditable;
    }

    public void a(EntityHuman entityhuman) {
        this.h = entityhuman;
    }

    public EntityHuman e() {
        return this.h;
    }

    @SuppressWarnings("unused")
    public boolean b(final EntityHuman entityhuman) {
        ICommandListener icommandlistener = new ICommandListener() {
            @SuppressWarnings("unused")
            public String getName() {
                return entityhuman.getName();
            }

            @SuppressWarnings("unused")
            public IChatBaseComponent getScoreboardDisplayName() {
                return entityhuman.getScoreboardDisplayName();
            }

            @SuppressWarnings("unused")
            public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

            @SuppressWarnings("unused")
            public boolean a(int i, String s) {
                return i <= 2;
            }

            @SuppressWarnings("unused")
            public BlockPosition getChunkCoordinates() {
                return TileEntitySign.this.position;
            }

            @SuppressWarnings("unused")
            public Vec3D d() {
                return new Vec3D((double) TileEntitySign.this.position.getX() + 0.5D, (double) TileEntitySign.this.position.getY() + 0.5D, (double) TileEntitySign.this.position.getZ() + 0.5D);
            }

            @SuppressWarnings("unused")
            public World getWorld() {
                return entityhuman.getWorld();
            }

            @SuppressWarnings("unused")
            public Entity f() {
                return entityhuman;
            }

            @SuppressWarnings("unused")
            public boolean getSendCommandFeedback() {
                return false;
            }

            @SuppressWarnings("unused")
            public void a(CommandObjectiveExecutor.EnumCommandResult commandobjectiveexecutor_enumcommandresult, int i) {
                if (TileEntitySign.this.world != null && !TileEntitySign.this.world.isClientSide) {
                    TileEntitySign.this.i.a(TileEntitySign.this.world.getMinecraftServer(), this, commandobjectiveexecutor_enumcommandresult, i);
                }

            }

            @SuppressWarnings("unused")
            public MinecraftServer h() {
                return entityhuman.h();
            }
        };
        IChatBaseComponent[] aichatbasecomponent = this.lines;
        int i = aichatbasecomponent.length;

        for (IChatBaseComponent ichatbasecomponent : aichatbasecomponent) {
            ChatModifier chatmodifier = ichatbasecomponent == null ? null : ichatbasecomponent.getChatModifier();

            if (chatmodifier != null && chatmodifier.h() != null) {
                ChatClickable chatclickable = chatmodifier.h();

                if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                    // CraftBukkit start
                    // entityhuman.h().getCommandHandler().a(icommandlistener, chatclickable.b());
                    CommandBlockListenerAbstract.executeCommand(icommandlistener, new ProxiedNativeCommandSender(
                            icommandlistener,
                            new CraftBlockCommandSender(icommandlistener),
                            entityhuman.getBukkitEntity()
                    ), chatclickable.b());
                    // CraftBukkit end
                }
            }
        }

        return true;
    }

    @SuppressWarnings("unused")
    public CommandObjectiveExecutor g() {
        return this.i;
    }
}
