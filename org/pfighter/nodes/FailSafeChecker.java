package org.pfighter.nodes;

import org.pfighter.FighterSettings;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.tab.Equipment;
import org.powerbot.game.api.methods.tab.Inventory;

public class FailSafeChecker extends Node
{
	@Override
	public boolean activate()
	{
		return true;
	}

	@Override
	public void execute()
	{
		if(FighterSettings.rejuvinate && FighterSettings.switchWeapons)
		{
			if(Inventory.contains(FighterSettings.weaponId) && !Equipment.appearanceContainsOneOf(FighterSettings.weaponId))
			{
				Equipment.equip(FighterSettings.weaponId);
			}
		}
	}
}
