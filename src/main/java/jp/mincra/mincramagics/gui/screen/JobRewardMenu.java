package jp.mincra.mincramagics.gui.screen;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import io.th0rgal.oraxen.api.OraxenItems;
import jp.mincra.bktween.BKTween;
import jp.mincra.bktween.TickTime;
import jp.mincra.mincramagics.MincraLogger;
import jp.mincra.mincramagics.MincraMagics;
import jp.mincra.mincramagics.config.JobRewardConfigLoader;
import jp.mincra.mincramagics.config.model.JobRewardConfig;
import jp.mincra.mincramagics.config.model.JobRewardsConfig;
import jp.mincra.mincramagics.db.dao.JobRewardDao;
import jp.mincra.mincramagics.db.model.JobReward;
import jp.mincra.mincramagics.gui.BuildContext;
import jp.mincra.mincramagics.gui.GUI;
import jp.mincra.mincramagics.gui.lib.*;
import lombok.Builder;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JobRewardMenu extends GUI {
    private final Job job;
    private final JobRewardsConfig jobConfig;
    private final JobRewardDao jobRewardDao;

    // Required for reflection-based instantiation
    public JobRewardMenu() {
        this(Jobs.getJobs().getFirst());
    }

    public JobRewardMenu(Job job) {
        this.job = job;
        jobConfig = JobRewardConfigLoader.getJobConfig(job);
        jobRewardDao = MincraMagics.getJobRewardDao();
    }

    @Override
    protected Screen build(BuildContext context) {
        if (job == null) {
            context.player().sendMessage(net.kyori.adventure.text.Component.text("§c ◆職業が見つかりません"));
            return null;
        }
        final var player = context.player();
        final var jobsPlayer = Jobs.getPlayerManager().getJobsPlayer(player);
        final var progression = jobsPlayer.getJobProgression().stream().filter(
                jobProgression -> jobProgression.getJob().equals(job)
        ).findFirst();

        if (jobConfig == null || !jobConfig.enabled()) {
            player.sendMessage(net.kyori.adventure.text.Component.text(String.format("§c ◆%s§c の報酬は準備中です", job.getDisplayName())));
            return null;
        }

        final int jobLevel = progression.map(JobProgression::getLevel).orElse(0);
        final var currentMinLevel = useState(Math.max(1, jobLevel));
        final var jobRewards = useState(jobRewardDao.findByPlayerAndJob(player.getUniqueId(), job.getId()));

        final Consumer<JobRewardConfig> handleReceiveReward = (jobRewardConfig) -> {
            // Give the reward items to the player
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 2.0f);
            if (jobRewardDao.findByPlayerAndJob(player.getUniqueId(), job.getId()).stream()
                    .anyMatch(reward -> reward.getLevel() == jobRewardConfig.level())) {
                player.sendMessage(net.kyori.adventure.text.Component.text("§c ◆報酬はすでに受け取っています"));
                return;
            }
            player.give(jobRewardConfig.items());
            MincraLogger.info(String.format(
                    "Player %s received job reward for job %s level %d. Items: %s",
                    player.getName(), job.getName(), jobRewardConfig.level(), jobRewardConfig.items()
            ));

            final var jobReward = JobReward.builder()
                    .playerId(player.getUniqueId())
                    .jobId(job.getId())
                    .level(jobRewardConfig.level())
                    .build();
            jobRewardDao.save(jobReward);

            // Update the job rewards state
            jobRewards.set(prev -> {
                final var updatedRewards = prev.stream()
                        .filter(reward -> reward.getLevel() != jobRewardConfig.level())
                        .collect(Collectors.toList());
                updatedRewards.add(jobReward);
                return updatedRewards;
            });
        };

        final Consumer<Void> handleBackClick = (v) -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            currentMinLevel.set((prev) -> {
                if (prev <= 1) return 1;
                return prev - 1;
            });
        };

        final Consumer<Void> handleNextClick = (v) -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            currentMinLevel.set((prev) -> {
                if (prev + 8 >= job.getMaxLevel()) return prev;
                return prev + 1;
            });
        };

        final Consumer<Integer> handleCursorClick = (pageIndex) -> currentMinLevel.set((prev) -> {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
            MincraLogger.debug(String.format(
                    "JobRewardMenu: Setting currentMinLevel to %d based on index %d",
                    (int) (job.getMaxLevel() * (double) pageIndex / 7.0), pageIndex));
            return Math.min((int) (job.getMaxLevel() * (double) pageIndex / 7.0) + 1, job.getMaxLevel() - 8);
        });

        // Close the menu and return to the job list menu
        addCloseListener(e -> new BKTween(MincraMagics.getInstance())
                .execute((v) -> {
                    player.playSound(player.getLocation(), Sound.UI_TOAST_OUT, 1.0f, 1.2f);
                    new JobRewardListMenu().open(player);
                    return true;
                })
                .delay(TickTime.TICK, 1)
                .run());

        return Screen.builder()
                .title(GUIHelper.guiTitle("職業報酬 §l"
                        + job.getDisplayName()
                        + "§r " + LevelStringBuilder.build(jobLevel, job.getMaxLevel(), 30), "%oraxen_gui_jobs_reward%", 4))
                .size(36)
                .isModifiableSlot(s -> s >= 36)
                .components(List.of(
                        new LevelDisplay(new Position(0, 0, 9), currentMinLevel.value()),
                        new RewardDisplay(new Position(0, 1, 9), currentMinLevel.value(), jobConfig),
                        ReceiveButtons.builder()
                                .pos(new Position(0, 2, 9))
                                .minLevel(currentMinLevel.value())
                                .currentLevel(jobLevel)
                                .rewards(jobConfig.rewards().stream()
                                        .collect(Collectors.toMap(
                                                JobRewardConfig::level,
                                                JobRewardConfig::items
                                        )))
                                .rewardHistories(jobRewards.value().stream()
                                        .collect(Collectors.toMap(
                                                JobReward::getLevel,
                                                reward -> reward
                                        )))
                                .onReceiveClicked(handleReceiveReward)
                                .build(),
                        HorizontalScrollbar.builder()
                                .pos(new Position(0, 3, 9))
                                .onBackClick(handleBackClick)
                                .onNextClick(handleNextClick)
                                .onPageClick(handleCursorClick)
                                .currentPageIndex((int) (8 * ((double) currentMinLevel.value() / job.getMaxLevel())))
                                .disableBack(currentMinLevel.value() <= 1)
                                .disableNext(currentMinLevel.value() + 8 >= job.getMaxLevel())
                                .build()
                ))
                .build();
    }
}

