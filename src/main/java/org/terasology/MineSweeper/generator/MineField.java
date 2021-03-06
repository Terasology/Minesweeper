/*
 * Copyright 2017 MovingBlocks
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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.world.block.BlockRegion;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MineField {
    public enum Size{
        Small,
        Medium,
        Large
    }

    public enum Type{
        Normal
    }
    
    private Set<Vector3i> mines = new HashSet<>();

    private Set<Vector3i> getNeighbors(Vector3ic pos) {
        Set<Vector3i> neighbors = new HashSet<>();

        for (Vector3ic neighbor : new BlockRegion(pos).expand(1,1,1)) {
            neighbors.add(new Vector3i(neighbor));
        }

        return neighbors;
    }

    public void addMines(Vector3ic mine) {
        if(getNeighbors(mine).stream().allMatch(neighbor -> getNumberOfNeighbors(neighbor) < 16)) {
            this.mines.add(new Vector3i(mine));
        }
    }

    public Set<Vector3i> getMines() {
        return Collections.unmodifiableSet(mines);
    }

    public boolean hasMine(Vector3ic relativePos) {
        return mines.contains(relativePos);
    }

    public int getNumberOfNeighbors(Vector3ic pos) {
        int count = 0;
        for (Vector3ic current : new BlockRegion(pos).expand(1,1,1)) {
            if (mines.contains(current)) {
                count++;
            }
        }
        return count;
    }
}
