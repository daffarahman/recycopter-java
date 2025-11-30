package com.dapasril.finalprojectpbo.managers;

public class EconomyManager {
    public static final int FUEL_REFILL_PRICE = 75;

    private static EconomyManager instance;
    private int money;

    private EconomyManager() {
        this.money = 0;
    }

    public static EconomyManager getInstance() {
        if (instance == null) {
            instance = new EconomyManager();
        }
        return instance;
    }

    public int getMoney() {
        return money;
    }

    public void addMoney(int amount) {
        if (amount > 0) {
            this.money += amount;
        }
    }

    public boolean spendMoney(int amount) {
        if (amount > 0 && this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public void reset() {
        this.money = 0;
    }
}
