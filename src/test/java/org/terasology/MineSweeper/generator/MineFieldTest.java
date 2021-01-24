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
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MineFieldTest {
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

    @Test
    public void getNumberOfNeighbors() throws Exception {
        MineField mineField = new MineField();

        mineField.addMines(new Vector3i(0,0,0));
        mineField.addMines(new Vector3i(0,1, 0));
        mineField.addMines(new Vector3i(1,0,0));
        mineField.addMines(new Vector3i(-1,0,0));
        mineField.addMines(new Vector3i(0,-1,0));
        mineField.addMines(new Vector3i(0,0,1));
        mineField.addMines(new Vector3i(0,0,-1));
        mineField.addMines(new Vector3i(1,1,1));

        assertEquals(8, mineField.getNumberOfNeighbors(new Vector3i(0,0,0)));
    }

    @Test
    public void addMines() throws Exception {
        MineField mineField = new MineField();

        for (Vector3i mine : getNeighbors(new Vector3i(1,1,1))) {
            mineField.addMines(mine);
        }

        for (Vector3i mine : getNeighbors(new Vector3i(0,0,0))) {
            mineField.addMines(mine);
        }

        for (Vector3i mine : getNeighbors(new Vector3i(0,0,1))) {
            mineField.addMines(mine);
        }

        assertTrue(mineField.getMines().stream().allMatch(mine -> mineField.getNumberOfNeighbors(mine) <= 16));
    }
}