package it.ziopagnotta.bwgenerator.commands;

import it.ziopagnotta.bwgenerator.BWGenerator;
import it.ziopagnotta.bwgenerator.drop.ItemDrop;
import it.ziopagnotta.bwgenerator.generator.Generator;
import it.ziopagnotta.bwgenerator.generator.GeneratorFactory;
import it.ziopagnotta.bwgenerator.generator.MovementAxis;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.concurrent.atomic.AtomicInteger;

public class BWCommands implements CommandExecutor {
    private final BWGenerator plugin;
    private final GeneratorFactory generatorFactory;

    public BWCommands(@NotNull BWGenerator plugin) {
        this.plugin = plugin;
        generatorFactory = plugin.getGeneratorFactory();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage("You are not a player!");
            return false;
        }

        if(args[0].equalsIgnoreCase("create")) {
            if(args.length != 3)
                return false;

            String name = args[1];

            if(generatorFactory.exists(name)) {
                player.sendMessage(Component.text("Generator with name " + name + " already exists!").color(NamedTextColor.RED));
                return false;
            }

            String text = args[2];
            Location location = player.getEyeLocation().add(0, 1, 0);
            location.setYaw(0);
            location.setPitch(0);

            Generator generator = new Generator.Builder()
                    .plugin(plugin)
                    .name(name)
                    .origin(location)
                    .enabled(true)
                    .startingLevel(0)
                    .maxLevel(1)
                    .build();

            TextDisplay title = generator.createTextLine(
                    Component.text(text).decorate(TextDecoration.BOLD).color(NamedTextColor.RED),
                    true,
                    new Vector3f(1f, 1f, 1f),
                    TextDisplay.TextAlignment.CENTER,
                    null
            );


            /*
            ItemDisplay subtitle = generator.createItemLine(
                    new ItemStack(Material.DIAMOND),
                    true,
                    ItemDisplay.ItemDisplayTransform.HEAD
            );*/


            ItemDisplay itemDisplay = generator.createItemLine(
                    new ItemStack(Material.DIAMOND_BLOCK),
                    false,
                    new Vector3f(0.5f, 0.5f, 0.5f),
                    ItemDisplay.ItemDisplayTransform.NONE
            );

            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                rotateY(itemDisplay);
            }, 3L, 20 * 4);

            generator.addLines(
                    title,
                    itemDisplay
            );

            ItemDrop itemDrop = new ItemDrop(
                    "test",
                    new ItemStack(Material.DIAMOND),
                    5,
                    true
            );

            ItemDrop itemDrop2 = new ItemDrop(
                    "test2",
                    new ItemStack(Material.EMERALD),
                    10,
                    false
            );

            generator.addDrop(itemDrop, 0);
            generator.addDrop(itemDrop2, 1);

            generatorFactory.registerGenerator(name, generator);
            player.sendMessage(Component.text("Generator with name " + generator.getName() + " was successfully set.").color(NamedTextColor.GREEN));
            return true;
        }

        if(args[0].equalsIgnoreCase("toggle")) {
            if(args.length != 2)
                return false;

            String name = args[1];
            Generator generator = generatorFactory.getByName(name);

            if(generator == null) {
                player.sendMessage(Component.text("Generator with name " + name + " was not found.").color(NamedTextColor.RED));
                return false;
            }

            generator.setEnabled(!generator.isEnabled());

            player.sendMessage(Component.text("Generator with name " + name + " toggled to " + generator.isEnabled()).color(NamedTextColor.GREEN));
            return true;
        }

        if(args[0].equalsIgnoreCase("remove")) {
            Generator generator = generatorFactory.getNearestGenerator(player.getLocation());

            if(generator == null) {
                player.sendMessage(Component.text("Cannot find any nearest generator.").color(NamedTextColor.RED));
                return false;
            }

            generatorFactory.unregisterGenerator(generator.getName());
            player.sendMessage(Component.text("Generator with name " + generator.getName() + " has been removed.").color(NamedTextColor.GREEN));
            return true;
        }

        // generator drop add|remove
        //   -1        0        1           2        3         4        5
        //generator drop generatorId dropName dropLevel addToInv dropMillis
        if(args[0].equalsIgnoreCase("drop")) {
            if(args[1].equalsIgnoreCase("add")) {
                if (args.length != 7)
                    return false;

                String name = args[2];
                Generator generator = generatorFactory.getByName(name);

                if (generator == null) {
                    player.sendMessage(Component.text("Generator with name " + name + " was not found.").color(NamedTextColor.RED));
                    return false;
                }

                ItemStack itemStack = player.getInventory().getItemInMainHand();
                if (!itemStack.getType().isItem()) {
                    player.sendMessage(Component.text("Cannot add item " + itemStack.getType() + " to generator with name " + name + ".").color(NamedTextColor.RED));
                    return false;
                }

                String dropName = args[3];
                int level = Integer.parseInt(args[4]);
                boolean addToInv = Boolean.parseBoolean(args[5]);
                long seconds = Long.parseLong(args[6]);

                generator.addDrop(new ItemDrop(dropName, itemStack, seconds, addToInv), level);

                player.sendMessage(Component.text("Successfully added drop " + dropName + " with item " + itemStack.getType().name() + " to generator with name " + name).color(NamedTextColor.GREEN));
                return true;
            }

            //generator drop remove dropName
            if(args[1].equalsIgnoreCase("remove")) {
                String name = args[2];
                Generator generator = generatorFactory.getByName(name);

                if (generator == null) {
                    player.sendMessage(Component.text("Generator with name " + name + " was not found.").color(NamedTextColor.RED));
                    return false;
                }

                String dropName = args[3];
                generator.removeDrop(dropName);

                player.sendMessage(Component.text("Successfully removed drop " + dropName + " from generator with name " + name).color(NamedTextColor.GREEN));
            }
            return true;
        }

        return false;
    }

    public void rotateY(Display entity) {
        Vector3f translation = new Vector3f(0, 0, 0);
        AxisAngle4f axis = new AxisAngle4f((float) Math.PI, new Vector3f(0, 1, 0));
        Transformation transformation = new Transformation(
                translation,
                axis,
                new Vector3f(0.5f, 0.5f, 0.5f),
                axis
        );
        entity.setInterpolationDelay(-1);
        entity.setInterpolationDuration(20 * 4);
        entity.setTransformation(transformation);
    }
}