class LevelStringBuilder {
    /**
     * Build a string representing the levels from `level` to `maxLevel`.
     * Example: "§fLv.§l8 §a┃┃┃┃§7┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃┃" for level=8, maxLevel=40
     *
     * @param level    current level
     * @param maxLevel maximum level
     * @return kyori text component representing the levels
     */
    public static String build(int level, int maxLevel, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append("§8lv").append(level).append(" §a");
        int filledLength = (int) Math.round((double) level / maxLevel * length);
        sb.append("┃".repeat(Math.max(0, filledLength)));
        sb.append("§7");
        sb.append("┃".repeat(Math.max(0, length - filledLength)));
        return sb.toString();
    }
}

/**
 * レベルの数字が書いたアイコンを表示するコンポーネント
 */
class LevelDisplay extends Component {
    private final Position pos;
    private final int minLevel;

    public LevelDisplay(Position pos, int minLevel) {
        this.pos = pos;
        this.minLevel = minLevel;
    }

    @Override
    public void render(Inventory inv) {
        IntStream.range(0, pos.width()).forEach(i -> inv.setItem(i + pos.startIndex(), levelIcon(minLevel + i)));
    }

    private ItemStack levelIcon(int level) {
        return OraxenItems.getItemById("level_" + level).build();
    }
}

/**
 * 各レベルの報酬アイテムを表示するコンポーネント
 */
class RewardDisplay extends Component {
    private final Position pos;
    private final int minLevel;
    private final Map<Integer, List<ItemStack>> rewards;

    public RewardDisplay(Position pos, int minLevel, JobRewardsConfig config) {
        this.pos = pos;
        this.minLevel = minLevel;
        this.rewards = config.rewards().stream()
                .collect(Collectors.toMap(
                        JobRewardConfig::level,
                        JobRewardConfig::items
                ));
    }

    @Override
    public void render(Inventory inv) {
        IntStream.range(0, pos.width()).forEach(i -> {
            int level = minLevel + i;
            if (rewards.containsKey(level)) {
                final var items = rewards.get(level);
                if (!items.isEmpty()) {
                    inv.setItem(i + pos.startIndex(), items.getFirst()); // Set the first item of the reward
                    return;
                }
            }

            inv.setItem(i + pos.startIndex(), Icons.invisible); // No reward for this level
        });
    }
}

