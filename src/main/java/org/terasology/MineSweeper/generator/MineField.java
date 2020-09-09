// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.generator;

import org.terasology.engine.math.Region3i;
import org.terasology.math.geom.Vector3i;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MineField {
    private final Set<Vector3i> mines = new HashSet<>();

    private Set<Vector3i> getNeighbors(Vector3i pos) {
        Set<Vector3i> neighbors = new HashSet<>();

        for (Vector3i neighbor : Region3i.createFromCenterExtents(pos, 1)) {
            neighbors.add(neighbor);
        }

        return neighbors;
    }

    public void addMines(Vector3i mine) {
        if (getNeighbors(mine).stream().allMatch(neighbor -> getNumberOfNeighbors(neighbor) < 16)) {
            this.mines.add(mine);
        }
    }

    public Set<Vector3i> getMines() {
        return Collections.unmodifiableSet(mines);
    }

    public boolean hasMine(Vector3i relativePos) {
        return mines.contains(relativePos);
    }

    public int getNumberOfNeighbors(Vector3i pos) {
        int count = 0;
        for (Vector3i current : Region3i.createFromCenterExtents(pos, 1)) {
            if (mines.contains(current)) {
                count++;
            }
        }
        return count;
    }

    public enum Size {
        Small,
        Medium,
        Large
    }

    public enum Type {
        Normal
    }
}
