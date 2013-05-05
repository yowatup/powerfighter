package org.pfighter;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pfighter.gui.SettingsGUI;
import org.pfighter.nodes.BasicAbilityUser;
import org.pfighter.nodes.FailSafeChecker;
import org.pfighter.nodes.RejuvinateUser;
import org.pfighter.nodes.TargetSelector;
import org.pfighter.nodes.ThresholdAbilityUser;
import org.pfighter.nodes.UltimateAbilityUser;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.core.script.job.state.Node;
import org.powerbot.core.script.job.state.Tree;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.SkillData;
import org.powerbot.game.api.util.Timer;

@Manifest(authors = {"yowatup"}, name = "Power Fighter", description = "fights stuff")
public class PowerFighter extends ActiveScript implements PaintListener
{
	private SkillData skillData;
	private Timer timer;
	
	private volatile boolean initialized = false;
	
	private Tree jobNodes;
	private List<Node> checkerNodes;
	
	@Override
	public void onStart()
	{
		SettingsGUI settings = new SettingsGUI();
		settings.waitForInitialization();
		
		Node[] nodes = new Node[]
		{
			new RejuvinateUser(),
			new TargetSelector(),
			new UltimateAbilityUser(),
			new ThresholdAbilityUser(),
			new BasicAbilityUser(),
		};
		Node[] checkerNodes = new Node[]
		{
			new FailSafeChecker(),
		};
		
		
		this.jobNodes = new Tree(nodes);
		this.checkerNodes = Arrays.asList(checkerNodes);
		
		this.timer = new Timer(0);
		this.skillData = new SkillData();
		
		this.initialized = true;
	}
	
	@Override
	public int loop()
	{
		if(initialized)
		{
			for(Node node : checkerNodes)
			{
				if(node.activate())
				{
					node.execute();
				}
			}
			
			final Node job = this.jobNodes.state();
			if(job != null)
			{
				System.out.println("executing: " + job);
				this.jobNodes.set(job);
				getContainer().submit(job);
				job.join();
			}
		}
		return Random.nextInt(10, 50);
	}
	
	private static final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
	private static final Color bgColor = new Color(0, 0, 0, 200);
	private static final Color color1 = Color.WHITE;
	
	private static final Map<Integer, String> skillMap;
	static
	{
		skillMap = new HashMap<>();
		skillMap.put(Skills.ATTACK, "Attack");
		skillMap.put(Skills.CONSTITUTION, "Constitution");
		skillMap.put(Skills.DEFENSE, "Defence");
		skillMap.put(Skills.MAGIC, "Magic");
		skillMap.put(Skills.RANGE, "Range");
	}
	
	@Override
	public void onRepaint(Graphics g)
	{
		if(!initialized)
			return;
		g.setFont(font);
		int yOffset = 0;
		int totalExp = 0;
		for(Map.Entry<Integer, String> entry : skillMap.entrySet())
		{
			int i = entry.getKey();
			int expGain = skillData.experience(i);
			if(expGain > 0)
			{
				g.setColor(bgColor);
				g.fillRect(0, yOffset, 480, 17);
				g.setColor(color1);
				String skillString = entry.getValue()
						+ " - level: " + Skills.getRealLevel(i)
						+ " - exp gained: " + expGain
						+ " - exp per hour: " + skillData.experience(
								SkillData.Rate.HOUR, i)
						+ " - time to level: "
						+ longToTime(skillData.timeToLevel(SkillData.Rate.HOUR, i));
				g.drawString(skillString, 10, yOffset + 14);
				yOffset += 17;
				totalExp += expGain;
			}
		}
		g.setColor(bgColor);
		g.fillRect(0, yOffset, 480, 20);
		g.setColor(color1);
		g.drawString("time: " + longToTime(timer.getElapsed())
				+ " - total exp: " + totalExp
				+ " - total exp per hour: " + (long)totalExp * 60 * 60 * 1000 / timer.getElapsed()
				, 8 , yOffset + 14);
	}
	
	private static String longToTime(long timeMillis)
	{
		long time = timeMillis / 1000;
		String seconds = Integer.toString((int)(time % 60));
		String minutes = Integer.toString((int)((time % 3600) / 60));
		String hours = Integer.toString((int)(time / 3600));
		for(int i = 0; i < 2; ++i)
		{
			if(seconds.length() < 2)
			{
				seconds = "0" + seconds;
			}
			if(minutes.length() < 2)
			{
				minutes = "0" + minutes;
			}
			if(hours.length() < 2)
			{
				hours = "0" + hours;
			}
		}
		return hours + "h:" + minutes + "m:" + seconds + "s";
		
	}
}
