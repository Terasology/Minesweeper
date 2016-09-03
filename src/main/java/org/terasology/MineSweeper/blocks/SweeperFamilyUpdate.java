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
package org.terasology.MineSweeper.blocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import gnu.trove.map.TByteObjectMap;
import org.terasology.MineSweeper.component.ExplosiveMineComponent;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.AbstractBlockFamily;
import org.terasology.world.block.family.ConnectionCondition;
import org.terasology.world.chunks.CoreChunk;

import java.util.Set;

/**
 * Created by michaelpollind on 8/27/16.
 */
public class SweeperFamilyUpdate extends AbstractBlockFamily {
    private TByteObjectMap<Block> blocks;


    public SweeperFamilyUpdate(BlockUri uri, Iterable<String> categories, TByteObjectMap<Block> blocks) {
        super(uri, categories);
        this.blocks = blocks;
        for (Block block : blocks.valueCollection()) {
            block.setBlockFamily(this);
        }
    }

    @Override
    public Block getBlockForPlacement(WorldProvider worldProvider, BlockEntityRegistry blockEntityRegistry, Vector3i location, Side attachmentSide, Side direction) {

        return getBlockForPlacement(blockEntityRegistry,location);
    }


    public  Block getBlockForPlacement(BlockEntityRegistry blockEntityRegistry, Vector3i location)
    {
        int numberOfMines = 0;
        for (int x = -1;x <= 1; x++)
        {
            for (int y = -1;y <= 1; y++)
            {
                for (int z = -1;z <= 1; z++)
                {
                    if(blockEntityRegistry.getBlockEntityAt(new Vector3i(location).addX(x).addY(y).addZ(z)).hasComponent(ExplosiveMineComponent.class))
                    {
                        numberOfMines++;
                    }
                }
            }
        }

        return blocks.get((byte) numberOfMines);
    }



    @Override
    public Block getArchetypeBlock() {
        return blocks.get((byte) 0);
    }

    @Override
    public Block getBlockFor(BlockUri blockUri) {

        if (getURI().equals(blockUri.getFamilyUri())) {
            try {
                byte connections = Byte.parseByte(blockUri.getIdentifier().toString());
                return blocks.get(connections);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    public  Block getBlockForNumberOfNeighbors(byte numberOfMines)
    {
        return  blocks.get((byte)numberOfMines);
    }

    @Override
    public Iterable<Block> getBlocks() {
        return blocks.valueCollection();
    }
}
