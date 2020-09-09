// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.generator;

import org.terasology.customOreGen.PDist;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.random.FastRandom;
import org.terasology.engine.utilities.random.Random;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetBorder;
import org.terasology.engine.world.generation.FacetProviderPlugin;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SurfaceHeightFacet;
import org.terasology.engine.world.generator.plugin.RegisterPlugin;
import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector3i;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
@Requires(@Facet(value = SurfaceHeightFacet.class, border = @FacetBorder(sides = 30, bottom = 30, top = 30)))
@Produces(MineFieldFacet.class)
public class MineFieldFacetProvider implements FacetProviderPlugin {
    private long seed;

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

        Border3D border = region.getBorderForFacet(MineFieldFacet.class).extendBy(30, 30, 30);
        final MineFieldFacet facet = new MineFieldFacet(region.getRegion(), border);
        final SurfaceHeightFacet surfaceHeightFacet = region.getRegionFacet(SurfaceHeightFacet.class);


        PDist size = new PDist(50, 30);
        PDist distance = new PDist(0, 5);
        PDist frequency = new PDist(2, 1);
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
//        Random random = ChunkRandom.getChunkRandom(seed, ChunkMath.calcChunkPos(region.getRegion().center()), 
//        17832181);
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
