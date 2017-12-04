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

import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MineSweeper.blocks.SweeperFamilyUpdate;
import org.terasology.MineSweeper.component.*;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.actions.ActionTarget;
import org.terasology.logic.actions.ExplosionActionComponent;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.delay.DelayedActionTriggeredEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.Region3i;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.rendering.logic.FloatingTextComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.OnChangedBlock;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.CreateBlockDropsEvent;
import org.terasology.world.block.family.BlockFamily;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;


/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class MinesweeperSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(MinesweeperSystem.class);


    @In
    private EntityManager entityManager;

    @In
    private BlockEntityRegistry blockEntityRegistry;

    @In
    WorldProvider worldProvider;

    @In
    BlockManager blockManager;

    @Override
    public void initialise() {
    }

    private Set<EntityRef> getMinesInRegion(Vector3i point, int padding) {
        Set<EntityRef> mines = Sets.newHashSet();
        Queue<Vector3i> targets = Queues.newArrayDeque();
        targets.add(point);

        while (targets.size() > 0) {
            Vector3i target = targets.remove();
            for (Vector3i loc : Region3i.createFromCenterExtents(target, padding)) {
                EntityRef blockEntity = blockEntityRegistry.getEntityAt(loc);
                if (!mines.contains(blockEntity) && blockEntity.hasComponent(MineComponent.class)) {
                    targets.add(loc);
                    mines.add(blockEntity);
                }
            }
        }
        return mines;
    }

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
//    public void onFloatingNumberDestroted(OnChangedBlock event, EntityRef entity, FloatingCountComponent floatingNumberComponent) {
//        if (!floatingNumberComponent.isReady) {
//            floatingNumberComponent.isReady = true;
//            entity.saveComponent(floatingNumberComponent);
//            return;
//        }
//        floatingNumberComponent.floatingNumber.destroy();
//    }


    @ReceiveEvent(components = {MineComponent.class})
    public void whenMineBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
        event.consume();
    }

    @ReceiveEvent(components = {CountComponent.class})
    public void whenCounterBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
        event.consume();
    }


    @ReceiveEvent
    public void onCounterDestroyed(DoDestroyEvent event, EntityRef entity, BlockComponent blockComponent, CountComponent counter) {
        Set<EntityRef> mines = getNeighboringMines(blockComponent.getPosition());
        if (mines.size() == 0)
            return;
        EntityRef ref = entityManager.create();
        ref.addComponent(new LocationComponent()).setWorldPosition(blockComponent.getPosition().toVector3f());
        ref.addComponent(new FloatingCountComponent()).neighbors = mines.size();

        FloatingTextComponent floatingTextComponent = new FloatingTextComponent();
        ref.addComponent(floatingTextComponent).text = mines.size() + "";

    }

    @ReceiveEvent
    public void onMineDestroyed(DoDestroyEvent event, EntityRef entity, BlockComponent blockComponent, MineComponent mineComponent) {
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

    @ReceiveEvent
    public void onMark(ActivateEvent event, EntityRef entity, BlockComponent blockComponent, CountComponent counterComponent) {
        BlockFamily blockFamily = blockComponent.getBlock().getBlockFamily();
        if (blockFamily != null) {
            if (blockFamily.getArchetypeBlock().equals(blockComponent.getBlock())) {
                worldProvider.setBlock(blockComponent.getPosition(), blockFamily.getBlockForPlacement(worldProvider, blockEntityRegistry, blockComponent.getPosition(), Side.TOP, Side.TOP));
            } else {
                worldProvider.setBlock(blockComponent.getPosition(), blockFamily.getArchetypeBlock());
            }
        }
    }
}
