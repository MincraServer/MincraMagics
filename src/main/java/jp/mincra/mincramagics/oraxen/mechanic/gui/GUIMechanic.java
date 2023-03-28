package jp.mincra.mincramagics.oraxen.mechanic.gui;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import org.bukkit.configuration.ConfigurationSection;

class GUIMechanic extends Mechanic {
    private final String guiId;

    public GUIMechanic(MechanicFactory mechanicFactory, ConfigurationSection section) {
        super(mechanicFactory, section, item -> item);

        guiId = section.getString("id");
    }

    public String getGuiId() {
        return guiId;
    }
}

