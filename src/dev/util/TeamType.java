package dev.util;

import lombok.Getter;

@Getter
public enum TeamType {

    T2x4, T2x1, T2x2, T3x2, T4x2, T4x3, T4x4;

    private String type;
    private int amount;
    private int teamSize;

    TeamType() {
        type = name().substring(1, name().length());
        String[] split = type.split("x");
        amount = Integer.parseInt(split[0]);
        teamSize = Integer.parseInt(split[1]);
    }

    public static TeamType of(String type) {
        for (TeamType types : values()) {
            if (type.equalsIgnoreCase(types.type)) {
                return types;
            }
        }
        return null;
    }
}
