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

import org.terasology.customOreGen.*;
import org.terasology.math.ChunkMath;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.utilities.random.Random;
import org.terasology.world.generation.*;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generation.facets.base.SparseObjectFacet3D;
import org.terasology.world.generator.plugin.RegisterPlugin;

import java.util.Collection;

import static org.terasology.math.TeraMath.*;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
@Updates(@Facet(value = SurfaceHeightFacet.class))
@Produces(MineFieldFacet.class)
public class MineFieldFacetProvider implements FacetProviderPlugin {
    private  long seed;

    @Override
    public void setSeed(long seed) {
        this.seed = seed;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(MineFieldFacet.class).extendBy(40,40,40);
        final MineFieldFacet facet = new MineFieldFacet(region.getRegion(),border);

        PDist size = new PDist(50,30);
        PDist distance = new PDist(0,20);
        PDist frequency = new PDist(10,3);

        Random random = ChunkRandom.getChunkRandom(seed, ChunkMath.calcChunkPos(region.getRegion().center()), 17832181);

        int numberOfFields = frequency.getIntValue(random);

        for(int x = 0;x < numberOfFields; x++)
        {
            int xPosition = random.nextInt(region.getRegion().minX(),region.getRegion().maxX());
            int yPosition = random.nextInt(region.getRegion().minY(),region.getRegion().maxY());
            int zPosition = random.nextInt(region.getRegion().minZ(),region.getRegion().maxZ());

            int sizeOfField = size.getIntValue(random);
            for(int y = 0; y < sizeOfField; y++) {
                int xOffset = (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random));
                int yOffset = (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random));
                int zOffset = (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random));
                facet.setWorld(xPosition+xOffset,yPosition+yOffset,zPosition+zOffset, new Mine());

            }

        }
/*
        ClusterStructureDefinition clusterStructureDefinition = new ClusterStructureDefinition(new PDist(0,10),new PDist(50,20),new PDist(0,10));
        for (Structure structure : clusterStructureDefinition.generateStructures(seed,region.getRegion()))
        {
            structure.generateStructure(new Structure.StructureCallback() {
                @Override
                public void replaceBlock(Vector3i position, StructureNodeType structureNodeType, Vector3i distanceToCenter) {

                    facet.setWorld(position, new Mine());
                }

                @Override
                public boolean canReplace(int x, int y, int z) {
                    return false;
                }
            });

        }*/

        region.setRegionFacet(MineFieldFacet.class, facet);
    }
}
