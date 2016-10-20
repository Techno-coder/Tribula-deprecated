package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import javax.annotation.Nullable;
import java.util.*;

// CraftBukkit start
// CraftBukkit end

public class PlayerChunkMap {

    @SuppressWarnings("unchecked")
    private static final Predicate<EntityPlayer> a = new Predicate() {
        public boolean a(@Nullable EntityPlayer entityplayer) {
            return entityplayer != null && !entityplayer.isSpectator();
        }

        public boolean apply(Object object) {
            return this.a((EntityPlayer) object);
        }
    };
    @SuppressWarnings("unchecked")
    private static final Predicate<EntityPlayer> b = new Predicate() {
        public boolean a(@Nullable EntityPlayer entityplayer) {
            return entityplayer != null && (!entityplayer.isSpectator() || entityplayer.x().getGameRules().getBoolean("spectatorsGenerateChunks"));
        }

        public boolean apply(Object object) {
            return this.a((EntityPlayer) object);
        }
    };
    private final WorldServer world;
    private final List<EntityPlayer> managedPlayers = Lists.newArrayList();
    @SuppressWarnings("unchecked")
    private final Long2ObjectMap<PlayerChunk> e = new Long2ObjectOpenHashMap(4096);
    private final Set<PlayerChunk> f = Sets.newHashSet();
    private final List<PlayerChunk> g = Lists.newLinkedList();
    private final List<PlayerChunk> h = Lists.newLinkedList();
    private final List<PlayerChunk> i = Lists.newArrayList();
    private int j;
    private long k;
    private boolean l = true;
    private boolean m = true;
    @SuppressWarnings("unused")
    private boolean wasNotEmpty; // CraftBukkit - add field

    public PlayerChunkMap(WorldServer worldserver, int viewDistance /* Spigot */) {
        this.world = worldserver;
        this.a(viewDistance); // Spigot
    }

    public static int getFurthestViewableBlock(int i) {
        return i * 16 - 16;
    }

    private static long d(int i, int j) {
        return (long) i + 2147483647L | (long) j + 2147483647L << 32;
    }

    public WorldServer getWorld() {
        return this.world;
    }

    public Iterator<Chunk> b() {
        final Iterator iterator = this.i.iterator();

        //noinspection unchecked
        return new AbstractIterator() {
            protected Chunk a() {
                while (true) {
                    if (iterator.hasNext()) {
                        PlayerChunk playerchunk = (PlayerChunk) iterator.next();
                        Chunk chunk = playerchunk.f();

                        if (chunk == null) {
                            continue;
                        }

                        if (!chunk.v() && chunk.isDone()) {
                            return chunk;
                        }

                        if (chunk.j()) {
                            return chunk;
                        }

                        if (playerchunk.a(128.0D, PlayerChunkMap.a)) {
                            continue;
                        }

                        return chunk;
                    }

                    return (Chunk) this.endOfData();
                }
            }

            protected Object computeNext() {
                return this.a();
            }
        };
    }

    public void flush() {
        long i = this.world.getTime();
        int j;
        PlayerChunk playerchunk;

        if (i - this.k > 8000L) {
            this.k = i;

            for (j = 0; j < this.i.size(); ++j) {
                playerchunk = this.i.get(j);
                playerchunk.d();
                playerchunk.c();
            }
        }

        if (!this.f.isEmpty()) {
            Iterator iterator = this.f.iterator();

            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                playerchunk = (PlayerChunk) iterator.next();
                playerchunk.d();
            }

            this.f.clear();
        }

        if (this.l && i % 4L == 0L) {
            this.l = false;
            //noinspection unchecked
            Collections.sort(this.h, new Comparator() {
                public int a(PlayerChunk playerchunk, PlayerChunk playerchunk1) {
                    return ComparisonChain.start().compare(playerchunk.g(), playerchunk1.g()).result();
                }

                public int compare(Object object, Object object1) {
                    return this.a((PlayerChunk) object, (PlayerChunk) object1);
                }
            });
        }

