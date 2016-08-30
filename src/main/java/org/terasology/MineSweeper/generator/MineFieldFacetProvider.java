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
@Produces(MineFieldFacet.class)
@Requires(@Facet(value = SparseObjectFacet3D.class, border = @FacetBorder(bottom = 1, sides = 1)))
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
        final MineFieldFacet facet = new MineFieldFacet(region.getRegion(), region.getBorderForFacet(MineFieldFacet.class));

        ClusterStructureDefinition clusterStructureDefinition = new ClusterStructureDefinition(new PDist(0,10),new PDist(50,20),new PDist(0,100));
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

        }

        region.setRegionFacet(MineFieldFacet.class, facet);
    }
}
