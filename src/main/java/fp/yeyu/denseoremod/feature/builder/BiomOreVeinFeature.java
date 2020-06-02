package fp.yeyu.denseoremod.feature.builder;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.BitSet;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/*
 * This class is the copy of net.minecraft.world.gen.feature.OreFeature
 * with modification to adapt new Target instance in BiomOreFeatureConfig
 * */
public class BiomOreVeinFeature extends Feature<BiomOreVeinFeatureConfig> {
    private static final Logger LOGGER = LogManager.getLogger(BiomOreVeinFeature.class);

    public BiomOreVeinFeature(Function<Dynamic<?>, ? extends BiomOreVeinFeatureConfig> configFactory) {
        super(configFactory);
    }

    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, @Nullable BlockPos blockPos, BiomOreVeinFeatureConfig oreFeatureConfig) {
        if (Objects.isNull(blockPos)) return false;

        LOGGER.info(String.format("Generating %s near %d %d %d", oreFeatureConfig.state.getBlock(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        float f = random.nextFloat() * 3.1415927F;
        float g = (float) oreFeatureConfig.size / 8.0F;
        int i = MathHelper.ceil(((float) oreFeatureConfig.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d = (float) blockPos.getX() + MathHelper.sin(f) * g;
        double e = (float) blockPos.getX() - MathHelper.sin(f) * g;
        double h = (float) blockPos.getZ() + MathHelper.cos(f) * g;
        double j = (float) blockPos.getZ() - MathHelper.cos(f) * g;
        boolean k = true;
        double l = blockPos.getY() + random.nextInt(3) - 2;
        double m = blockPos.getY() + random.nextInt(3) - 2;
        int n = blockPos.getX() - MathHelper.ceil(g) - i;
        int o = blockPos.getY() - 2 - i;
        int p = blockPos.getZ() - MathHelper.ceil(g) - i;
        int q = 2 * (MathHelper.ceil(g) + i);
        int r = 2 * (2 + i);

        for (int s = n; s <= n + q; ++s) {
            for (int t = p; t <= p + q; ++t) {
                if (o <= iWorld.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, s, t)) {
                    return this.generateVeinPart(iWorld, random, oreFeatureConfig, d, e, h, j, l, m, n, o, p, q, r);
                } else {
                    LOGGER.warn(String.format("Cannot place %s at %d %d %d", oreFeatureConfig.state.getBlock(), blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                }
            }
        }

        return false;
    }

    protected boolean generateVeinPart(IWorld world, Random random, BiomOreVeinFeatureConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i) {
        int j = 0;
        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double[] ds = new double[config.size * 4];

        int m;
        double o;
        double p;
        double q;
        double r;
        for (m = 0; m < config.size; ++m) {
            float f = (float) m / (float) config.size;
            o = MathHelper.lerp(f, startX, endX);
            p = MathHelper.lerp(f, startY, endY);
            q = MathHelper.lerp(f, startZ, endZ);
            r = random.nextDouble() * (double) config.size / 16.0D;
            double l = ((double) (MathHelper.sin(3.1415927F * f) + 1.0F) * r + 1.0D) / 2.0D;
            ds[m * 4] = o;
            ds[m * 4 + 1] = p;
            ds[m * 4 + 2] = q;
            ds[m * 4 + 3] = l;
        }

        for (m = 0; m < config.size - 1; ++m) {
            if (ds[m * 4 + 3] > 0.0D) {
                for (int n = m + 1; n < config.size; ++n) {
                    if (ds[n * 4 + 3] > 0.0D) {
                        o = ds[m * 4] - ds[n * 4];
                        p = ds[m * 4 + 1] - ds[n * 4 + 1];
                        q = ds[m * 4 + 2] - ds[n * 4 + 2];
                        r = ds[m * 4 + 3] - ds[n * 4 + 3];
                        if (r * r > o * o + p * p + q * q) {
                            if (r > 0.0D) {
                                ds[n * 4 + 3] = -1.0D;
                            } else {
                                ds[m * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        for (m = 0; m < config.size; ++m) {
            double t = ds[m * 4 + 3];
            if (t >= 0.0D) {
                double u = ds[m * 4];
                double v = ds[m * 4 + 1];
                double w = ds[m * 4 + 2];
                int aa = Math.max(MathHelper.floor(u - t), x);
                int ab = Math.max(MathHelper.floor(v - t), y);
                int ac = Math.max(MathHelper.floor(w - t), z);
                int ad = Math.max(MathHelper.floor(u + t), aa);
                int ae = Math.max(MathHelper.floor(v + t), ab);
                int af = Math.max(MathHelper.floor(w + t), ac);

                for (int ag = aa; ag <= ad; ++ag) {
                    double ah = ((double) ag + 0.5D - u) / t;
                    if (ah * ah < 1.0D) {
                        for (int ai = ab; ai <= ae; ++ai) {
                            double aj = ((double) ai + 0.5D - v) / t;
                            if (ah * ah + aj * aj < 1.0D) {
                                for (int ak = ac; ak <= af; ++ak) {
                                    double al = ((double) ak + 0.5D - w) / t;
                                    if (ah * ah + aj * aj + al * al < 1.0D) {
                                        int am = ag - x + (ai - y) * size + (ak - z) * size * i;
                                        if (!bitSet.get(am)) {
                                            bitSet.set(am);
                                            mutable.set(ag, ai, ak);
                                            if (config.target.getCondition().test(world.getBlockState(mutable))) {
                                                world.setBlockState(mutable, config.state, 2);
                                                ++j;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return j > 0;
    }

}