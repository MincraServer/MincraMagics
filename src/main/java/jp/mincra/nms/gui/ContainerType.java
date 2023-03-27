package jp.mincra.nms.gui;

import net.minecraft.world.inventory.Containers;

public enum ContainerType {
    GENERIC_9X6(Containers.c);

    private Containers containers;

    ContainerType(Containers containers) {
        this.containers = containers;
    }

    public Containers getContainers() {
        return containers;
    }
}
