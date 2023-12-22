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
    private String treasure;
    private boolean isSearched;
    private boolean dugAlready;
    private boolean easyMode;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness) {
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;

        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);
        easyMode = false;
        if (toughness == 0.25) {
            easyMode = true;
        }
        treasure = null;
    }

    public String getLatestNews() {
        return printMessage;
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

        int random = (int) (Math.random() * 4) + 1;
        if (random == 1) {
            treasure = "a crown";
        } else if (random == 2) {
            treasure = "a trophy";
        } else if (random == 3) {
            treasure = "a gem";
        } else {
            treasure = "dust";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + Colors.CYAN + terrain.getTerrainName() + Colors.RESET + ".";
            if (checkItemBreak()) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item + ".";
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
        printMessage = "";
        shop.enter(hunter, choice);
    }

    public void leaveShop() {
        printMessage = "You left the shop";
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        if (hunter.isSamuraiMode() && hunter.hasItemInKit("sword")) {
            double noTroubleChance;
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
                printMessage += Colors.GREEN + "The brawler ran away after seeing your sword" + Colors.RESET;
                printMessage += "\nGold was dropped! You collected " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                hunter.changeGold(goldDiff);
            }
        } else {
            double noTroubleChance;
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
                if (easyMode) {
                    if (Math.random() > 0.25) {
                        printMessage += Colors.GREEN + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                        printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                        hunter.changeGold(goldDiff);
                    } else {
                        printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                        hunter.changeGold(-goldDiff);
                        if (hunter.getGold() >= 0) {
                            printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                        }
                    }
                } else {
                    if (Math.random() > noTroubleChance) {
                        printMessage += Colors.GREEN + "Okay, stranger! You proved yer mettle. Here, take my gold." + Colors.RESET;
                        printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + Colors.RESET + " gold.";
                        hunter.changeGold(goldDiff);
                    } else {
                        printMessage += Colors.RED + "That'll teach you to go lookin' fer trouble in MY town! Now pay up!" + Colors.RESET;
                        hunter.changeGold(-goldDiff);
                        if (hunter.getGold() >= 0) {
                            printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold.";
                        }
                    }
                }
            }
        }
    }

    public void digGold() {
        if(hunter.hasItemInKit("shovel") && !dugAlready) {
            double rnd = (int) (Math.random() * 2);
            int goldDiff = (int) (Math.random() * 20) + 1;
            if (rnd == 1) {
                printMessage = "You dug up " + goldDiff + " gold!";
                dugAlready = true;
                hunter.changeGold(goldDiff);
            } else {
                printMessage = "You dug but only found dirt";
                dugAlready = true;
            }
        } else if (!hunter.hasItemInKit("shovel")) {
            printMessage = "You can't dig dirt without a shovel!";
        } else if (dugAlready) {
            printMessage = "You already dug for gold in this town";
        }

    }

    public void searchForTreasure() {
        if (!isSearched) {
            if (!hunter.hasTreasure(treasure)) {
                if (treasure.equals("dust")) {
                    printMessage = ("You found " + treasure + "!");
                    isSearched = true;
                } else {
                    printMessage = ("You found " + treasure + "!");
                    hunter.collectTreasure(treasure);
                    isSearched = true;
                }
            } else {
                printMessage = ("You have already collected this treasure. Don't collect it again.");
            }
        } else {
            printMessage = ("You have already searched this town.");
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
        double rnd = (int) (Math.random() * 6) + 1;
        if (rnd == 1) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd == 2) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd == 3) {
            return new Terrain("Plains", "Horse");
        } else if (rnd == 4) {
            return new Terrain("Desert", "Water");
        } else if (rnd == 5) {
            return new Terrain("Jungle", "Machete");
        } else {
            return new Terrain("Marsh", "Boot");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        if (easyMode) {
            return (false);
        } else {
            double rand = Math.random();
            return (rand < 0.5);
        }
    }
}