package org.pfighter.util;

import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;

public class LocalPlayerHealth
{
	public static int getPercent()
	{
		if(Players.getLocal().isInCombat())
		{
			return Players.getLocal().getHealthPercent();
		}
		else
		{
			int hpWidgetHeight = Widgets.get(748, 5).getHeight();
			int percent = (Math.abs(100 - 100 * hpWidgetHeight / 28) * 120 / 100);
			return percent <= 100 ? percent : 100;
		}
	}
	
	private LocalPlayerHealth()
	{
	}
}
