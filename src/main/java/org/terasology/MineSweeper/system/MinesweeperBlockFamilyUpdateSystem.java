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
package org.terasology.MineSweeper.system;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MineSweeper.blocks.SweeperFamilyUpdate;
import org.terasology.MineSweeper.component.ExplosiveMineComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.OnChangedBlock;
import org.terasology.world.WorldComponent;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.neighbourUpdate.LargeBlockUpdateFinished;
import org.terasology.world.block.entity.neighbourUpdate.LargeBlockUpdateStarting;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.chunks.Chunk;
import org.terasology.world.chunks.ChunkConstants;
import org.terasology.world.chunks.event.OnChunkGenerated;

import java.util.Set;

/**
 * Created by michaelpollind on 8/28/16.
 */
public class MinesweeperBlockFamilyUpdateSystem extends BaseComponentSystem implements UpdateSubscriberSystem {

    private static final Logger logger = LoggerFactory.getLogger(MinesweeperBlockFamilyUpdateSystem .class);

    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private BlockManager blockManager;

    private int largeBlockUpdateCount;
    private Set<Vector3i> blocksUpdatedInLargeBlockUpdate = Sets.newHashSet();
    private int[] checkOnHeight = {-1, 0, 1};

    private SweeperFamilyUpdate mineFamily;
    private SweeperFamilyUpdate counterFamily;



    @Override
    public void initialise() {

        mineFamily = (SweeperFamilyUpdate) blockManager.getBlockFamily("MineSweeper:Mine");
        counterFamily = (SweeperFamilyUpdate) blockManager.getBlockFamily("MineSweeper:Counter");
    }

    @ReceiveEvent
    public void largeBlockUpdateStarting(LargeBlockUpdateStarting event, EntityRef entity) {
        largeBlockUpdateCount++;
    }

    @ReceiveEvent
    public void largeBlockUpdateFinished(LargeBlockUpdateFinished event, EntityRef entity) {
        largeBlockUpdateCount--;
        if (largeBlockUpdateCount < 0) {
            largeBlockUpdateCount = 0;
            throw new IllegalStateException("LargeBlockUpdateFinished invoked too many times");
        }

        if (largeBlockUpdateCount == 0) {
            notifyNeighboursOfChangedBlocks();
        }
    }

    @ReceiveEvent(priority = EventPriority.PRIORITY_NORMAL)
    public void doDestroy(DoDestroyEvent event, EntityRef entity, BlockComponent blockComponent) {
        Vector3i upBlock = new Vector3i(blockComponent.getPosition());
        upBlock.y += 1;
        Block block = worldProvider.getBlock(upBlock);

        if (block.getBlockFamily() instanceof SweeperFamilyUpdate) {
            blockEntityRegistry.getEntityAt(upBlock).send(new DoDamageEvent(1000, EngineDamageTypes.DIRECT.get()));
        }
    }
/*
    @ReceiveEvent(components = {ConnectsToRailsComponent.class}, netFilter = RegisterMode.AUTHORITY)
    public void onActivate(ActivateEvent event, EntityRef entity) {
        ConnectsToRailsComponent connectsToRailsComponent = entity.getComponent(ConnectsToRailsComponent.class);
        BlockComponent blockComponent = entity.getComponent(BlockComponent.class);
        if (blockComponent == null) {
            return;
        }
        Vector3i targetLocation = blockComponent.getPosition();
        if (connectsToRailsComponent.type == ConnectsToRailsComponent.RAILS.TEE) {
            BlockFamily type = blockManager.getBlockFamily("RailsTBlockInverted");
            Block targetBlock = worldProvider.getBlock(targetLocation);
            changeTBlock(event.getInstigator(), type, targetLocation, targetBlock.getDirection(), targetBlock.getDirection().yawClockwise(2));
        } else if (connectsToRailsComponent.type == ConnectsToRailsComponent.RAILS.TEE_INVERSED) {
            BlockFamily type = blockManager.getBlockFamily("rails:Rails");
            Block targetBlock = worldProvider.getBlock(targetLocation);
            changeTBlock(event.getInstigator(), type, targetLocation, targetBlock.getDirection(), targetBlock.getDirection().yawClockwise(2));
        }
    }*/

    @ReceiveEvent(components = {BlockItemComponent.class, ItemComponent.class}, priority = EventPriority.PRIORITY_HIGH)
    public void onPlaceBlock(ActivateEvent event, EntityRef item) {
        BlockComponent blockComponent = event.getTarget().getComponent(BlockComponent.class);
        if (blockComponent == null) {
            return;
        }
        Vector3i targetBlock = blockComponent.getPosition();
        Block centerBlock = worldProvider.getBlock(targetBlock.x, targetBlock.y, targetBlock.z);

        if (centerBlock.getBlockFamily() instanceof SweeperFamilyUpdate) {
            event.consume();
        }
    }

    private void notifyNeighboursOfChangedBlocks() {
        // Invoke the updates in another large block change for this class only
        largeBlockUpdateCount++;
        while (!blocksUpdatedInLargeBlockUpdate.isEmpty()) {
            Set<Vector3i> blocksToUpdate = blocksUpdatedInLargeBlockUpdate;

            // Setup new collection for blocks changed in this pass
            blocksUpdatedInLargeBlockUpdate = Sets.newHashSet();

            for (Vector3i blockLocation : blocksToUpdate) {
                processUpdateForBlockLocation(blockLocation);
            }
        }
        largeBlockUpdateCount--;
    }


    @ReceiveEvent(components = {BlockComponent.class})
    public void blockUpdate(OnChangedBlock event, EntityRef blockEntity) {
        if (largeBlockUpdateCount > 0) {
            blocksUpdatedInLargeBlockUpdate.add(event.getBlockPosition());
        } else {
            Vector3i blockLocation = event.getBlockPosition();
            processUpdateForBlockLocation(blockLocation);
        }
    }

    private void processUpdateForBlockLocation(Vector3i blockLocation) {
        if(!blockEntityRegistry.getBlockEntityAt(blockLocation).hasComponent(ExplosiveMineComponent.class))
            return;
        for (int x = -1;x <= 1; x++)
        {
            for (int y = -1;y <= 1; y++)
            {
                for (int z = -1;z <= 1; z++)
                {
                    if(x == 0 && y ==0 && z == 0)
                        return;
                    Vector3i position = new Vector3i(blockLocation).addX(x).addY(y).addZ(z);
                    EntityRef ref = blockEntityRegistry.getBlockEntityAt(position);
                    if(ref.hasComponent(ExplosiveMineComponent.class))
                    {
                        worldProvider.setBlock(position,mineFamily.getBlockForPlacement(blockEntityRegistry,position));
                    }
                    else
                    {
                        worldProvider.setBlock(position,counterFamily.getBlockForPlacement(blockEntityRegistry,position));
                    }
                }
            }
        }

    }

    @Override
    public void update(float delta) {
        if (largeBlockUpdateCount > 0) {
            logger.error("Unmatched LargeBlockUpdateStarted - LargeBlockUpdateFinished not invoked enough times");
        }
        largeBlockUpdateCount = 0;
    }

}
