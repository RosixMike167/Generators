package it.ziopagnotta.bwgenerator.drop;

public enum DropType {
    ITEM_STACK, COMMAND, CUSTOM;

    public static boolean contains(String str) {
        for (DropType c : DropType.values()) {
            if (c.name().equalsIgnoreCase(str)) {
                return true;
            }
        }

        return false;
    }
}
