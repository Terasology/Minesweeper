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

    private SweeperFamilyUpdate mine;
    private  SweeperFamilyUpdate counterFamily;

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);
        mine = (SweeperFamilyUpdate)blockManager.getBlockFamily("MineSweeper:Mine");
        counterFamily = (SweeperFamilyUpdate)blockManager.getBlockFamily("MineSweeper:Counter");

        MineFieldFacet oreFacet = chunkRegion.getFacet(MineFieldFacet.class);
        for (Map.Entry<BaseVector3i, Mine> entry : oreFacet.getWorldEntries().entrySet()) {
            Vector3i center = new Vector3i(entry.getKey());


            boolean isMineLegal = true;
            for (int x = -1;x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Vector3i position = new Vector3i(center).addX(x).addY(y).addZ(z);
                        if(chunk.getBlock(ChunkMath.calcBlockPos(position)).getPrefab().isPresent()) {
                            if (chunk.getBlock(ChunkMath.calcBlockPos(position)).getPrefab().get().hasComponent(ExplosiveMineComponent.class)) {
                                if (mine.getBlockForNumberOfNeighbors((byte) (CalculateNumberofMines(position, chunk, true) + 1)) == null) {
                                    isMineLegal = false;
                                }
                            }
                           else
                            {
                                if (counterFamily.getBlockForNumberOfNeighbors((byte) (CalculateNumberofMines(position, chunk, true) + 1)) == null) {
                                    isMineLegal = false;
                                }
                            }
                        }

                    }
                }
            }
            if(!isMineLegal )
                return;

            chunk.setBlock(ChunkMath.calcBlockPos(center),mine.getBlockForNumberOfNeighbors((byte) CalculateNumberofMines(center,chunk,true)));//mineFamily.getBlockForPlacement(blockEntityRegistry,center));

            for (int x = -1;x <= 1; x++)
            {
                for (int y = -1;y <= 1; y++)
                {
                    for (int z = -1;z <= 1; z++)
                    {
                        Vector3i position = new Vector3i(center).addX(x).addY(y).addZ(z);
                        if(chunk.getBlock(ChunkMath.calcBlockPos(position)).getPrefab().isPresent()) {
                            if (chunk.getBlock(ChunkMath.calcBlockPos(position)).getPrefab().get().hasComponent(ExplosiveMineComponent.class)) {
                                chunk.setBlock(ChunkMath.calcBlockPos(position), mine.getBlockForNumberOfNeighbors((byte) CalculateNumberofMines(position, chunk, true)));
                            } else {
                                chunk.setBlock(ChunkMath.calcBlockPos(position), counterFamily.getBlockForNumberOfNeighbors((byte) CalculateNumberofMines(position, chunk, false)));
                            }
                        }else {
                            chunk.setBlock(ChunkMath.calcBlockPos(position), counterFamily.getBlockForNumberOfNeighbors((byte) CalculateNumberofMines(position, chunk, false)));
                        }
                    }
                }
            }

        }
    }

    private int CalculateNumberofMines(Vector3i center,CoreChunk chunk,boolean isMine)
    {
        int amount  = 0;
        for (int x = -1;x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Vector3i position = new Vector3i(center).addX(x).addY(y).addZ(z);
                    if(chunk.getBlock(ChunkMath.calcBlockPos(position)).getPrefab().isPresent()) {
                        if (chunk.getBlock(ChunkMath.calcBlockPos(position)).getPrefab().get().hasComponent(ExplosiveMineComponent.class)) {
                            amount++;
                        }
                    }
                }
            }
        }
        return amount;
    }

}
