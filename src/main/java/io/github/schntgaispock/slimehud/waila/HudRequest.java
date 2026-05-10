package io.github.schntgaispock.slimehud.waila;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public record HudRequest(SlimefunItem slimefunItem, Location location, Player player) {}
