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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import org.terasology.MineSweeper.component.ExplosiveMineComponent;
import org.terasology.MineSweeper.component.SweeperCountComponent;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.Rotation;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.naming.Name;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockBuilderHelper;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.*;
import org.terasology.world.block.loader.BlockFamilyDefinition;
import org.terasology.world.block.shapes.BlockShape;

import java.util.*;

/**
 * Created by michaelpollind on 8/27/16.
 */
@RegisterBlockFamilyFactory(value = "MineSweeper:Sweeper")
public class SweeperFamilyFactory implements BlockFamilyFactory {
    public static final String ONE = "one";
    public static final String TWO = "two";
    public static final String THREE= "three";
    public static final String FOUR= "four";
    public static final String FIVE = "five";
    public static final String SIX= "six";
    public static final String SEVEN = "seven";
    public static final String EIGHT = "eight";
    public static final String NINE = "nine";
    public static final String TEN = "ten";
    public static final String ELEVEN = "eleven";
    public static final String TWELEVE = "twelve";
    public static final String THIRTEEN = "thirteen";
    public static final String FOURTEEN  = "fourteen";
    public static final String FIFTEEN = "fifteen";
    public static final String SIXTEEN = "sixteen";
    public static final String MARKED = "marked";
    private static final ImmutableList<String> SWEEPER_MAPPING= ImmutableList.<String>builder()
            .add(MARKED)
            .add(ONE)
            .add(TWO)
            .add(THREE)
            .add(FOUR)
            .add(FIVE)
            .add(SIX)
            .add(SEVEN)
            .add(EIGHT)
            .add(NINE)
            .add(TEN)
            .add(ELEVEN)
            .add(TWELEVE)
            .add(THIRTEEN)
            .add(FOURTEEN)
            .add(FIFTEEN)
            .add(SIXTEEN)
            .build();
    @Override
    public Set<String> getSectionNames() {

        return ImmutableSet.<String>builder()
                .add(ONE)
                .add(TWO)
                .add(THREE)
                .add(FOUR)
                .add(FIVE)
                .add(SIX)
                .add(SEVEN)
                .add(EIGHT)
                .add(NINE)
                .add(TEN)
                .add(ELEVEN)
                .add(TWELEVE)
                .add(THIRTEEN)
                .add(FOURTEEN)
                .add(FIFTEEN)
                .add(SIXTEEN)
                .add(MARKED)
                .build();
    }


    public SweeperFamilyFactory()
    {
    }

    @Override
    public BlockFamily createBlockFamily(BlockFamilyDefinition definition, BlockBuilderHelper blockBuilder) {
        TByteObjectMap<Block> blocks = new TByteObjectHashMap<>();
        BlockUri blockUri = new BlockUri(definition.getUrn());

        for(byte x = 0; x < SWEEPER_MAPPING.size(); x++)
        {

            Block block = blockBuilder.constructTransformedBlock(definition, SWEEPER_MAPPING.get(x), Rotation.none());

            //block.getPrefab().get().getComponent(SweeperCountComponent.class).value = x;
            block.setUri(new BlockUri(blockUri,new Name(String.valueOf(x))));
            blocks.put(x,block);

        }
        return new SweeperFamilyUpdate(blockUri,definition.getCategories(),blocks);
    }




}
