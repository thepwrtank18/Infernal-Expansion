package com.nekomaster1000.infernalexp.world.gen.features;

import com.mojang.serialization.Codec;
import com.nekomaster1000.infernalexp.init.ModBlocks;
import com.nekomaster1000.infernalexp.util.ShapeUtil;
import com.nekomaster1000.infernalexp.world.gen.features.config.GlowSpikeFeatureConfig;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

import java.util.ArrayList;
import java.util.Random;

public class GlowSpikeFeature extends Feature<GlowSpikeFeatureConfig> {

    public GlowSpikeFeature(Codec<GlowSpikeFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader world, ChunkGenerator generator, Random random, BlockPos pos, GlowSpikeFeatureConfig config) {
        int height = config.minHeight + random.nextInt(config.maxHeight - config.minHeight);
        int diameter = config.minDiameter + random.nextInt(config.maxDiameter - config.minDiameter);
        int xOffset = -config.maxXOffset + random.nextInt(config.maxXOffset * 2);
        int zOffset = -config.maxZOffset + random.nextInt(config.maxZOffset * 2);

        if (!world.isAirBlock(pos) || world.getBlockState(pos.down()).getBlock() != ModBlocks.GLOWDUST_SAND.get()) {
            return false;
        } else {
            ArrayList<BlockPos> points = ShapeUtil.generateSolidCircle((float) diameter / 2);

            for (BlockPos point : points) {
                placeLine(world, pos.add(point.getX(), 0, point.getZ()), pos.add(xOffset, height, zOffset), random, config);
            }

            return true;
        }
    }

    /**
     * Places a line of blocks from the startPos to the endPos. It uses the appropriate blocks to make a {@link GlowSpikeFeature}
     *
     * @param world    World blocks are to be placed in
     * @param startPos Start position
     * @param endPos   End position
     */
    private void placeLine(ISeedReader world, BlockPos startPos, BlockPos endPos, Random random, GlowSpikeFeatureConfig config) {
        Vector3d vec1 = new Vector3d(startPos.getX(), startPos.getY(), startPos.getZ());
        Vector3d vec2 = new Vector3d(endPos.getX(), endPos.getY(), endPos.getZ());

        Vector3d diffVec = vec2.subtract(vec1);
        Vector3d incVec = new Vector3d((int) diffVec.x / diffVec.length(), (int) diffVec.y / diffVec.length(), (int) diffVec.z / diffVec.length());

        int lineLength = (int) diffVec.length();

        for (int i = 0; i <= lineLength; i++) {
        	if (vec1.y > 128 || world.getBlockState(new BlockPos(vec1.x, vec1.y, vec1.z)).equals(Blocks.BEDROCK.getDefaultState())) {
        		continue;
        	}

        	// finds what percentage of the line has been built and then adds some randomness to it to make for a
            // more gradual change between blocks
            float percentage = (((float) i / lineLength) - 0.1f) + (random.nextFloat() * 0.2f);

            if (percentage <= 0.33) {
                if (config.darkAtTop)
                    world.setBlockState(new BlockPos(vec1.x, vec1.y, vec1.z), Blocks.GLOWSTONE.getDefaultState(), 10);
                else
                    world.setBlockState(new BlockPos(vec1.x, vec1.y, vec1.z), ModBlocks.DULLSTONE.get().getDefaultState(), 10);
            } else if (percentage > 0.33 && percentage <= 0.66) {
                world.setBlockState(new BlockPos(vec1.x, vec1.y, vec1.z), ModBlocks.DIMSTONE.get().getDefaultState(), 10);
            } else {
                if (config.darkAtTop)
                    world.setBlockState(new BlockPos(vec1.x, vec1.y, vec1.z), ModBlocks.DULLSTONE.get().getDefaultState(), 10);
                else
                    world.setBlockState(new BlockPos(vec1.x, vec1.y, vec1.z), Blocks.GLOWSTONE.getDefaultState(), 10);
            }
            vec1 = vec1.add(incVec);
        }
    }
}
