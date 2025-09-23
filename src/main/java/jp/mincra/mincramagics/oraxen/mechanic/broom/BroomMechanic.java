package jp.mincra.mincramagics.oraxen.mechanic.broom;

import io.th0rgal.oraxen.mechanics.Mechanic;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import jp.mincra.mincramagics.nbt.components.Divider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

public class BroomMechanic extends Mechanic {
    private final double speed;
    private final double maxHeight;
    private final double brakeDistance;
    private final double ascentPower;
    private final String modelId;

    public BroomMechanic(MechanicFactory factory, ConfigurationSection section) {
        super(factory, section, itemBuilder -> {
            final var speed = section.getDouble("speed", 0.6);
            final var maxHeight = section.getDouble("max_height", 100.0);
            final var brakeDistance = section.getDouble("brake_distance", 2.0);
            final var ascentPower = section.getDouble("ascent_power", 0.1);

            final var lore = new ArrayList<>(itemBuilder.getLore());
            lore.add(Divider.get());
            lore.add("<reset><white>速度       <yellow>" + speed);
            lore.add("<reset><white>最高高度  <yellow>" + maxHeight);
            lore.add("<reset><white>減速距離  <yellow>" + brakeDistance);
            lore.add("<reset><white>上昇力    <yellow>" + ascentPower);
            lore.add(Divider.get());
            itemBuilder.setLore(lore);
            return itemBuilder;
        });
        this.speed = section.getDouble("speed", 0.6);
        this.maxHeight = section.getDouble("max_height", 100.0);
        this.brakeDistance = section.getDouble("brake_distance", 2.0);
        this.ascentPower = section.getDouble("ascent_power", 0.1);
        this.modelId = section.getString("model_id", "magic_broom");
    }

    public double getSpeed() { return speed; }
    public double getMaxHeight() { return maxHeight; }
    public double getBrakeDistance() { return brakeDistance; }
    public double getAscentPower() { return ascentPower; }
    public String getModelId() { return modelId; }
}
