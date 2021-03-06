// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.blocks;

import com.google.common.collect.ImmutableList;
import gnu.trove.map.TByteObjectMap;
import gnu.trove.map.hash.TByteObjectHashMap;
import org.joml.Vector3ic;
import org.terasology.MineSweeper.component.MineComponent;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.BlockEntityRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockBuilderHelper;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.block.BlockUri;
import org.terasology.engine.world.block.family.AbstractBlockFamily;
import org.terasology.engine.world.block.family.BlockPlacementData;
import org.terasology.engine.world.block.family.BlockSections;
import org.terasology.engine.world.block.family.RegisterBlockFamily;
import org.terasology.engine.world.block.loader.BlockFamilyDefinition;
import org.terasology.engine.world.block.shapes.BlockShape;
import org.terasology.gestalt.naming.Name;

@RegisterBlockFamily("countable")
@BlockSections({"one", "two", "three", "four", "five", "six", "seven","eight","nine","ten","eleven","twelve","thirteen","fourteen","fifteen","sixteen","marked"})
public class SweeperFamily extends AbstractBlockFamily {
    public static final String ONE = "one";
    public static final String TWO = "two";
    public static final String THREE = "three";
    public static final String FOUR = "four";
    public static final String FIVE = "five";
    public static final String SIX = "six";
    public static final String SEVEN = "seven";
    public static final String EIGHT = "eight";
    public static final String NINE = "nine";
    public static final String TEN = "ten";
    public static final String ELEVEN = "eleven";
    public static final String TWELEVE = "twelve";
    public static final String THIRTEEN = "thirteen";
    public static final String FOURTEEN = "fourteen";
    public static final String FIFTEEN = "fifteen";
    public static final String SIXTEEN = "sixteen";
    public static final String MARKED = "marked";
    public static final ImmutableList<String> SWEEPER_MAPPING = ImmutableList.<String>builder()
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

    @In
    BlockEntityRegistry blockEntityRegistry;


    private TByteObjectMap<Block> blocks = new TByteObjectHashMap<>();

    public SweeperFamily(BlockFamilyDefinition definition, BlockShape shape, BlockBuilderHelper blockBuilder) {
        super(definition, blockBuilder);
        throw new UnsupportedOperationException("Freeform blocks not supported");
    }

    public SweeperFamily(BlockFamilyDefinition definition, BlockBuilderHelper blockBuilder) {

        super(definition, blockBuilder);

        BlockUri blockUri = new BlockUri(definition.getUrn());

        for (byte x = 0; x < SWEEPER_MAPPING.size(); x++) {
            Block block = blockBuilder.constructSimpleBlock(definition, SWEEPER_MAPPING.get(x), new BlockUri(blockUri
                , new Name(String.valueOf(x))), this);
            block.setUri(new BlockUri(blockUri, new Name(String.valueOf(x))));
            blocks.put(x, block);
        }
    }

    public int getNumberOfMines(Vector3ic location) {
        int numberOfMines = 0;
        for (Vector3ic current : new BlockRegion(location).expand(1, 1, 1)) {
            if (blockEntityRegistry.getBlockEntityAt(current).hasComponent(MineComponent.class)) {
                numberOfMines++;
            }
        }
        return numberOfMines;
    }

    @Override
    public Block getBlockForPlacement(BlockPlacementData data) {
        return blocks.get((byte) getNumberOfMines(data.blockPosition));
    }

    public Block getBlockForNumberOfNeighbors(byte numberOfMines) {
        return blocks.get(numberOfMines);
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

    @Override
    public Iterable<Block> getBlocks() {
        return blocks.valueCollection();
    }
}
