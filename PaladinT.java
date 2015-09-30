package scripts;

import java.awt.Point;
import org.tribot.api.DynamicClicking;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Walking;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;

@ScriptManifest(authors={"Hsxu"}, category="Thieving", name="PaladinThieveBETA")
public class PaladinT
extends Script {
    RSTile bankTile = new RSTile(2654, 3283, 0);
    RSTile paladinTile = new RSTile(2581, 3298, 0);
    RSTile safeTile = new RSTile(2590, 3306, 0);
    RSTile lumbyTile = new RSTile(3222, 3219, 0);
	
    RSTile[] pathToBank = new RSTile[]{new RSTile(2586, 3297, 0), 
					      new RSTile(2592, 3297, 0), 
					      new RSTile(2597, 3297, 0), 
					      new RSTile(2605, 3296, 0), 
					      new RSTile(2611, 3297, 0), 
					      new RSTile(2616, 3298, 0), 
					      new RSTile(2625, 3298, 0), 
					      new RSTile(2631, 3290, 0), 
					      new RSTile(2640, 3286, 0), 
					      new RSTile(2645, 3283, 0), 
					      BankTile};
											  
    RSTile[] pathToPaladins = new RSTile[]{new RSTile(2645, 3283, 0), 
						  new RSTile(2640, 3288, 0), 
						  new RSTile(2631, 3296, 0), 
						  new RSTile(2625, 3298, 0), 
						  new RSTile(2616, 3298, 0), 
						  new RSTile(2611, 3297, 0), 
						  new RSTile(2605, 3296, 0), 
						  new RSTile(2597, 3297, 0), 
						  new RSTile(2592, 3297, 0), 
						  new RSTile(2586, 3297, 0), 
						  PaladinTile};
    final int numFood = 25;
    final int foodID = 379;
    final int eatatHP = 15;
    boolean openBank = false;
    boolean returnToBank = false;

    public void run() {
        Initialize();
        do {
            executeState();
            sleep(200, 300);
        } while (true);
    }

    private void initialize() {
        Mouse.setSpeed(250);
        Camera.setCameraAngle(100);
        Walking.control_click = true;
		
        RSItem[] food = Inventory.find(new int[]{foodID});
        if (food.length < 1) {
            returnToBank = true;
            openBank = true;
        }
    }

    public void executeState() {
        RSTile CurrentPosition = Player.getPosition();
        int distanceToBank = CurrentPosition.distanceTo((Positionable)bankTile);
        int distanceToPaladins = CurrentPosition.distanceTo((Positionable)paladinTile);
        int distanceToSafeTile = CurrentPosition.distanceTo((Positionable)safeTile);
        int distanceToLumby = CurrentPosition.distanceTo((Positionable)lumbyTile);
		
        if (Banking.isBankScreenOpen()) {
            bank();
        } else if (distanceToBank < 5 && openBank) {
            openBank();
        } else if (!(distanceToBank >= 5 || openBank)) {
            walkToPaladins();
        } else if (distanceToPaladins < 12 && returnToBank) {
            walkToBank();
        } else if (!(distanceToPaladins >= 12 || returnToBank)) {
            pickpocket();
        } else if (distanceToSafeTile < 5) {
            Walking.walkTo((Positionable)paladinTile);
        } else if (distanceToLumby < 5) {
            WebWalking.walkTo((Positionable)bankTile);
        }
    }

    private void openBank() {
        println("Opening Bank.");
        int openBankAttempts = 0;
        Banking.openBankBooth();
        do {
            Banking.openBankBooth();
            sleep(1000, 1200);
        } while (!Banking.isBankScreenOpen() || ++openBankAttempts == 10);
    }

    private void walkToPaladins() {
        println("Walking to Paladins.");
        Walking.walkPath((Positionable[])pathToPaladins);
        while (Player.isMoving()) {
            sleep(200, 300);
        }
    }

    private void walkToBank() {
        println("Walking to Bank.");
        Walking.walkPath((Positionable[])pathToBank);
        while (Player.isMoving()) {
            sleep(200, 300);
        }
        openBank = true;
    }

    private void pickpocket() {
        Paladin = NPCs.findNearest(new int[]{2709});
        println("Pickpocketing");
        for (i = 0; i < 50; ++i) {
            Mouse.click((Point)new Point(
				Projection.tileToMinimap(
					(Positionable)new RSTile(Paladin[0].getPosition().getX(), 
					Paladin[0].getPosition().getY(), 
					Paladin[0].getPosition().getPlane()))),
					0);
					
            DynamicClicking.clickRSNPC(Paladin[0], ("Pickpocket Pal");
            sleep(100, 125);
            if (Player.getRSPlayer().isInCombat()) {
                sleep(1500, 1700);
            }
			
            if (Paladin[0].isInCombat() && Player.getRSPlayer().isInCombat()) {
                Walking.walkTo((Positionable)safeTile);
                sleep(5000, 6000);
                break;
            }
			
            if (!checkAllStatus()) break;
        }
        Walking.walkTo(paladinTile);
        println("Resetting Tile!");
    }

    private void bank() {
        returnToBank = false;
        println("Banking.");
        Banking.depositAll();
        Banking.withdrawNumFood, (new int[]{foodID}));
        Banking.close();
        sleep(1000, 1200);
        openBank = false;
    }

    private boolean checkAllStatus() {
        RSItem[] food = Inventory.find(new int[]{FoodID});
        if (food.length < 1) {
            println("Returning to the bank.");
            sleep(3000, 4000);
            returnToBank = true;
            return false;
        }
        if (food.length > 0 && Skills.getCurrentLevel("HITPOINTS") < eatatHP) {
            food[0].click(new String[]{"Eat"});
        }
        return true;
    }
}
