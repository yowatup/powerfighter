package org.pfighter.nodes;

import org.pfighter.FighterSettings;
import org.pfighter.util.ActionBar;
import org.pfighter.util.ActionBar.Defence_Abilities;
import org.pfighter.util.LocalPlayerHealth;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Equipment;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.util.Timer;
import org.powerbot.game.api.wrappers.interactive.Character;

public class RejuvinateUser extends Node
{
	private static final int ACTIVATE_HEALTH_PERCENT = 60;

	@Override
	public boolean activate()
	{
		if(FighterSettings.rejuvinate
				&& LocalPlayerHealth.getPercent() < ACTIVATE_HEALTH_PERCENT
				&& ActionBar.getAdrenalinPercent() == 100)
		{

			Character interacting = Players.getLocal().getInteracting();
			return interacting == null || !interacting.validate() || interacting.getHealthPercent() == 0;
		}
		return false;
	}

	@Override
	public void execute()
	{
		ActionBar.Slot rejuv = ActionBar.getSlotWithAbility(Defence_Abilities.REJUVENATE);
		if(FighterSettings.switchWeapons)
		{
			if(ActionBar.getAdrenalinPercent() == 100 && !rejuv.getCooldownWidget().isOnScreen())
			{
				while(Inventory.contains(FighterSettings.shieldId))
				{
					Equipment.equip(FighterSettings.shieldId);
					Task.sleep(200, 500);
				}
				while(!rejuv.isAvailable() || rejuv.getCooldownWidget().isOnScreen())
				{
					Task.sleep(50);
				}
				
				while(!rejuv.getCooldownWidget().isOnScreen())
				{
					rejuv.activate(true);
					Task.sleep(200, 500);
				}
				Timer rejuvWait = new Timer(10500);
				Task.sleep(10500);
				while(rejuvWait.isRunning())
				{
					Task.sleep(20);
				}
				while(Inventory.contains(FighterSettings.weaponId))
				{
					Equipment.equip(FighterSettings.weaponId);
					Task.sleep(200, 500);
				}
			}
		}
		else
		{
			if(rejuv != null && rejuv.isAvailable())
			{
				rejuv.activate(true);
			}
		}
	} 
}