        if (this.m && i % 4L == 2L) {
            this.m = false;
            //noinspection unchecked
            Collections.sort(this.g, new Comparator() {
                public int a(PlayerChunk playerchunk, PlayerChunk playerchunk1) {
                    return ComparisonChain.start().compare(playerchunk.g(), playerchunk1.g()).result();
                }

                public int compare(Object object, Object object1) {
                    return this.a((PlayerChunk) object, (PlayerChunk) object1);
                }
            });
        }

        if (!this.h.isEmpty()) {
            long k = System.nanoTime() + 50000000L;
            int l = 49;
            Iterator iterator1 = this.h.iterator();

            while (iterator1.hasNext()) {
                PlayerChunk playerchunk1 = (PlayerChunk) iterator1.next();

                if (playerchunk1.f() == null) {
                    boolean flag = playerchunk1.a(PlayerChunkMap.b);

                    if (playerchunk1.a(flag)) {
                        iterator1.remove();
                        if (playerchunk1.b()) {
                            this.g.remove(playerchunk1);
                        }

                        --l;
                        if (l < 0 || System.nanoTime() > k) {
                            break;
                        }
                    }
                }
            }
        }

        if (!this.g.isEmpty()) {
            j = 81;
            Iterator iterator2 = this.g.iterator();

            while (iterator2.hasNext()) {
                PlayerChunk playerchunk2 = (PlayerChunk) iterator2.next();

                if (playerchunk2.b()) {
                    iterator2.remove();
                    --j;
                    if (j < 0) {
                        break;
                    }
                }
            }
        }

