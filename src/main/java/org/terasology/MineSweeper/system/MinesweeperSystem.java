// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.system;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MineSweeper.component.CountComponent;
import org.terasology.MineSweeper.component.FloatingCountComponent;
import org.terasology.MineSweeper.component.MineComponent;
import org.terasology.engine.entitySystem.entity.EntityManager;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.entitySystem.event.ReceiveEvent;
import org.terasology.engine.entitySystem.systems.BaseComponentSystem;
import org.terasology.engine.entitySystem.systems.RegisterMode;
import org.terasology.engine.entitySystem.systems.RegisterSystem;
import org.terasology.engine.logic.actions.ActionTarget;
import org.terasology.engine.logic.common.ActivateEvent;
import org.terasology.engine.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.engine.logic.destruction.DoDestroyEvent;
import org.terasology.engine.logic.location.LocationComponent;
import org.terasology.engine.math.Region3i;
import org.terasology.engine.math.Side;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.rendering.logic.FloatingTextComponent;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.WorldProvider;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockComponent;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.entity.CreateBlockDropsEvent;
import org.terasology.engine.world.block.family.BlockFamily;
import org.terasology.explosives.logic.ExplosionActionComponent;
import org.terasology.math.geom.Vector3i;

import java.util.Map;
import java.util.Queue;
import java.util.Set;


/**
 * System for handling minefields.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class MinesweeperSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(MinesweeperSystem.class);
    @In
    WorldProvider worldProvider;
    @In
    BlockManager blockManager;
    @In
    private EntityManager entityManager;
    @In
    private BlockEntityRegistry blockEntityRegistry;

    @Override
    public void initialise() {
    }

    /**
     * Used to obtain the amount of mines in a field.
     * <p>
     * This method searches through the given region, then repeats with the same sized region centered around each mine
     * found. This is done until no more mines are found.
     *
     * @param point The starting point of the search.
     * @param padding The amount of blocks to go out each iteration of the search.
     * @return A Map containing an EntityRef for each mine and its corresponding position as a Vector3i
     */
    private Map<EntityRef, Vector3i> getMinesInRegion(Vector3i point, int padding) {
        Map<EntityRef, Vector3i> mines = Maps.newHashMap();
        Queue<Vector3i> targets = Queues.newArrayDeque();
        targets.add(point);

        while (targets.size() > 0) {
            Vector3i target = targets.remove();
            for (Vector3i loc : Region3i.createFromCenterExtents(target, padding)) {
                EntityRef blockEntity = blockEntityRegistry.getEntityAt(loc);
                if (!mines.containsKey(blockEntity) && blockEntity.hasComponent(MineComponent.class)) {
                    targets.add(loc);
                    mines.put(blockEntity, loc);
                }
            }
        }
        return mines;
    }

    /**
     * Used for obtaining all of the mines within a 3x3x3 block cube
     *
     * @param point The center of the cube to be searched.
     * @return A set containing all of the mines located within the area.
     */
    private Set<EntityRef> getNeighboringMines(Vector3i point) {
        Set<EntityRef> mines = Sets.newHashSet();
        for (Vector3i loc : Region3i.createFromCenterExtents(point, 1)) {
            EntityRef blockEntity = blockEntityRegistry.getEntityAt(loc);
            if (blockEntity.hasComponent(MineComponent.class)) {
                mines.add(blockEntity);
            }
        }
        return mines;
    }


