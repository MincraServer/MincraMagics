package jp.mincra.mincramagics.gui;

import lombok.Builder;
import org.bukkit.entity.Player;

@Builder
public record BuildContext(boolean isFirstRender, Player player) {
}
