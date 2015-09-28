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
    public RSTile bankTile = new RSTile(2654, 3283, 0);
    public RSTile paladinTile = new RSTile(2581, 3298, 0);
    public RSTile safeTile = new RSTile(2590, 3306, 0);
    public RSTile lumbyTile = new RSTile(3222, 3219, 0);
	
    public RSTile[] pathToBank = new RSTile[]{new RSTile(2586, 3297, 0), 
					      new RSTile(2592, 3297, 0), 
					      new RSTile(2597, 3297, 0), 
					      new RSTile(2605, 3296, 0), 
					      new RSTile(2611, 3297, 0), 
					      new RSTile(2616, 3298, 0), 
					      new RSTile(2625, 3298, 0), 
					      new RSTile(2631, 3290, 0), 
					      new RSTile(2640, 3286, 0), 
					      new RSTile(2645, 3283, 0), 
					      this.BankTile};
											  
    public RSTile[] pathToPaladins = new RSTile[]{new RSTile(2645, 3283, 0), 
						  new RSTile(2640, 3288, 0), 
						  new RSTile(2631, 3296, 0), 
						  new RSTile(2625, 3298, 0), 
						  new RSTile(2616, 3298, 0), 
						  new RSTile(2611, 3297, 0), 
						  new RSTile(2605, 3296, 0), 
						  new RSTile(2597, 3297, 0), 
						  new RSTile(2592, 3297, 0), 
						  new RSTile(2586, 3297, 0), 
						  this.PaladinTile};
    int numFood = 25;
    int foodID = 379;
    int eatatHP = 15;
    boolean openBank = false;
    boolean returnToBank = false;

    public void run() {
        this.Initialize();
        do {
            this.executeState();
            this.sleep(200, 300);
        } while (true);
    }

    private void initialize() {
        Mouse.setSpeed(250);
        Camera.setCameraAngle(100);
        Walking.control_click = true;
		
        RSItem[] Food = Inventory.find((int[])new int[]{this.foodID});
        if (Food.length < 1) {
            this.returnToBank = true;
            this.openBank = true;
        }
    }

    public void executeState() {
        RSTile CurrentPosition = Player.getPosition();
        int distanceToBank = CurrentPosition.distanceTo((Positionable)this.bankTile);
        int distanceToPaladins = CurrentPosition.distanceTo((Positionable)this.paladinTile);
        int distanceToSafeTile = CurrentPosition.distanceTo((Positionable)this.safeTile);
        int distanceToLumby = CurrentPosition.distanceTo((Positionable)this.lumbyTile);
		
        if (Banking.isBankScreenOpen()) {
            this.Bank();
        }
        if (DistanceToBank < 5 && this.OpenBank) {
            this.openBank();
        }
        if (!(DistanceToBank >= 5 || this.OpenBank)) {
            this.walkToPaladins();
        }
        if (DistanceToPaladins < 12 && this.ReturnToBank) {
            this.walkToBank();
        }
        if (!(DistanceToPaladins >= 12 || this.ReturnToBank)) {
            this.pickpocket();
        }
        if (DistanceToSafeTile < 5) {
            Walking.walkTo((Positionable)this.paladinTile);
        }
        if (DistanceToLumby < 5) {
            WebWalking.walkTo((Positionable)this.bankTile);
        }
    }

    private void openBank() {
        this.println((Object)"Opening Bank.");
        int openBankAttempts = 0;
        Banking.openBankBooth();
        do {
            Banking.openBankBooth();
            this.sleep(1000, 1200);
        } while (!Banking.isBankScreenOpen() || ++openBankAttempts == 10);
    }

    private void walkToPaladins() {
        this.println((Object)"Walking to Paladins.");
        Walking.walkPath((Positionable[])this.pathToPaladins);
        while (Player.isMoving()) {
            this.sleep(200, 300);
        }
    }

    private void walkToBank() {
        this.println((Object)"Walking to Bank.");
        Walking.walkPath((Positionable[])this.pathToBank);
        while (Player.isMoving()) {
            this.sleep(200, 300);
        }
        this.openBank = true;
    }

    private void Pickpocket() {
        Paladin = NPCs.findNearest((int[])new int[]{2709});
        this.println((Object)"Pickpocketing");
        for (i = 0; i < 50; ++i) {
            Mouse.click((Point)new Point(
				Projection.tileToMinimap(
					(Positionable)new RSTile(Paladin[0].getPosition().getX(), 
					Paladin[0].getPosition().getY(), 
					Paladin[0].getPosition().getPlane()))),
					(int)0);
					
            DynamicClicking.clickRSNPC((RSCharacter)Paladin[0], (String)"Pickpocket Pal");
            this.sleep(100, 125);
            if (Player.getRSPlayer().isInCombat()) {
                this.sleep(1500, 1700);
            }
			
            if (Paladin[0].isInCombat() && Player.getRSPlayer().isInCombat()) {
                Walking.walkTo((Positionable)this.safeTile);
                this.sleep(5000, 6000);
                break;
            }
			
            if (!this.checkAllStatus()) break;
        }
        Walking.walkTo((Positionable)this.paladinTile);
        this.println((Object)"Resetting Tile!");
    }

    private void bank() {
        this.returnToBank = false;
        this.println((Object)"Banking.");
        Banking.depositAll();
        Banking.withdraw((int)this.NumFood, (int[])new int[]{this.foodID});
        Banking.close();
        this.sleep(1000, 1200);
        this.openBank = false;
    }

    private boolean checkAllStatus() {
        RSItem[] Food = Inventory.find((int[])new int[]{this.FoodID});
        if (Food.length < 1) {
            this.println((Object)"Returning to the bank.");
            this.sleep(3000, 4000);
            this.returnToBank = true;
            return false;
        }
        if (Food.length > 0 && Skills.getCurrentLevel((String)"HITPOINTS") < this.eatatHP) {
            Food[0].click(new String[]{"Eat"});
        }
        return true;
    }
}
