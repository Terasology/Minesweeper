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

import org.terasology.customOreGen.PDist;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.random.FastRandom;
import org.terasology.utilities.random.Random;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetBorder;
import org.terasology.world.generation.FacetProviderPlugin;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SurfaceHeightFacet;
import org.terasology.world.generator.plugin.RegisterPlugin;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
@Requires(@Facet(value = SurfaceHeightFacet.class, border = @FacetBorder(sides = 30, bottom = 30, top = 30)))
@Produces(MineFieldFacet.class)
public class MineFieldFacetProvider implements FacetProviderPlugin {
    private  long seed;

    private Noise noise;
    @Override
    public void setSeed(long seed) {

        this.seed = seed;
        noise = new SimplexNoise(seed);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(MineFieldFacet.class).extendBy(30,30,30);
        final MineFieldFacet facet = new MineFieldFacet(region.getRegion(),border);
        final SurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(SurfaceHeightFacet.class);


        PDist size = new PDist(50,30);
        PDist distance = new PDist(0,5);
        PDist frequency = new PDist(2,1);
        for (BaseVector2i position : surfaceHeightFacet.getWorldRegion().contents()) {
            int surfaceHeight = (int) surfaceHeightFacet.getWorld(position);
            if (facet.getWorldRegion().encompasses(position.getX(), surfaceHeight, position.getY())
                    && noise.noise(position.getX(), position.getY()) > 0.99) {
                Random random = new FastRandom(seed * (97 * position.x() + position.y() + surfaceHeight));

                    MineField mineField = new MineField();

                    int sizeOfField = size.getIntValue(random);
                    for (int y = 0; y < sizeOfField; y++) {

                        mineField.addMines(new Vector3i(new Vector3i(
                                (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
                                (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
                                (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random))
                        )));
                    }
                    facet.setWorld(position.getX(), surfaceHeight, position.y(), mineField);

            }
        }







//        for (Iterator<Vector3i> it = facet.getWorldRegion().iterator(); it.hasNext(); ) {
//            Vector3i p = it.next();
//            if(seedNoiseGen.noise(p.x,p.y,p.z) > .99f){
//
//            }
//
//        }


//        PDist size = new PDist(50,30);
//        PDist distance = new PDist(0,5);
//        PDist frequency = new PDist(2,1);
//
//        Random random = ChunkRandom.getChunkRandom(seed, ChunkMath.calcChunkPos(region.getRegion().center()), 17832181);
//        int numberOfFields = frequency.getIntValue(random);
//        for(int x = 0;x < numberOfFields; x++)
//        {
//            int xPosition = random.nextInt(facet.getWorldRegion().minX(),facet.getWorldRegion().maxX());
//            int yPosition = random.nextInt(facet.getWorldRegion().minY(),facet.getWorldRegion().maxY());
//            int zPosition = random.nextInt(facet.getWorldRegion().minZ(),facet.getWorldRegion().maxZ());
//
//            MineField mineField = new MineField();
//
//            int sizeOfField = size.getIntValue(random);
//            for(int y = 0; y < sizeOfField; y++) {
//
//                mineField.addMines(new Vector3i(new Vector3i(
//                        (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
//                        (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
//                        (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random))
//                )));
//            }
//            facet.setWorld(xPosition,yPosition,zPosition, mineField);
//
//        }

        region.setRegionFacet(MineFieldFacet.class, facet);
    }
}
