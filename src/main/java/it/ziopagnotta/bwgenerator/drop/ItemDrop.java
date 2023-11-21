package it.ziopagnotta.bwgenerator.drop;

import org.bukkit.inventory.ItemStack;

import java.time.Instant;

public final class ItemDrop implements Drop {
    private final String name;
    private final ItemStack itemStack;
    private final DropType dropType;
    private final long dropSeconds;
    private Instant lastDropSeconds;
    private final boolean addToInventory;

    public ItemDrop(String name, ItemStack itemStack, long dropSeconds, boolean addToInventory) {
        this.name = name;
        this.itemStack = itemStack;
        this.dropSeconds = dropSeconds;

        dropType = DropType.ITEM_STACK;
        this.addToInventory = addToInventory;
        lastDropSeconds = Instant.now();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getDrop() {
        return itemStack;
    }

    @Override
    public DropType getDropType() { return dropType; }

    @Override
    public long getDropSeconds() {
        return dropSeconds;
    }

    @Override
    public Instant getLastDropSeconds() {
        return lastDropSeconds;
    }

    @Override
    public void setLastDropSeconds(Instant lastDropSeconds) {
        this.lastDropSeconds = lastDropSeconds;
    }

    public boolean isAddToInventory() {
        return addToInventory;
    }
}
