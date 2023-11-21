package it.ziopagnotta.bwgenerator.drop;

import org.bukkit.command.CommandSender;

import java.time.Instant;

public final class CommandDrop implements Drop {
    private final String name;
    private final String command;
    private final DropType dropType;
    private final CommandSender sender;
    private final long dropSeconds;
    private Instant lastDropSeconds;

    public CommandDrop(String name, CommandSender sender, String command, long dropSeconds) {
        this.name = name;
        this.command = command;
        this.sender = sender;
        this.dropSeconds = dropSeconds;

        dropType = DropType.COMMAND;
        lastDropSeconds = Instant.now();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getDrop() {
        return command;
    }

    @Override
    public DropType getDropType() { return dropType; }

    public CommandSender getSender() {
        return sender;
    }

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
}
