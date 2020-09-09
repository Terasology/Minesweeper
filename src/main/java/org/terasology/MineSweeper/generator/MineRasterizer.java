// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.generator;

import org.terasology.MineSweeper.blocks.SweeperFamily;
import org.terasology.engine.math.ChunkMath;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.CoreChunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.WorldRasterizerPlugin;
import org.terasology.engine.world.generator.plugin.RegisterPlugin;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;

import java.util.Map;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
public class MineRasterizer implements WorldRasterizer, WorldRasterizerPlugin {

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        SweeperFamily mine = (SweeperFamily) blockManager.getBlockFamily("MineSweeper:Mine");
        SweeperFamily counterFamily = (SweeperFamily) blockManager.getBlockFamily("MineSweeper:Counter");

        MineFieldFacet mineFieldFacet = chunkRegion.getFacet(MineFieldFacet.class);
        for (Map.Entry<BaseVector3i, MineField> entry : mineFieldFacet.getWorldEntries().entrySet()) {
            Vector3i center = new Vector3i(entry.getKey());
            MineField field = entry.getValue();

            for (Vector3i pos : field.getMines()) {
                Vector3i minePos = new Vector3i(center).add(pos);
                if (chunk.getRegion().encompasses(minePos)) {
                    chunk.setBlock(ChunkMath.calcRelativeBlockPos(minePos),
                            mine.getBlockForNumberOfNeighbors((byte) field.getNumberOfNeighbors(pos)));
                }
                for (Vector3i current : Region3i.createFromCenterExtents(pos, 1)) {
                    Vector3i counterPos = new Vector3i(center).add(current);
                    if (!field.hasMine(current) && chunk.getRegion().encompasses(counterPos)) {
                        chunk.setBlock(ChunkMath.calcRelativeBlockPos(counterPos),
                                counterFamily.getBlockForNumberOfNeighbors((byte) field.getNumberOfNeighbors(current)));
                    }
                }
            }
        }
    }


}
