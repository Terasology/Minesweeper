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

import org.terasology.MineSweeper.blocks.SweeperFamilyUpdate;
import org.terasology.MineSweeper.component.ExplosiveMineComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.protobuf.NetData;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockFamily;
import org.terasology.world.chunks.ChunkConstants;
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
public class MineRasterizer  implements WorldRasterizer, WorldRasterizerPlugin {

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        SweeperFamilyUpdate mine = (SweeperFamilyUpdate) blockManager.getBlockFamily("MineSweeper:Mine");
        SweeperFamilyUpdate counterFamily = (SweeperFamilyUpdate) blockManager.getBlockFamily("MineSweeper:Counter");


        MineFieldFacet mineFieldFacet = chunkRegion.getFacet(MineFieldFacet.class);
        for (Map.Entry<BaseVector3i, MineField> entry : mineFieldFacet.getWorldEntries().entrySet()) {
            Vector3i center = new Vector3i(entry.getKey());
            MineField field = entry.getValue();

            for (Vector3i pos : field.getMines()) {
                Vector3i minePos = new Vector3i(center).add(pos);
                if (chunk.getRegion().encompasses(minePos)) {
                    chunk.setBlock(ChunkMath.calcBlockPos(minePos), mine.getBlockForNumberOfNeighbors((byte) field.getNumberOfNeighbors(pos)));
                }
                for (int x = pos.x - 1; x <= pos.x + 1; x++) {
                    for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                        for (int z = pos.z - 1; z <= pos.z + 1; z++) {
                            Vector3i counterPos = new Vector3i(center).add(x, y, z);
                            if (!field.hasMine(new Vector3i(x, y, z)) && chunk.getRegion().encompasses(counterPos)) {
                                chunk.setBlock(ChunkMath.calcBlockPos(counterPos), counterFamily.getBlockForNumberOfNeighbors((byte) field.getNumberOfNeighbors(new Vector3i(x, y, z))));
                            }
                        }
                    }
                }
            }
        }
    }


}
