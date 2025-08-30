package jp.mincra.mincramagics.gui.screen;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import jp.mincra.mincramagics.config.JobRewardConfigLoader;
import jp.mincra.mincramagics.gui.BuildContext;
import jp.mincra.mincramagics.gui.GUI;
import jp.mincra.mincramagics.gui.component.CloseButton;
import jp.mincra.mincramagics.gui.lib.Component;
import jp.mincra.mincramagics.gui.lib.GUIHelper;
import jp.mincra.mincramagics.gui.lib.Position;
import jp.mincra.mincramagics.gui.lib.Screen;
import lombok.Builder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class JobRewardListMenu extends GUI {
    @Override
    protected @Nullable Screen build(BuildContext context) {
        final var player = context.player();
        final var jobs = Jobs.getJobs();
        final var playerJobs = Jobs.getPlayerManager().getJobsPlayer(player).getJobProgression();

        final Consumer<Job> handleJobClicked = job -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            final var menu = new JobRewardMenu(job);
            menu.open(player);
        };

        final Consumer<Void> handleCloseClicked = v -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            player.closeInventory();
        };

        return Screen.builder()
                .title(GUIHelper.guiTitle("職業報酬一覧", "%oraxen_gui_jobs_reward_list%", 4))
                .size(36)
                .isModifiableSlot(i -> false)
                .components(List.of(
                        CloseButton.builder()
                                .pos(new Position(8, 0))
                                .onClick(handleCloseClicked)
                                .build(),
                        JobList.builder()
                                        .pos(new Position(1, 1, 7, 2))
                                .jobs(jobs)
                                .playerJobs(playerJobs)
                                .onJobClicked(handleJobClicked)
                                .build()
                ))
                .build();
    }
}

@Builder
class JobList extends Component {
    private final Position pos;
    private final List<Job> jobs;
    private final List<JobProgression> playerJobs;
    private final Consumer<Job> onJobClicked;

    @Override
    public void render(Inventory inv) {
        final var indexes = pos.toIndexStream().boxed().toList();
        IntStream.range(0, jobs.size()).forEach(i -> {
            final var job = jobs.get(i);
            final var jobProgression = playerJobs.stream().filter(jp -> jp.getJob().equals(job)).findFirst();
            final var config = JobRewardConfigLoader.getJobConfig(job);

            final var item = job.getGuiItem();
            final var meta = item.getItemMeta();
            meta.displayName(net.kyori.adventure.text.Component.text(job.getDisplayName()));
            if (jobProgression.isEmpty()) {
                meta.lore(List.of(
                        net.kyori.adventure.text.Component.text("未加入").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
                ));
            } else if (config == null) {
                meta.lore(List.of(
                        net.kyori.adventure.text.Component.text("準備中").color(NamedTextColor.RED).decoration(TextDecoration.ITALIC, false)
                ));
            } else {
                meta.lore(List.of(
                        net.kyori.adventure.text.Component.text("クリックして報酬を確認").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                        net.kyori.adventure.text.Component.text("lv" + jobProgression.get().getLevel()).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false)
                ));
                meta.addEnchant(Enchantment.FORTUNE, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
            inv.setItem(indexes.get(i), item);
            addClickListener(indexes.get(i), e -> onJobClicked.accept(job));
        });
    }
}
