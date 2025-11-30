package com.dapasril.finalprojectpbo.recycling;

public enum RecyclableItem {
    PLASTIC_BOTTLE("Plastic Bottle"),
    ALUMINUM_CAN("Aluminum Can"),
    GLASS_BOTTLE("Glass Bottle"),
    NEWSPAPER("Newspaper"),
    CARDBOARD_BOX("Cardboard Box"),
    E_WASTE("E-Waste"),
    OLD_TIRE("Old Tire"),
    SCRAP_METAL("Scrap Metal"),
    CAR_BATTERY("Car Battery"),
    OLD_CLOTHES("Old Clothes");

    private final String displayName;

    RecyclableItem(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getTexturePath() {
        switch (this) {
            case PLASTIC_BOTTLE:
                return "sprite/recyclable/plastic_bottle.png";
            case ALUMINUM_CAN:
                return "sprite/recyclable/aluminum_can.png";
            case GLASS_BOTTLE:
                return "sprite/recyclable/glass_bottle.png";
            case NEWSPAPER:
                return "sprite/recyclable/newspaper.png";
            case CARDBOARD_BOX:
                return "sprite/recyclable/cardboard_box.png";
            case E_WASTE:
                return "sprite/recyclable/e_waste.png";
            case OLD_TIRE:
                return "sprite/recyclable/old_tire.png";
            case SCRAP_METAL:
                return "sprite/recyclable/scrap_metal.png";
            case CAR_BATTERY:
                return "sprite/recyclable/car_battery.png";
            case OLD_CLOTHES:
                return "sprite/recyclable/old_clothes.png";
            default:
                return "sprite/placeholder.png";
        }
    }
}