        if (this.managedPlayers.isEmpty()) {
            WorldProvider worldprovider = this.world.worldProvider;

            if (!worldprovider.e()) {
                this.world.getChunkProviderServer().b();
            }
        }

    }

    public boolean a(int i, int j) {
        long k = d(i, j);

        return this.e.get(k) != null;
    }

    @Nullable
    public PlayerChunk getChunk(int i, int j) {
        return this.e.get(d(i, j));
    }
    // CraftBukkit end

    private PlayerChunk c(int i, int j) {
        long k = d(i, j);
        PlayerChunk playerchunk = this.e.get(k);

        if (playerchunk == null) {
            playerchunk = new PlayerChunk(this, i, j);
            this.e.put(k, playerchunk);
            this.i.add(playerchunk);
            if (playerchunk.f() == null) {
                this.h.add(playerchunk);
            }

            if (!playerchunk.b()) {
                this.g.add(playerchunk);
            }
        }

        return playerchunk;
    }

    // CraftBukkit start - add method
    public final boolean isChunkInUse(int x, int z) {
        PlayerChunk pi = getChunk(x, z);
        return pi != null && (pi.c.size() > 0);
    }

    public void flagDirty(BlockPosition blockposition) {
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;
        PlayerChunk playerchunk = this.getChunk(i, j);

        if (playerchunk != null) {
            playerchunk.a(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
        }

    }

    public void addPlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;

        entityplayer.d = entityplayer.locX;
        entityplayer.e = entityplayer.locZ;


        // CraftBukkit start - Load nearby chunks first
        List<ChunkCoordIntPair> chunkList = new LinkedList<ChunkCoordIntPair>();

        for (int k = i - this.j; k <= i + this.j; ++k) {
            for (int l = j - this.j; l <= j + this.j; ++l) {
                chunkList.add(new ChunkCoordIntPair(k, l));
            }
        }

        Collections.sort(chunkList, new ChunkCoordComparator(entityplayer));
        for (ChunkCoordIntPair pair : chunkList) {
            this.c(pair.x, pair.z).a(entityplayer);
        }
        // CraftBukkit end

        this.managedPlayers.add(entityplayer);
        this.e();
    }

    public void removePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.d >> 4;
        int j = (int) entityplayer.e >> 4;

        for (int k = i - this.j; k <= i + this.j; ++k) {
            for (int l = j - this.j; l <= j + this.j; ++l) {
                PlayerChunk playerchunk = this.getChunk(k, l);

                if (playerchunk != null) {
                    playerchunk.b(entityplayer);
                }
            }
        }

        this.managedPlayers.remove(entityplayer);
        this.e();
    }

    private boolean a(int i, int j, int k, int l, int i1) {
        int j1 = i - k;
        int k1 = j - l;

        return (j1 < -i1 || j1 > i1) || (k1 < -i1 || k1 > i1);
    }

    public void movePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;
        double d0 = entityplayer.d - entityplayer.locX;
        double d1 = entityplayer.e - entityplayer.locZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            int k = (int) entityplayer.d >> 4;
            int l = (int) entityplayer.e >> 4;
            int i1 = this.j;
            int j1 = i - k;
            int k1 = j - l;

            List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>(); // CraftBukkit

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (this.a(l1, i2, k, l, i1)) {
                            // this.c(l1, i2).a(entityplayer);
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2)); // CraftBukkit
                        }

                        if (this.a(l1 - j1, i2 - k1, i, j, i1)) {
                            PlayerChunk playerchunk = this.getChunk(l1 - j1, i2 - k1);

                            if (playerchunk != null) {
                                playerchunk.b(entityplayer);
                            }
                        }
                    }
                }

                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;
                this.e();

                // CraftBukkit start - send nearest chunks first
                Collections.sort(chunksToLoad, new ChunkCoordComparator(entityplayer));
                for (ChunkCoordIntPair pair : chunksToLoad) {
                    this.c(pair.x, pair.z).a(entityplayer);
                }
                // CraftBukkit end
            }
        }
    }

    public boolean a(EntityPlayer entityplayer, int i, int j) {
        PlayerChunk playerchunk = this.getChunk(i, j);

        return playerchunk != null && playerchunk.d(entityplayer) && playerchunk.e();
    }

    public void a(int i) {
        i = MathHelper.clamp(i, 3, 32);
        if (i != this.j) {
            int j = i - this.j;
            ArrayList arraylist = Lists.newArrayList(this.managedPlayers);
            Iterator iterator = arraylist.iterator();

            //noinspection WhileLoopReplaceableByForEach
            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();
                int k = (int) entityplayer.locX >> 4;
                int l = (int) entityplayer.locZ >> 4;
                int i1;
                int j1;

                if (j > 0) {
                    for (i1 = k - i; i1 <= k + i; ++i1) {
                        for (j1 = l - i; j1 <= l + i; ++j1) {
                            PlayerChunk playerchunk = this.c(i1, j1);

                            if (!playerchunk.d(entityplayer)) {
                                playerchunk.a(entityplayer);
                            }
                        }
                    }
                } else {
                    for (i1 = k - this.j; i1 <= k + this.j; ++i1) {
                        for (j1 = l - this.j; j1 <= l + this.j; ++j1) {
                            if (this.a(i1, j1, k, l, i)) {
                                this.c(i1, j1).b(entityplayer);
                            }
                        }
                    }
                }
            }

            this.j = i;
            this.e();
        }
    }

    private void e() {
        this.l = true;
        this.m = true;
    }

    public void a(PlayerChunk playerchunk) {
        this.f.add(playerchunk);
    }

    public void b(PlayerChunk playerchunk) {
        ChunkCoordIntPair chunkcoordintpair = playerchunk.a();
        long i = d(chunkcoordintpair.x, chunkcoordintpair.z);

        playerchunk.c();
        this.e.remove(i);
        this.i.remove(playerchunk);
        this.f.remove(playerchunk);
        this.g.remove(playerchunk);
        this.h.remove(playerchunk);
        Chunk chunk = playerchunk.f();

        if (chunk != null) {
            this.getWorld().getChunkProviderServer().unload(chunk);
        }

    }

    // CraftBukkit start - Sorter to load nearby chunks first
    private static class ChunkCoordComparator implements java.util.Comparator<ChunkCoordIntPair> {
        private int x;
        private int z;

        public ChunkCoordComparator (EntityPlayer entityplayer) {
            x = (int) entityplayer.locX >> 4;
            z = (int) entityplayer.locZ >> 4;
        }

        public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
            if (a.equals(b)) {
                return 0;
            }

            // Subtract current position to set center point
            int ax = a.x - this.x;
            int az = a.z - this.z;
            int bx = b.x - this.x;
            int bz = b.z - this.z;

            int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));
            if (result != 0) {
                return result;
            }

            if (ax < 0) {
                if (bx < 0) {
                    return bz - az;
                } else {
                    return -1;
                }
            } else {
                if (bx < 0) {
                    return 1;
                } else {
                    return az - bz;
                }
            }
        }
    }
    // CraftBukkit end
}
