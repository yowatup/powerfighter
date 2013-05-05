package org.pfighter;

import java.util.HashSet;
import java.util.Set;

public class FighterSettings
{
	private static final Set<Integer> npcIds = new HashSet<>();
	
	public static boolean rejuvinate;
	public static boolean switchWeapons;
	public static int weaponId;
	public static int shieldId;
	
	public static void addNPCId(int id)
	{
		npcIds.add(id);
	}
	
	public static boolean npcIdMatches(int id)
	{
		return npcIds.contains(id);
	}
	
}
