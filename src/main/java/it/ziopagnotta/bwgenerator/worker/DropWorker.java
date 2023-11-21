package it.ziopagnotta.bwgenerator.worker;

import it.ziopagnotta.bwgenerator.BWGenerator;
import it.ziopagnotta.bwgenerator.drop.CommandDrop;
import it.ziopagnotta.bwgenerator.drop.Drop;
import it.ziopagnotta.bwgenerator.drop.ItemDrop;
import it.ziopagnotta.bwgenerator.generator.Generator;
import it.ziopagnotta.bwgenerator.generator.GeneratorFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static java.time.Instant.now;

public class DropWorker extends BukkitRunnable {
    private final BWGenerator plugin;
    private final GeneratorFactory generatorFactory;

    public DropWorker(@NotNull BWGenerator plugin) {
        this.plugin = plugin;
        this.generatorFactory = plugin.getGeneratorFactory();
    }

    @Override
    public void run() {
        for(Generator generator : generatorFactory.getGenerators().values()) {
            if(!generator.isEnabled()) continue;

            for(Drop drop : generator.getDropByCurrentLevel()) {
                if(!generator.canDrop(drop)) {
                    continue;
                }

                if (drop instanceof ItemDrop itemDrop) {
                    ItemStack itemStack = (ItemStack) itemDrop.getDrop();

                    if(itemDrop.isAddToInventory()) {
                        Player player = generator.getNearestPlayer();

                        if (player == null)
                            continue;

                        Inventory inventory = player.getInventory();

                        if (inventory.firstEmpty() != -1) {
                            inventory.addItem(itemStack);
                        }
                    } else {
                        Location currentLocation = generator.getOrigin().subtract(0, 0.1, 0);

                        currentLocation.getWorld().dropItem(currentLocation, itemStack);
                    }

                    itemDrop.setLastDropSeconds(now());
                    return;
                }

                if(drop instanceof CommandDrop commandDrop) {
                    Player player = generator.getNearestPlayer();

                    if (player == null)
                        continue;

                    Bukkit.dispatchCommand(player, (String) commandDrop.getDrop());

                    commandDrop.setLastDropSeconds(now());
                }
            }
        }
    }

    public void start(long delay, long ticks) {
        this.runTaskTimer(plugin, delay, ticks).getTaskId();
    }
}
