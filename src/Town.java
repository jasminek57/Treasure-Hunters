import java.awt.*;

/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean gameOver;
    private boolean dugForGold;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();
        gameOver = false;
        dugForGold = false;

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
    }

    public String getLatestNews() {
        return printMessage;
    }


    /** returns the Boolean vaule gameOver */
   public Boolean getGameOver(){
        return gameOver;
   }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";

        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown(Boolean easyMode) {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (!easyMode) {
                if (checkItemBreak()) {
                    hunter.removeItemFromKit(item);
                    printMessage += "\nUnfortunately, your lost your " + item + ".";
                }
            }

            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have a " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        shop.enter(hunter, choice, hunter.hasItemInKit("sword"));
        printMessage = "You have left the shop";
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble(Boolean hasSword) {
        double noTroubleChance;
        double chanceOfWinning;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }

        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n" + Colors.RESET;
            int goldDiff = (int) (Math.random() * 10) + 1;

            if (hasSword){
                chanceOfWinning = 1000;
            } else {
                chanceOfWinning = Math.random();
            }

            if (chanceOfWinning > noTroubleChance) {
                if (hasSword) {
                    printMessage += Colors.RED + "The brawler, seeing your sword realizes he picked a losing fight and give you all his gold. (" + goldDiff + " gold)";
                } else {
                printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                }
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                gameOver = hunter.changeGold(-goldDiff);
            }
        }
    }

    /**
     * Gives the hunter a chance to dig for some gold only if they have a shovel.<p>
     * The chances of finding gold are exactly 50%.<p>
     * The hunter can only dig for some gold once in a given town.
     */
    public void lookForGold() {
        if ((hunter.getInventory().indexOf("shovel") > -1) && (dugForGold == false)){
            int goldChance = (int)(Math.random() * 2) + 1;
            if (goldChance == 1){
                int gold = (int)(Math.random() * 20) + 1;
                printMessage = "You dug up " + gold + " gold!";
                hunter.changeGold(gold);
                dugForGold = true;
            } else {
                printMessage = "You dug but only found dirt";
                dugForGold = true;
            }
        } else if (!hunter.getInventory().contains("shovel")){
            printMessage = "You can't dig for gold without a shovel";
        } else {
            printMessage = "You already dug for gold in this town.";
        }
    }

    public String lookForTreasure() {
        String item = "";

        double rand = Math.random();
        if (rand < .25) {
            item = "crown";
        } else if (rand < .5) {
            item = "gem";
        } else if (rand < .75) {
            item = "trophy";
        } else {
            item = "dirt";
        }
        if (!item.equals("dirt") && !hunter.hasItemInTreasure(item)){
            printMessage = "";
            hunter.implementItem(item);
            return "You have found a " + item + "!";
        } else if(hunter.hasItemInTreasure(item)) {
            printMessage = "";
            return "you have already found a " + item + "!";
        } else {
            printMessage = "";
            return "You have found " + item + "!";
        }
    }

    public String toString() {
        return "This nice little town is surrounded by " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random();
        if (rnd < .166) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .332) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .498) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .664) {
            return new Terrain("Desert", "Water");
        } else if (rnd < .83) {
            return new Terrain("Marsh", "Boots");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

}