/**
 * それぞれの報酬が受け取られていなければ受け取りボタンを, そうでなければ受け取り済みのアイコンを表示するコンポーネント
 */
@Builder
class ReceiveButtons extends Component {
    private static final ItemStack lockedIcon = OraxenItems.getItemById("gui_job_reward_locked_icon").build();
    private static final ItemStack receiveIcon = OraxenItems.getItemById("gui_job_reward_receive_icon").build();
    private static final ItemStack receivedIcon = OraxenItems.getItemById("gui_job_reward_received_icon").build();
    private final Position pos;
    private final int minLevel;
    private final int currentLevel;
    private final Map<Integer, List<ItemStack>> rewards;
    private final Map<Integer, JobReward> rewardHistories;
    private final Consumer<JobRewardConfig> onReceiveClicked;

    public ReceiveButtons(Position pos, int minLevel, int currentLevel, Map<Integer, List<ItemStack>> rewards, Map<Integer, JobReward> rewardHistories, Consumer<JobRewardConfig> onReceiveClicked) {
        this.pos = pos;
        this.minLevel = minLevel;
        this.currentLevel = currentLevel;
        this.rewards = rewards;
        this.rewardHistories = rewardHistories;
        this.onReceiveClicked = onReceiveClicked;
    }

    @Override
    public void render(Inventory inv) {
        IntStream.range(0, pos.width()).forEach(i -> {
            final var level = minLevel + i;
            final var reward = rewards.get(level);
            if (reward == null || reward.isEmpty()) {
                inv.setItem(i + pos.startIndex(), Icons.invisible); // No reward for this level
                return;
            }
            final var rewardHistory = rewardHistories.get(level);
            if (rewardHistory != null) {
                inv.setItem(i + pos.startIndex(), receivedIcon); // Set received icon
                return;
            }

            if (level > currentLevel) {
                inv.setItem(i + pos.startIndex(), lockedIcon); // Set locked icon
                return;
            }

            inv.setItem(i + pos.startIndex(), receiveIcon);
            addClickListener(i + pos.startIndex(), e -> {
                e.setCancelled(true);
                onReceiveClicked.accept(new JobRewardConfig(level, rewards.get(level)));
            });
        });
    }
}

@Builder
class HorizontalScrollbar extends Component {
    private final Position pos;
    private final Consumer<Void> onBackClick;
    private final Consumer<Void> onNextClick;
    private final Consumer<Integer> onPageClick;
    private final int currentPageIndex;
    private final boolean disableBack;
    private final boolean disableNext;

    public HorizontalScrollbar(Position pos, Consumer<Void> onBackClicked, Consumer<Void> onNextClicked, Consumer<Integer> onPageClick, int currentPageIndex, boolean disableBack, boolean disableNext) {
        this.pos = pos;
        this.onBackClick = onBackClicked;
        this.onNextClick = onNextClicked;
        this.onPageClick = onPageClick;
        this.currentPageIndex = currentPageIndex >= pos.width() ? pos.width() - 1 : currentPageIndex;
        this.disableBack = disableBack;
        this.disableNext = disableNext;

        if (currentPageIndex >= pos.width()) {
            MincraLogger.warn(String.format(
                    "Pagination currentPageIndex %d is greater than width %d. Resetting to last index.",
                    currentPageIndex, pos.width()));
        }
    }

    @Override
    public void render(Inventory inv) {
        inv.setItem(pos.startIndex(), disableBack ? Icons.leftArrowDisabled : Icons.leftArrow);
        IntStream.range(0, pos.width() - 2)
                .forEach(i -> {
                    final var index = i + pos.startIndex() + 1;
                    if (i == currentPageIndex) {
                        inv.setItem(index, Icons.horizontalCursor);
                        return;
                    }
                    inv.setItem(index, Icons.invisible);
                    addClickListener(index, e -> {
                        e.setCancelled(true);
                        onPageClick.accept(i);
                    });
                }); // Fill empty slots with invisible items
        inv.setItem(pos.endIndex(), disableNext ? Icons.rightArrowDisabled : Icons.rightArrow);

        // Add click listeners for back and next arrows
        if (!disableBack) {
            addClickListener(pos.startIndex(), e -> {
                e.setCancelled(true);
                onBackClick.accept(null);
            });
        }

        if (!disableNext) {
            addClickListener(pos.endIndex(), e -> {
                e.setCancelled(true);
                onNextClick.accept(null);
            });
        }
    }
}
