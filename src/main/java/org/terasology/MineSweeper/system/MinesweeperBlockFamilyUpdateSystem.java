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

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.MineSweeper.component.ExplosiveMineComponent;
import org.terasology.MineSweeper.component.SweeperCountComponent;
import org.terasology.audio.StaticSound;
import org.terasology.audio.events.PlaySoundEvent;
import org.terasology.core.logic.blockDropGrammar.BlockDropGrammarComponent;
import org.terasology.entitySystem.entity.EntityBuilder;
import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.EventPriority;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.actions.ActionTarget;
import org.terasology.logic.actions.ExplosionActionComponent;
import org.terasology.logic.actions.ExplosionAuthoritySystem;
import org.terasology.logic.common.ActivateEvent;
import org.terasology.logic.health.DoDamageEvent;
import org.terasology.logic.health.DoDestroyEvent;
import org.terasology.logic.health.EngineDamageTypes;
import org.terasology.logic.inventory.ItemComponent;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Vector3f;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.utilities.Assets;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockManager;
import org.terasology.world.block.entity.CreateBlockDropsEvent;
import org.terasology.world.block.items.BlockItemComponent;
import org.terasology.world.generation.EntityProvider;

import java.util.List;
import java.util.Optional;


/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class MinesweeperBlockFamilyUpdateSystem extends BaseComponentSystem {

    private static final Logger logger = LoggerFactory.getLogger(MinesweeperBlockFamilyUpdateSystem .class);

    private Random random = new FastRandom();
    private List<Optional<StaticSound>> explosionSounds = Lists.newArrayList();


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
        explosionSounds.add(Assets.getSound("core:explode1"));
        explosionSounds.add(Assets.getSound("core:explode2"));
        explosionSounds.add(Assets.getSound("core:explode3"));
        explosionSounds.add(Assets.getSound("core:explode4"));
        explosionSounds.add(Assets.getSound("core:explode5"));
    }


    @ReceiveEvent(components = {ExplosiveMineComponent.class})
    public void whenMineBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
        event.consume();
    }


    @ReceiveEvent(components = {SweeperCountComponent.class})
    public void whenCounterBlockDropped(CreateBlockDropsEvent event, EntityRef blockEntity) {
        event.consume();
    }

    @ReceiveEvent
    public void onMineDestroyed(DoDestroyEvent event, EntityRef entity, BlockComponent blockComponent,ExplosiveMineComponent mine) {

        ExplosionActionComponent component = new ExplosionActionComponent();
        component.relativeTo = ActionTarget.Self;
        doExplosion(component,blockComponent.getPosition().toVector3f(),entity);

    }

    private StaticSound getRandomExplosionSound() {
        return explosionSounds.get(random.nextInt(0, explosionSounds.size() - 1)).get();
    }

    void doExplosion(ExplosionActionComponent explosionComp, Vector3f origin, EntityRef instigatingBlockEntity) {
        worldProvider.setBlock(new Vector3i(origin), blockManager.getBlock(BlockManager.AIR_ID));


        EntityBuilder builder = entityManager.newBuilder("engine:smokeExplosion");
        builder.getComponent(LocationComponent.class).setWorldPosition(origin);
        EntityRef smokeEntity = builder.build();

        smokeEntity.send(new PlaySoundEvent(getRandomExplosionSound(), 1f));

        Vector3i blockPos = new Vector3i();
        for (int i = 0; i < explosionComp.maxRange; i++) {
            Vector3f direction = random.nextVector3f(1.0f);

            for (int j = 0; j < 4; j++) {
                Vector3f target = new Vector3f(origin);

                target.x += direction.x * j;
                target.y += direction.y * j;
                target.z += direction.z * j;
                blockPos.set((int) target.x, (int) target.y, (int) target.z);
                Block currentBlock = worldProvider.getBlock(blockPos);

                /* PHYSICS */
                if (currentBlock.isDestructible()) {
                    EntityRef blockEntity = blockEntityRegistry.getEntityAt(blockPos);
                    // allow explosions to chain together,  but do not chain on the instigating block
                    if (!blockEntity.equals(instigatingBlockEntity) && blockEntity.hasComponent(ExplosiveMineComponent.class)) {
                        doExplosion(blockEntity.getComponent(ExplosionActionComponent.class), blockPos.toVector3f(), blockEntity);
                    } else {
                        blockEntity.send(new DoDamageEvent(explosionComp.damageAmount, explosionComp.damageType));
                    }
                }
            }
        }
    }
}