//    @ReceiveEvent(components = {CountComponent.class})
//    public void whenCounterBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
//
//        BlockComponent blockComponent = blockEntity.getComponent(BlockComponent.class);
//
//        Set<EntityRef> mines =  getNeighboringMines(blockComponent.getPosition());
//        EntityRef ref = entityManager.create();
//        ref.addComponent(new LocationComponent()).setWorldPosition(blockComponent.getPosition().toVector3f());
//        ref.addComponent(new FloatingCountComponent()).neighbors = mines.size();
//        for (Iterator<EntityRef> it = mines.iterator(); it.hasNext(); ) {
//            EntityRef mine = it.next();
//            ref.setOwner(mine);
//            break;
//
//        }
//
//        event.consume();
//    }
//
//
//    @ReceiveEvent(components = {FloatingCountComponent.class}, priority = EventPriority.PRIORITY_HIGH)
//    public void onFloatingNumberDestroted(OnChangedBlock event, EntityRef entity, FloatingCountComponent 
//    floatingNumberComponent) {
//        if (!floatingNumberComponent.isReady) {
//            floatingNumberComponent.isReady = true;
//            entity.saveComponent(floatingNumberComponent);
//            return;
//        }
//        floatingNumberComponent.floatingNumber.destroy();
//    }


    /**
     * Destroys item for mines dropped
     */
    @ReceiveEvent(components = {MineComponent.class})
    public void whenMineBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
        event.consume();
    }

    /**
     * Destroys item for counters dropped
     */
    @ReceiveEvent(components = {CountComponent.class})
    public void whenCounterBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
        event.consume();
    }

    /**
     * Creates a floating counter to replace the block destroyed
     *
     * @param event The event being received
     * @param entity The entity at the position of the event
     * @param blockComponent The block component of the counter being destroyed
     * @param counter The counter component of the counter being destroyed
     */
    @ReceiveEvent
    public void onCounterDestroyed(DoDestroyEvent event, EntityRef entity, BlockComponent blockComponent,
                                   CountComponent counter) {
        Set<EntityRef> mines = getNeighboringMines(blockComponent.getPosition());
        if (mines.size() == 0)
            return;
        EntityRef ref = entityManager.create();
        ref.addComponent(new LocationComponent()).setWorldPosition(blockComponent.getPosition().toVector3f());
        ref.addComponent(new FloatingCountComponent()).neighbors = mines.size();

        FloatingTextComponent floatingTextComponent = new FloatingTextComponent();
        ref.addComponent(floatingTextComponent).text = mines.size() + "";

    }

    /**
     * Creates an explosion when the player breaks a mine
     *
     * @param event The event being received
     * @param entity The entity at the position of the event
     * @param blockComponent The block component of the mine being destroyed
     * @param mineComponent The mine component of the mine being destroyed
     */
    @ReceiveEvent
    public void onMineDestroyed(DoDestroyEvent event, EntityRef entity, BlockComponent blockComponent,
                                MineComponent mineComponent) {
        ExplosionActionComponent component = new ExplosionActionComponent();
        component.relativeTo = ActionTarget.Self;
        component.maxRange = 120;
        entity.addComponent(component);
        entity.send(new DelayedActionTriggeredEvent("Delayed Explosion"));

        for (EntityRef entityRef : entityManager.getEntitiesWith(FloatingCountComponent.class)) {
            Vector3i pos = new Vector3i(entityRef.getComponent(LocationComponent.class).getWorldPosition());
            if (getNeighboringMines(pos).size() == 0) {
                entityRef.destroy();
            }

        }
    }

    /**
     * Marks blocks when interacted with, clears the field and adds a reward if all mines have been marked
     *
     * @param event The event being received
     * @param entity The entity at the position of the event
     * @param blockComponent The block component of the block being marked
     * @param counterComponent The counter component of the block being marked
     */
    @ReceiveEvent
    public void onMark(ActivateEvent event, EntityRef entity, BlockComponent blockComponent,
                       CountComponent counterComponent) {
        BlockFamily blockFamily = blockComponent.getBlock().getBlockFamily();
        if (blockFamily != null) {
            if (blockFamily.getArchetypeBlock().equals(blockComponent.getBlock())) {
                worldProvider.setBlock(blockComponent.getPosition(),
                        blockFamily.getBlockForPlacement(blockComponent.getPosition(), Side.TOP, Side.TOP));
            } else {
                worldProvider.setBlock(blockComponent.getPosition(), blockFamily.getArchetypeBlock());
                Map<EntityRef, Vector3i> mines = getMinesInRegion(blockComponent.getPosition(), 3);
                if (mines.values().stream().allMatch(position -> worldProvider.getBlock(position).equals(blockFamily.getArchetypeBlock()))) {
                    BlockManager blockManager = CoreRegistry.get(BlockManager.class);
                    Block air = blockManager.getBlock(BlockManager.AIR_ID);
                    for (EntityRef mine : mines.keySet())
                        for (Vector3i pos : Region3i.createFromCenterExtents(mines.get(mine), 1)) {
                            blockEntityRegistry.getBlockEntityAt(pos).destroy();
                            worldProvider.setBlock(pos, air);
                        }
                    Vector3i pos = new Vector3i(blockComponent.getPosition());
                    int size = mines.size();
                    if (size >= 68)
                        worldProvider.setBlock(pos, blockManager.getBlock("CoreAssets:DiamondOre"));
                    else if (size >= 56)
                        worldProvider.setBlock(pos, blockManager.getBlock("CoreAssets:GoldOre"));
                    else if (size >= 44)
                        worldProvider.setBlock(pos, blockManager.getBlock("CoreAssets:CopperOre"));
                    else if (size >= 32)
                        worldProvider.setBlock(pos, blockManager.getBlock("CoreAssets:IronOre"));
                    else
                        worldProvider.setBlock(pos, blockManager.getBlock("CoreAssets:CoalOre"));
                }
            }
        }
    }
}
