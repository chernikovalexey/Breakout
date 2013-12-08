package com.twopeople.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BonusManager {
    private class BonusInfo {
        public long startTime = 0;
        public boolean activated = false;

        public BonusInfo(long startTime) {
            this.startTime = startTime;
        }
    }

    private HashMap<BonusType, BonusInfo> pickedBonuses = new HashMap<BonusType, BonusInfo>();
    private ArrayList<BonusType> justRemoved = new ArrayList<BonusType>();

    public BonusManager() {
    }

    public void update() {
        justRemoved.clear();

        for (Map.Entry<BonusType, BonusInfo> entry : pickedBonuses.entrySet()) {
            BonusType type = entry.getKey();
            BonusInfo info = entry.getValue();
            if (System.currentTimeMillis() - info.startTime > type.getLifetime()) {
                pickedBonuses.remove(type);
                justRemoved.add(type);
            }
        }
    }

    public void add(BonusType type) {
        pickedBonuses.put(type, new BonusInfo(System.currentTimeMillis()));
    }

    public boolean has(BonusType type) {
        return pickedBonuses.get(type) != null;
    }

    public boolean isInactive(BonusType type) {
        return has(type) && !pickedBonuses.get(type).activated;
    }

    public void activate(BonusType type) {
        if (has(type)) {
            pickedBonuses.get(type).activated = true;
        }
    }

    public boolean isJustRemoved(BonusType type) {
        return justRemoved.indexOf(type) > -1;
    }

    public int getTimeRemaining(BonusType type) {
        if (!has(type)) { return 0; }
        long passed = System.currentTimeMillis() - pickedBonuses.get(type).startTime;
        return passed >= 0 ? (int) passed : 0;
    }

    public int count() {
        return pickedBonuses.size();
    }
}