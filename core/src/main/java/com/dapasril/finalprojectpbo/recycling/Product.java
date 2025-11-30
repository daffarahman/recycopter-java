package com.dapasril.finalprojectpbo.recycling;

public enum Product {
    RECYCLED_PLASTIC("Recycled Plastic", 50),
    ALUMINUM_INGOT("Aluminum Ingot", 75),
    GLASS_BLOCK("Glass Block", 60),
    RECYCLED_PAPER("Recycled Paper", 40),
    CARDBOARD_BOX("Cardboard Box", 45),
    CIRCUIT_BOARD("Circuit Board", 150),
    RUBBER_MAT("Rubber Mat", 80),
    STEEL_BEAM("Steel Beam", 100),
    POWER_CELL("Power Cell", 200),
    TEXTILE_ROLL("Textile Roll", 70);

    private final String displayName;
    private final int sellValue;

    Product(String displayName, int sellValue) {
        this.displayName = displayName;
        this.sellValue = sellValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSellValue() {
        return sellValue;
    }

    public String getTexturePath() {
        switch (this) {
            case RECYCLED_PLASTIC:
                return "sprite/product/recycled_plastic.png";
            case ALUMINUM_INGOT:
                return "sprite/product/aluminum_ingot.png";
            case GLASS_BLOCK:
                return "sprite/product/glass_block.png";
            case RECYCLED_PAPER:
                return "sprite/product/recycled_paper.png";
            case CARDBOARD_BOX:
                return "sprite/product/cardboard_box.png";
            case CIRCUIT_BOARD:
                return "sprite/product/circuit_board.png";
            case RUBBER_MAT:
                return "sprite/product/rubber_mat.png";
            case STEEL_BEAM:
                return "sprite/product/steel_beam.png";
            case POWER_CELL:
                return "sprite/product/power_cell.png";
            case TEXTILE_ROLL:
                return "sprite/product/textile_roll.png";
            default:
                return "sprite/placeholder.png";
        }
    }
}
