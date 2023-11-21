package it.ziopagnotta.bwgenerator.generator;

import it.ziopagnotta.bwgenerator.drop.Drop;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.time.Duration;
import java.util.*;

import static java.time.Instant.now;

public class Generator {
    private final JavaPlugin plugin;
    private final String name;
    private final LinkedList<Display> lines; //lines
    private final HashMap<Drop, Integer> drops; //drop, generator level
    private final int maxLevel;
    private final Location origin;
    private boolean enabled;
    private int currentLevel;

    private Generator(JavaPlugin plugin,
                      String name,
                      Location origin,
                      boolean enabled,
                      int startingLevel,
                      int maxLevel) {
        this.plugin = plugin;
        this.name = name;
        this.origin = origin;
        lines = new LinkedList<>();
        this.enabled = enabled;

        drops = new HashMap<>();
        currentLevel = Math.max(startingLevel, 0);
        this.maxLevel = Math.max(maxLevel, startingLevel);
    }

    public String getName() {
        return name;
    }

    public Location getOrigin() {
        return origin;
    }

    public LinkedList<Display> getLines() {
        return new LinkedList<>(lines);
    }

    public HashMap<Drop, Integer> getDrops() {
        return new HashMap<>(drops);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        for(Display line : getLines()) {
            line.setVisibleByDefault(enabled);

            for(Player player : Bukkit.getOnlinePlayers()) {
                if(enabled) {
                    player.showEntity(plugin, line);
                    continue;
                }

                player.hideEntity(plugin, line);
            }
        }
    }

    public void toggleLinesFor(Player player) {
        if(player == null)
            throw new IllegalArgumentException("player cannot be null");

        for(Display line : getLines()) {
            if(enabled) {
                player.showEntity(plugin, line);
                continue;
            }

            player.hideEntity(plugin, line);
        }
    }

    public boolean canDrop(@NotNull Drop drop) {
        return Math.abs(Duration.between(now(), drop.getLastDropSeconds()).toSeconds()) >= drop.getDropSeconds();
    }

    public void addDrop(Drop drop, int level) {
        if(drop == null) {
            throw new IllegalArgumentException("drop cannot be null.");
        }

        if(level > maxLevel) {
            throw new IllegalArgumentException("level must be between 0 and " + maxLevel);
        }

        drops.put(drop, level);
    }

    public void removeDrop(String name) {
        for(Drop drop : getDrops().keySet()) {
            if(drop.getName().equalsIgnoreCase(name)) {
                drops.remove(drop);
                break;
            }
        }
    }

    public void addLines(Display... displays) {
        lines.addAll(Arrays.asList(displays));
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<Drop> getDropsByLevel(int level) {
        List<Drop> drops = new ArrayList<>();

        for(Map.Entry<Drop, Integer> entry : this.drops.entrySet()) {
            if(entry.getValue() == level) {
                drops.add(entry.getKey());
            }
        }

        return drops;
    }

    public List<Drop> getDropByCurrentLevel() {
        List<Drop> drops = new ArrayList<>();

        for(Map.Entry<Drop, Integer> entry : this.drops.entrySet()) {
            if(entry.getValue() == getCurrentLevel()) {
                drops.add(entry.getKey());
            }
        }

        return drops;
    }

    private void initializeDisplay(@NotNull Display display, boolean faceToPlayer) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            display.setBillboard(faceToPlayer ? Display.Billboard.CENTER : Display.Billboard.FIXED);
            display.setVisibleByDefault(true);
        }, 2L);
    }

    @Nullable
    public TextDisplay createTextLine(@NotNull Component text,
                                      boolean faceToPlayer,
                                      Vector3f scale,
                                      @Nullable TextDisplay.TextAlignment alignment,
                                      @Nullable Color backgroundColor) {
        Location location = getOrigin();

        if(location == null) {
            return null;
        }

        location.subtract(0, 0.3, 0);

        TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class);

        initializeDisplay(textDisplay, faceToPlayer);
        textDisplay.text(text);
        textDisplay.setAlignment(alignment == null ? TextDisplay.TextAlignment.CENTER : alignment);
        textDisplay.setBackgroundColor(backgroundColor);

        Transformation transformation = textDisplay.getTransformation();
        transformation.getScale().set(scale);
        textDisplay.setTransformation(transformation);

        return textDisplay;
    }

    @Nullable
    public ItemDisplay createItemLine(@NotNull ItemStack itemStack,
                                      boolean faceToPlayer,
                                      Vector3f scale,
                                      @Nullable ItemDisplay.ItemDisplayTransform transform) {
        Location location = getOrigin();

        if(location == null) {
            return null;
        }

        location.subtract(0, 0.3, 0);

        ItemDisplay itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);

        initializeDisplay(itemDisplay, faceToPlayer);
        itemDisplay.setItemStack(itemStack);
        itemDisplay.setItemDisplayTransform(transform == null ? ItemDisplay.ItemDisplayTransform.FIXED : transform);

        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(scale);
        itemDisplay.setTransformation(transformation);

        return itemDisplay;
    }

    @Nullable
    public BlockDisplay createBlockLineByMaterial(@NotNull Material blockMaterial,
                                                  boolean faceToPlayer,
                                                  Vector3f scale) {
        Location location = getOrigin();

        if(location == null) {
            return null;
        }

        location.subtract(0, 0.3, 0);

        BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);
        Transformation transformation = blockDisplay.getTransformation();

        initializeDisplay(blockDisplay, faceToPlayer);
        blockDisplay.setBlock(Bukkit.createBlockData(blockMaterial));

        transformation.getScale().set(scale);
        blockDisplay.setTransformation(transformation);

        return blockDisplay;
    }

    @Nullable
    public BlockDisplay createBlockLineByBlockData(@NotNull BlockData blockData,
                                                   boolean faceToPlayer,
                                                   Vector3f scale) {
        Location location = getOrigin();

        if(location == null) {
            return null;
        }

        location.subtract(0, 0.3, 0);

        BlockDisplay blockDisplay = location.getWorld().spawn(location, BlockDisplay.class);
        Transformation transformation = blockDisplay.getTransformation();

        initializeDisplay(blockDisplay, faceToPlayer);
        blockDisplay.setBlock(blockData);

        transformation.getScale().set(scale);
        blockDisplay.setTransformation(transformation);

        return blockDisplay;
    }

    @Nullable
    public Player getNearestPlayer() {
        Player nearest = null;
        Location point = getOrigin();

        if(point == null) {
            return null;
        }

        for(Player player : point.getNearbyPlayers(5)) {
            if(nearest == null) {
                nearest = player;
                continue;
            }

            if(player.getLocation().distanceSquared(point) < nearest.getLocation().distanceSquared(point)) {
                nearest = player;
                break;
            }
        }

        return nearest;
    }

    public static class Builder {
        private JavaPlugin plugin;
        private int startingLevel, maxLevel;
        private boolean enabled;
        private String name;
        private Location origin;

        public Builder plugin(JavaPlugin plugin) {
            this.plugin = plugin;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder startingLevel(int startingLevel) {
            this.startingLevel = startingLevel;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder origin(Location origin) {
            this.origin = origin;
            return this;
        }

        public Generator build() {
            return new Generator(plugin, name, origin, enabled, startingLevel, maxLevel);
        }
    }
}