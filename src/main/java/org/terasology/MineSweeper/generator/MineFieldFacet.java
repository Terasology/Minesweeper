// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.generator;

import org.terasology.engine.math.Region3i;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.facets.base.SparseObjectFacet3D;

/**
 * Created by michaelpollind on 8/28/16.
 */
public class MineFieldFacet extends SparseObjectFacet3D<MineField> {

    public MineFieldFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }

}
