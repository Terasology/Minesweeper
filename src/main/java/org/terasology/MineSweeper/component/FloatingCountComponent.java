// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.MineSweeper.component;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Created by michaelpollind on 9/4/16.
 */
public class FloatingCountComponent implements Component<FloatingCountComponent> {
    public int neighbors = 0;

    @Override
    public void copy(FloatingCountComponent other) {
        this.neighbors = other.neighbors;
    }
}
