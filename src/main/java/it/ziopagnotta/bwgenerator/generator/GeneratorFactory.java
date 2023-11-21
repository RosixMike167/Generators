package it.ziopagnotta.bwgenerator.generator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GeneratorFactory {
    private final HashMap<String, Generator> generators;
    public GeneratorFactory() {
        generators = new HashMap<>();
    }

    public HashMap<String, Generator> getGenerators() {
        return new HashMap<>(generators);
    }

    public void registerGenerator(@NotNull String name, @NotNull Generator generator) {
        generators.put(name, generator);
    }

    public void unregisterGenerator(@NotNull String name) {
        Generator generator = getByName(name);

        if(generator == null) {
            return;
        }

        generator.getLines().forEach(Entity::remove);

        generators.remove(name);
    }

    @Nullable
    public Generator getByName(String name) {
        return generators.get(name);
    }

    public boolean exists(String name) {
        return getByName(name) != null;
    }

    @Nullable
    public Generator getNearestGenerator(@NotNull Location location) {
        Generator nearest = null;

        for(Map.Entry<String, Generator> iterGenerator : generators.entrySet()) {
            if(nearest == null) {
                nearest = generators.get(iterGenerator.getKey());
                continue;
            }

            Generator generator = iterGenerator.getValue();

            Location generatorLocation = generator.getOrigin();
            Location nearestLocation = nearest.getOrigin();

            if(generatorLocation == null || nearestLocation == null) {
                continue;
            }

            if (generatorLocation.distanceSquared(location) < nearestLocation.distanceSquared(location)) {
                nearest = generator;
                break;
            }
        }

        return nearest;
    }
}
