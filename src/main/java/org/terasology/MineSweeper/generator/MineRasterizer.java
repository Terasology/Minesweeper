/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.MineSweeper.generator;

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.MineSweeper.blocks.SweeperFamily;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockRegion;
import org.terasology.world.chunks.Chunks;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.WorldRasterizerPlugin;
import org.terasology.world.generator.plugin.RegisterPlugin;

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
        for (Map.Entry<Vector3ic, MineField> entry : mineFieldFacet.getWorldEntries().entrySet()) {
            Vector3i center = new Vector3i(entry.getKey());
            MineField field = entry.getValue();

            for (org.joml.Vector3i pos : field.getMines()) {
                Vector3i minePos = new Vector3i(center).add(pos);
                if (chunk.getRegion().contains(minePos)) {
                    chunk.setBlock(Chunks.toRelative(minePos, minePos),
                            mine.getBlockForNumberOfNeighbors((byte) field.getNumberOfNeighbors(pos)));
                }
                for (Vector3ic current : new BlockRegion(pos).expand(1, 1, 1)) {
                    Vector3i counterPos = new Vector3i(center).add(current);
                    if (!field.hasMine(current) && chunk.getRegion().contains(counterPos)) {
                        chunk.setBlock(Chunks.toRelative(counterPos, counterPos),
                                counterFamily.getBlockForNumberOfNeighbors((byte) field.getNumberOfNeighbors(current)));
                    }
                }
            }
        }
    }


}
