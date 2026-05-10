package io.github.schntgaispock.slimehud.waila;

import javax.annotation.Nonnull;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.schntgaispock.slimehud.SlimeHUD;
import io.github.schntgaispock.slimehud.util.Util;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;

public class PlayerWAILA extends BukkitRunnable {

    private final @Nonnull Player player;
    private final BossBar WAILABar;
    private final String WAILALocation;
    private final boolean useAutoBossBarColor;
    private final boolean keepTextColors;

    private String facing = "";
    private String facingBlock = "";
    private String facingBlockInfo = "";
    private String previousFacing = "";
    private boolean paused;

    private static final LegacyComponentSerializer LEGACY_SECTION = LegacyComponentSerializer.legacySection();

    public PlayerWAILA(@Nonnull Player player) {
        this.WAILALocation = SlimeHUD.getInstance().getConfig().getString("waila.location");
        this.player = player;

        String bossbarColor = SlimeHUD.getInstance().getConfig().getString("waila.bossbar-color").trim().toLowerCase();
        this.useAutoBossBarColor = bossbarColor.equals("inherit");

        BossBar.Color color = Util.pickBarColorFromColor(bossbarColor);
        this.WAILABar = BossBar.bossBar(Component.empty(), 1.0f, color, BossBar.Overlay.PROGRESS);
        player.showBossBar(WAILABar);

        this.keepTextColors = SlimeHUD.getInstance().getConfig().getBoolean("waila.use-original-colors");
    }

    @Override
    public void run() {
        updateFacing();

        if (paused) {
            return;
        }

        if (facing.equals(previousFacing)) {
            return;
        }

        previousFacing = facing;
        switch (WAILALocation) {
            case "bossbar" -> {
                if (facing.isEmpty()) {
                    WAILABar.name(Component.empty());
                    player.hideBossBar(WAILABar);
                    break;
                }
                player.showBossBar(WAILABar);

                Component title = keepTextColors
                    ? LEGACY_SECTION.deserialize(facing)
                    : Component.text(PlainTextComponentSerializer.plainText()
                        .serialize(LEGACY_SECTION.deserialize(facing)));

                if (useAutoBossBarColor) {
                    WAILABar.color(Util.pickBarColorFromName(facing));
                }

                WAILABar.name(title);
            }
            case "hotbar" -> {
                player.sendActionBar(LEGACY_SECTION.deserialize(facing));
            }
            default -> {}
        }
    }

    private void updateFacing() {
        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            clearFacing();
            return;
        }

        SlimefunItem item = BlockStorage.check(targetBlock);
        if (item == null) {
            clearFacing();
            return;
        }

        Location target = targetBlock.getLocation();
        HudRequest request = new HudRequest(item, target, player);
        facingBlock = SlimeHUD.getTranslationManager().getItemName(player, item);
        facingBlockInfo = SlimeHUD.getHudController().processRequest(request);

        String raw = facingBlock + (facingBlockInfo.isEmpty() ? "" : " &7| " + facingBlockInfo);
        facing = raw.replace("&", "§");
    }

    private void clearFacing() {
        facingBlock = "";
        facingBlockInfo = "";
        facing = "";
    }

    public void setPaused(boolean paused) {
        if (paused) {
            player.hideBossBar(WAILABar);
        } else if (!previousFacing.isEmpty()) {
            player.showBossBar(WAILABar);
        }
        this.paused = paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public @Nonnull Player getPlayer() {
        return player;
    }

    public BossBar getWAILABar() {
        return WAILABar;
    }

    public String getFacing() {
        return facing;
    }

    public String getFacingBlock() {
        return facingBlock;
    }

    public String getFacingBlockInfo() {
        return facingBlockInfo;
    }

    public PlayerWAILA setVisible(boolean visible) {
        if (visible) {
            player.showBossBar(WAILABar);
        } else {
            player.hideBossBar(WAILABar);
        }
        return this;
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }
}
