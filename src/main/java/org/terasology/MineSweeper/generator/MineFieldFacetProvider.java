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
import org.terasology.math.Side;
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
import java.util.Iterator;

import static org.terasology.math.TeraMath.*;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
@Produces(MineFieldFacet.class)
public class MineFieldFacetProvider implements FacetProviderPlugin {
    private  long seed;

    private Noise seedNoiseGen;
    private Noise fieldNoiseGen;

    @Override
    public void setSeed(long seed) {

        seedNoiseGen = new WhiteNoise(seed);
        this.seed = seed;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(MineFieldFacet.class).extendBy(30,30,30);
        final MineFieldFacet facet = new MineFieldFacet(region.getRegion(),border);


//        for (Iterator<Vector3i> it = facet.getWorldRegion().iterator(); it.hasNext(); ) {
//            Vector3i p = it.next();
//            if(seedNoiseGen.noise((float) (p.x/10.0f),(float) (p.y/10.0f),(float) (p.z/10.0f)) > .99) {
//
//                MineField mineField = new MineField();
//
//                int sizeOfField = size.getIntValue(random);
//                for (int y = 0; y < sizeOfField; y++) {
//
//                    mineField.addMines(new Vector3i(new Vector3i(
//                            (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
//                            (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
//                            (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random))
//                    )));
//                }
//                facet.setWorld(p, mineField);
//            }
//        }



//        for (Iterator<Vector3i> it = facet.getWorldRegion().iterator(); it.hasNext(); ) {
//            Vector3i p = it.next();
//            if(seedNoiseGen.noise(p.x,p.y,p.z) > .99f){
//
//            }
//
//        }


        PDist size = new PDist(50,30);
        PDist distance = new PDist(0,5);
        PDist frequency = new PDist(2,1);

        Random random = ChunkRandom.getChunkRandom(seed, ChunkMath.calcChunkPos(region.getRegion().center()), 17832181);
        int numberOfFields = frequency.getIntValue(random);
        for(int x = 0;x < numberOfFields; x++)
        {
            int xPosition = random.nextInt(facet.getWorldRegion().minX(),facet.getWorldRegion().maxX());
            int yPosition = random.nextInt(facet.getWorldRegion().minY(),facet.getWorldRegion().maxY());
            int zPosition = random.nextInt(facet.getWorldRegion().minZ(),facet.getWorldRegion().maxZ());

            MineField mineField = new MineField();

            int sizeOfField = size.getIntValue(random);
            for(int y = 0; y < sizeOfField; y++) {

                mineField.addMines(new Vector3i(new Vector3i(
                        (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
                        (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random)),
                        (int) (Math.cos(random.nextFloat() * Math.PI * 2.0f) * distance.getValue(random))
                )));
            }
            facet.setWorld(xPosition,yPosition,zPosition, mineField);

        }

        region.setRegionFacet(MineFieldFacet.class, facet);
    }
}
