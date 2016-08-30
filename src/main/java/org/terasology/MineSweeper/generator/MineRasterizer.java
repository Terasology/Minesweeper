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

import org.terasology.math.geom.BaseVector3i;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.BlockUri;
import org.terasology.world.chunks.ChunkConstants;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.WorldRasterizerPlugin;
import org.terasology.world.generator.plugin.RegisterPlugin;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
public class MineRasterizer  implements WorldRasterizer, WorldRasterizerPlugin {
    private Block mine;
    private Block counter;


    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        counter= blockManager.getBlock(new BlockUri("MineSweeper:Counter"));
        mine = blockManager.getBlock(new BlockUri("MineSweeper:Mine"));

    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        MineFieldFacet oreFacet = chunkRegion.getFacet(MineFieldFacet.class);
        for (Entry<BaseVector3i, Mine> entry : houseFacet.getWorldEntries().entrySet()) {

        }
    }
}
