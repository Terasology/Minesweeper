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

import org.terasology.math.geom.Vector3i;

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

    private Set<Vector3i> getNeighbors(Vector3i pos) {
        Set<Vector3i> neighbors = new HashSet<>();

        for (int x = pos.x - 1; x <= pos.x + 1; x++) {
            for (int y = pos.y - 1; y <= pos.y + 1; y++) {
                for (int z = pos.z - 1; z <= pos.z + 1; z++) {
                    Vector3i neighbor = new Vector3i(x, y, z);
                    neighbors.add(neighbor);
                }
            }
        }

        return neighbors;
    }

    public void addMines(Vector3i mine) {
        if(getNeighbors(mine).stream().allMatch(neighbor -> getNumberOfNeighbors(neighbor) < 16)) {
            this.mines.add(mine);
        }
    }

    public Set<Vector3i> getMines(){
        return Collections.unmodifiableSet(mines);
    }

    public boolean hasMine(Vector3i relativePos){
        return mines.contains(relativePos);
    }

    public int getNumberOfNeighbors(Vector3i pos){
        int count = 0;
        for(int x = pos.x - 1; x <= pos.x + 1; x++){
            for(int y = pos.y - 1; y <= pos.y + 1; y++){
                for(int z = pos.z - 1; z <= pos.z + 1; z++){
                    if(mines.contains(new Vector3i(x,y,z)))
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}
