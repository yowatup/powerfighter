package org.pfighter.nodes;

import java.awt.Rectangle;

import org.pfighter.FighterSettings;
import org.pfighter.util.LocalPlayerHealth;
import org.powerbot.core.Bot;
import org.powerbot.core.script.job.Task;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.interactive.Character;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

public class TargetSelector extends Node
{
	private static NPC currentTarget;
	
	public static NPC currentTarget()
	{
		return currentTarget;
	}
	
	private static final Filter<NPC> TARGET_FILTER = new Filter<NPC>()
	{
		public boolean accept(NPC n)
		{
			if(FighterSettings.npcIdMatches(n.getId()))
			{
				Character interacting = n.getInteracting();
				return interacting == null
						&& n.getHealthPercent() > 0;
			}
			return false;
		}
	};
	
	private static final Filter<NPC> RETALIATE_FILTER = new Filter<NPC>()
	{
		public boolean accept(NPC n)
		{
			Character interacting = n.getInteracting();
			return interacting != null
					&& interacting.equals(Players.getLocal())
					&& n.getHealthPercent() > 0;
		}
	};
	
	@Override
	public boolean activate()
	{
		if(LocalPlayerHealth.getPercent() < 30)
		{
			Bot.context().getScriptHandler().stop();
			return false;
		}
		
		if(currentTarget == null
				|| !currentTarget.validate()
				|| currentTarget.getHealthPercent() == 0
				|| !currentTarget.isOnScreen())
		{
			System.out.println("resetting target");
			currentTarget = null;
			return true;
		}
		else
		{
			if(Players.getLocal().isMoving())
				return false;
			Character interacting = Players.getLocal().getInteracting();
			if(interacting == null || interacting.getHealthPercent() == 0)
				return true;
			
			Character interacting2 = interacting.getInteracting();
			return interacting2 != null && !interacting2.equals(Players
					.getLocal());
		}
	}
	
	@Override
	public void execute()
	{
		NPC retaliate = NPCs.getNearest(RETALIATE_FILTER);
		if(retaliate != null)
		{
			if(!retaliate.equals(currentTarget))
			{
				attack(retaliate);
				currentTarget = retaliate;
				Task.sleep(300, 500);
			}
			return;
		}
		
		if(currentTarget != null)
		{
			Character targetInteracting = currentTarget.getInteracting();
			if(targetInteracting == null
					|| targetInteracting.equals(Players.getLocal()))
			{
				attack(currentTarget);
				Task.sleep(300, 500);
				return;
			}
		}
		
		NPC target = NPCs.getNearest(TARGET_FILTER);
		if(!target.equals(currentTarget))
		{
			attack(target);
			currentTarget = target;
			Task.sleep(300, 500);
		}
	}
	
	private static boolean isOnScreen(Character c)
	{
		if(!c.isOnScreen())
			return false;
		WidgetChild actionbar = Widgets.get(640, 6);
		if(actionbar == null || !actionbar.isOnScreen())
			return true;
		Rectangle abBounds = actionbar.getBoundingRectangle();
		return c.getCentralPoint().getY() < abBounds.getMinY();
	}
	
	private static void attack(NPC npc)
	{
		if(!isOnScreen(npc))
		{
			Walking.walk(npc);
		}
		npc.interact("Attack");
	}
}
