package it.ziopagnotta.bwgenerator.drop;

import java.time.Instant;

public sealed interface Drop permits CommandDrop, ItemDrop {
    String getName();

    default Object getDrop() {
        return null;
    }

    DropType getDropType();

    long getDropSeconds();

    Instant getLastDropSeconds();

    void setLastDropSeconds(Instant lastDropSeconds);
}
