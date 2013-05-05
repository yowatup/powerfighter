package org.pfighter.nodes;

import org.pfighter.util.ActionBar;
import org.pfighter.util.ActionBar.Ability;
import org.pfighter.util.ActionBar.AbilityType;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.wrappers.interactive.Character;

public class BasicAbilityUser extends Node
{
	@Override
	public boolean activate()
	{
		Character target = Players.getLocal().getInteracting();
		return target != null
				&& getAvailableSlot() != null;
	}

	@Override
	public void execute()
	{
		ActionBar.Slot slot = getAvailableSlot();
		slot.activate(true);
		Task.sleep(200, 500);
	}
	
	private static ActionBar.Slot getAvailableSlot()
	{
		for(ActionBar.Slot slot : ActionBar.Slot.values())
		{
			if(slot.isAvailable() && !slot.getCooldownWidget().isOnScreen())
			{
				Ability abil = ActionBar.getAbilityAt(slot.getIndex());
				if(abil != null && abil.getAbilityType() == AbilityType.BASIC)
				{
					return slot;
				}
			}
		}
		return null;
	}
}
