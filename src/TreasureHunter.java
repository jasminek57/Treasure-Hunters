import java.util.Scanner;

/**
 * This class is responsible for controlling the Treasure Hunter game.<p>
 * It handles the display of the menu and the processing of the player's choices.<p>
 * It handles all the display based on the messages it receives from the Town object. <p>
 *
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class TreasureHunter {
    // static variables
    private static final Scanner SCANNER = new Scanner(System.in);

    // instance variables
    private Town currentTown;
    private int pastTown;
    private int town;
    private Hunter hunter;
    private boolean hardMode;
    private boolean testMode;
    private boolean easyMode;
    private boolean samuariMode;
    private boolean gameOver;

    /**
     * Constructs the Treasure Hunter game.
     */
    public TreasureHunter() {
        // these will be initialized in the play method
        currentTown = null;
        hunter = null;
        hardMode = false;
        easyMode = false;
        samuariMode = false;
        gameOver = false;
        pastTown = 0;
        town  = 1;
    }

    /**
     * Starts the game; this is the only public method
     */
    public void play() {
        welcomePlayer();
        enterTown();
        showMenu();
    }

    /**
     * Creates a hunter object at the beginning of the game and populates the class member variable with it.
     */
    private void welcomePlayer() {
        System.out.println("Welcome to TREASURE HUNTER!!");
        System.out.println("Going hunting for the big treasure, eh?");
        System.out.print("What's your name, Hunter? ");
        String name = SCANNER.nextLine().toLowerCase();


        System.out.print("Mode? Easy/Normal/Hard/Test (e/n/h/t): ");
        String mode = SCANNER.nextLine().toLowerCase();

        // three different modes which determine the difficulty of the game and one to just test the game
        if (mode.equals("h")) {
            hardMode = true;
        } else if (mode.equals("t")) {
            testMode = true;
            hunter = new Hunter(name, 100, true); // set hunter instance variable
        } else if (mode.equals("e")) {
            easyMode = true;
            hunter = new Hunter(name, 20, false);
        } else if (mode.equals("s")) {
            samuariMode = true;
            hunter = new Hunter(name, 10, false);
        } else {
            hunter = new Hunter(name, 10, false);
        }
    }

    /**
     * Creates a new town and adds the Hunter to it.
     */
    private void enterTown() {
        double markdown = 0.5;
        double toughness = 0.4;
        if (hardMode) {
            // in hard mode, you get less money back when you sell items
            markdown = 0.25;

            // and the town is "tougher"
            toughness = 0.75;
        } else if (easyMode) {
            // in easy mode, you get the full amount back
            markdown = 1;

            // and the town is less tough = it is easier to win a brawl
            toughness = 0.2;
        }

        Shop shop;
        if (samuariMode) {
            shop = new Shop(markdown, true, hunter.hasItemInKit("sword"));
        } else {
            shop = new Shop(markdown, false, false);
        }

        // creating the new Town -- which we need to store as an instance
        // variable in this class, since we need to access the Town
        // object in other methods of this class
        currentTown = new Town(shop, toughness);

        // calling the hunterArrives method, which takes the Hunter
        // as a parameter; note this also could have been done in the
        // constructor for Town, but this illustrates another way to associate
        // an object with an object of a different class
        currentTown.hunterArrives(hunter);
    }
    public String treasureInfo() {
        String[] t = hunter.getHunterTreasure();
        String message = "";
        if (t[0] == null) {
            for (int i = 0; i < t.length; i++) {
                if (!(t[i] == null)) {
                    message += " " + t[i] + ",";
                } else {
                    message += "";
                }

            }
        } else {
            message = " none";
        }
        return message;
    }

    /**
     * Displays the menu and receives the choice from the user.<p>
     * The choice is sent to the processChoice() method for parsing.<p>
     * This method will loop until the user chooses to exit.
     */
    private void showMenu() {
        String choice = "";

        while (!choice.equals("x")) {
            System.out.println();
            System.out.println(currentTown.getLatestNews());
            System.out.println("***");
            System.out.println(hunter);
            System.out.println("Treasure:" + treasureInfo());
            System.out.println(currentTown);
            System.out.println(Colors.BLUE + "(B)uy something at the shop.");
            System.out.println("(S)ell something at the shop.");
            System.out.println("(M)ove on to a different town.");
            System.out.println("(L)ook for trouble!");
            System.out.println("(D)ig for gold!");
            System.out.println("(H)unt for treasure!");
            System.out.println("Give up the hunt and e(X)it." + Colors.RESET);
            System.out.println();
            System.out.print("What's your next move? ");
            choice = SCANNER.nextLine().toLowerCase();
            processChoice(choice);
            if (currentTown.getGameOver()) {
                choice = "x";
                System.out.println("You have lost the brawl and all your gold..." + "\n" + "Game Over!");
            }
        }
    }

    /**
     * Takes the choice received from the menu and calls the appropriate method to carry out the instructions.
     * @param choice The action to process.
     */
    private void processChoice(String choice) {
        Boolean hasSword = hunter.hasItemInKit("sword");
        if (choice.equals("b") || choice.equals("s")) {
            currentTown.enterShop(choice);
        } else if (choice.equals("m")) {
            if (currentTown.leaveTown(easyMode)) {
                // This town is going away so print its news ahead of time.
                System.out.println(currentTown.getLatestNews());
                enterTown();
            }
            town ++;
        } else if (choice.equals("l")) {
            currentTown.lookForTrouble(hasSword);
        } else if (choice.equals("h")) {
            if (pastTown < town || pastTown > town) {
                System.out.print(currentTown.lookForTreasure());
                pastTown ++;
            } else {
                System.out.print("you have already searched this town\n");
            }
        } else if (choice.equals("d")) {
            currentTown.lookForGold();
        } else if (choice.equals("x")) {
            System.out.println("Fare thee well, " + hunter.getHunterName() + "!");
        } else {
            System.out.println("Yikes! That's an invalid option! Try again.");
        }
    }
}