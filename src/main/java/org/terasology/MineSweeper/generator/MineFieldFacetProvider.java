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

import org.terasology.customOreGen.ClusterStructureDefinition;
import org.terasology.customOreGen.PDist;
import org.terasology.customOreGen.Structure;
import org.terasology.customOreGen.StructureNodeType;
import org.terasology.math.Region3i;
import org.terasology.math.TeraMath;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.FacetProviderPlugin;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generator.plugin.RegisterPlugin;

import java.util.Collection;

import static org.terasology.math.TeraMath.*;

/**
 * Created by michaelpollind on 8/28/16.
 */
@RegisterPlugin
@Produces(MineFieldFacet.class)
public class MineFieldFacetProvider implements FacetProviderPlugin {
    private Noise noise;

    @Override
    public void setSeed(long seed) {

        noise = new WhiteNoise(seed);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void process(GeneratingRegion region) {
        final MineFieldFacet facet = new MineFieldFacet(region.getRegion(), region.getBorderForFacet(MineFieldFacet.class));

        Region3i worldRegion = facet.getWorldRegion();

        for (int x = worldRegion.minX(); x <= worldRegion.maxX(); x++)
            for (int y = worldRegion.minX(); y <= worldRegion.maxX(); y++) {
                for (int z = worldRegion.minX(); z <= worldRegion.maxX(); z++) {

                    // TODO: check for overlap
                    if (noise.noise(x,y, z) > 0.99) {
                        facet.setWorld(x, y, z, new Mine());

                    }
                }

            }

        region.setRegionFacet(MineFieldFacet.class, facet);
    }
}
