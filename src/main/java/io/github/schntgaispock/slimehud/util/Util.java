package io.github.schntgaispock.slimehud.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;

import net.kyori.adventure.bossbar.BossBar;

import io.github.schntgaispock.slimehud.SlimeHUD;

public final class Util {

    private Util() {}

    private static final class RGB {
        final int red;
        final int green;
        final int blue;

        RGB(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        int[] asArray() {
            return new int[] {red, green, blue};
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof RGB r)) return false;
            return red == r.red && green == r.green && blue == r.blue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(red, green, blue);
        }
    }

    private static final HashMap<RGB, BossBar.Color> barColorRGBMap = new HashMap<>();
    private static final HashMap<RGB, BossBar.Color> savedBarColors = new HashMap<>();

    static {
        barColorRGBMap.put(new RGB(0x00, 0xb9, 0xec), BossBar.Color.BLUE);
        barColorRGBMap.put(new RGB(0x16, 0xb9, 0x00), BossBar.Color.GREEN);
        barColorRGBMap.put(new RGB(0xb9, 0x00, 0x90), BossBar.Color.PINK);
        barColorRGBMap.put(new RGB(0x61, 0x00, 0xb9), BossBar.Color.PURPLE);
        barColorRGBMap.put(new RGB(0xb9, 0x2a, 0x00), BossBar.Color.RED);
        barColorRGBMap.put(new RGB(0xff, 0xff, 0xff), BossBar.Color.WHITE);
        barColorRGBMap.put(new RGB(0xb9, 0xb9, 0x00), BossBar.Color.YELLOW);
    }

    public static BossBar.Color pickBarColorFromName(String name) {
        char colorCode = name.trim().startsWith("§") ? name.charAt(1) : ' ';
        if (colorCode == 'x') {
            try {
                final String stripped = name.replace("§", "");
                final int red = Integer.parseInt(stripped, 1, 3, 16);
                final int green = Integer.parseInt(stripped, 3, 5, 16);
                final int blue = Integer.parseInt(stripped, 5, 7, 16);

                final RGB rgb = new RGB(red, green, blue);

                if (savedBarColors.containsKey(rgb)) {
                    return savedBarColors.get(rgb);
                }

                BossBar.Color color = barColorRGBMap.get(Collections.min(barColorRGBMap.keySet(), (RGB a, RGB b) ->
                    (errorSquared(a.asArray(), rgb.asArray()) < errorSquared(b.asArray(), rgb.asArray())) ? -1 : 1
                ));

                savedBarColors.put(rgb, color);
                return color;

            } catch (NumberFormatException e) {
                return BossBar.Color.WHITE;
            }
        }

        return switch (colorCode) {
            case '4', 'c' -> BossBar.Color.RED;
            case '6', 'e' -> BossBar.Color.YELLOW;
            case '2', 'a' -> BossBar.Color.GREEN;
            case '3', 'b' -> BossBar.Color.BLUE;
            case '1', '5', '9' -> BossBar.Color.PURPLE;
            case 'd' -> BossBar.Color.PINK;
            default -> BossBar.Color.WHITE;
        };
    }

    public static BossBar.Color pickBarColorFromColor(String color) {
        return switch (color.trim()) {
            case "red" -> BossBar.Color.RED;
            case "yellow" -> BossBar.Color.YELLOW;
            case "green" -> BossBar.Color.GREEN;
            case "blue" -> BossBar.Color.BLUE;
            case "purple" -> BossBar.Color.PURPLE;
            case "pink" -> BossBar.Color.PINK;
            case "white", "default", "inherit" -> BossBar.Color.WHITE;
            default -> {
                SlimeHUD.log(Level.WARNING,
                    "[SlimeHUD] Invalid bossbar color: " + color,
                    "[SlimeHUD] Setting color to white...");
                yield BossBar.Color.WHITE;
            }
        };
    }

    public static String getColorFromCargoChannel(int channel) {
        return switch (channel) {
            case 1 -> "§f";
            case 2 -> "§6";
            case 3 -> "§9";
            case 4 -> "§b";
            case 5 -> "§e";
            case 6 -> "§a";
            case 7 -> "§d";
            case 8 -> "§8";
            case 9 -> "§7";
            case 10 -> "§3";
            case 11 -> "§5";
            case 12 -> "§1";
            case 13 -> "§c";
            case 14 -> "§2";
            case 15 -> "§4";
            case 16 -> "§0";
            default -> "§f";
        };
    }

    public static int errorSquared(int[] a, int[] b) {
        if (a.length != b.length) return Integer.MAX_VALUE;

        int total = 0;
        for (int i = 0; i < a.length; i++) {
            total += (int) Math.pow(a[i] - b[i], 2);
        }

        return total;
    }
}
