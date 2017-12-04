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

import gnu.trove.map.TByteObjectMap;
import org.terasology.MineSweeper.component.MineComponent;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.AbstractBlockFamily;

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


    public  Block getBlockForPlacement(BlockEntityRegistry blockEntityRegistry, Vector3i location) {
        return blocks.get((byte) getNumberOfMines(blockEntityRegistry,location));
    }

    public  int getNumberOfMines(BlockEntityRegistry blockEntityRegistry, Vector3i location) {
        int numberOfMines = 0;
        for (Vector3i current : Region3i.createFromCenterExtents(location, 1)) {
            if (blockEntityRegistry.getBlockEntityAt(current).hasComponent(MineComponent.class)) {
                numberOfMines++;
            }
        }
        return numberOfMines;
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

    public  Block getBlockForNumberOfNeighbors(byte numberOfMines) {
        return  blocks.get((byte)numberOfMines);
    }

    @Override
    public Iterable<Block> getBlocks() {
        return blocks.valueCollection();
    }
}
