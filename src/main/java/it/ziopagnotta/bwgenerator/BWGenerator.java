package it.ziopagnotta.bwgenerator;

import it.ziopagnotta.bwgenerator.commands.BWCommands;
import it.ziopagnotta.bwgenerator.events.JoinQuitEvent;
import it.ziopagnotta.bwgenerator.generator.GeneratorFactory;
import it.ziopagnotta.bwgenerator.worker.DropWorker;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

public class BWGenerator extends JavaPlugin {
    private GeneratorFactory generatorFactory;
    private DropWorker dropWorker;

    @Override
    public void onEnable() {
        generatorFactory = new GeneratorFactory();

        dropWorker = new DropWorker(this);
        dropWorker.start(0L, 20L);

        getCommand("generator").setExecutor(new BWCommands(this));

        getServer().getPluginManager().registerEvents(new JoinQuitEvent(this), this);
    }

    @Override
    public void onDisable() {
        generatorFactory.getGenerators().forEach((name, generator) -> {
            generator.getLines().forEach(Entity::remove);

            generatorFactory.unregisterGenerator(name);
        });

        dropWorker.cancel();
        dropWorker = null;

        generatorFactory = null;
    }

    public GeneratorFactory getGeneratorFactory() {
        return generatorFactory;
    }

    public DropWorker getDropWorker() {
        return dropWorker;
    }
}
