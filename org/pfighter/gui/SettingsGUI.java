package org.pfighter.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.pfighter.FighterSettings;
import org.powerbot.core.script.job.Task;

public class SettingsGUI
{
	private static final String DEFAULT_NPC_IDS = "";
	private static final String DEFAULT_WEAPON_ID = "";
	private static final String DEFAULT_SHIELD_ID = "";
	
	private JFrame frame;
	
	private JCheckBox rejuvBox;
	private JCheckBox switchWeaponBox;
	
	private JDigitTextField weaponIdField;
	private JDigitTextField shieldIdField;
	private JTextField npcIdsField;
	
	private volatile boolean started = false;
	
	public SettingsGUI()
	{
		JPanel npcIds = new JPanel();
		npcIds.add(new JLabel("NPC Ids:"));
		npcIdsField = new JTextField(DEFAULT_NPC_IDS, 10);
		npcIds.add(npcIdsField);

		JPanel weaponId = new JPanel();
		weaponId.add(new JLabel("Weapon Id:"));
		weaponIdField = new JDigitTextField(DEFAULT_WEAPON_ID);
		weaponIdField.setEnabled(false);
		weaponId.add(weaponIdField);

		JPanel shieldId = new JPanel();
		shieldId.add(new JLabel("Shield Id:"));
		shieldIdField = new JDigitTextField(DEFAULT_SHIELD_ID);
		shieldIdField.setEnabled(false);
		shieldId.add(shieldIdField);
		
		switchWeaponBox = new JCheckBox("Switch Weapons", false);
		switchWeaponBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(switchWeaponBox.isSelected())
				{
					weaponIdField.setEnabled(true);
					shieldIdField.setEnabled(true);
				}
				else
				{
					weaponIdField.setEnabled(false);
					shieldIdField.setEnabled(false);
				}
			}
		});
		
		rejuvBox = new JCheckBox("Rejuvinate", true);
		rejuvBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(rejuvBox.isSelected())
				{
					switchWeaponBox.setEnabled(true);
					if(switchWeaponBox.isSelected())
					{
						weaponIdField.setEnabled(true);
						shieldIdField.setEnabled(true);
					}
				}
				else
				{
					switchWeaponBox.setEnabled(false);
					weaponIdField.setEnabled(false);
					shieldIdField.setEnabled(false);
				}
			}
		});

		JPanel fightSettings = new JPanel();
		fightSettings.setLayout(new BoxLayout(fightSettings, BoxLayout.PAGE_AXIS));
		fightSettings.add(npcIds);
		fightSettings.add(rejuvBox);
		fightSettings.add(switchWeaponBox);
		fightSettings.add(weaponId);
		fightSettings.add(shieldId);
		
		final JButton startButton = new JButton("start");
		startButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				startButton.setEnabled(false);
				
				try
				{
					List<Integer> ids = getNpcIds();
					for(int id : ids)
					{
						FighterSettings.addNPCId(id);
					}
				}
				catch(NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(null,
							"invalid npc ids", "error in initialization",
							JOptionPane.ERROR_MESSAGE);
					startButton.setEnabled(true);
					return;
				}
				
				boolean rejuv = rejuvBox.isSelected();
				FighterSettings.rejuvinate = rejuv;
				if(rejuv)
				{
					boolean switchWep = switchWeaponBox.isSelected();
					FighterSettings.switchWeapons = switchWep;
					if(switchWep)
					{
						int weaponId = weaponIdField.getValue();
						int shieldId = shieldIdField.getValue();
						
						FighterSettings.weaponId = weaponId;
						FighterSettings.shieldId = shieldId;
					}
				}

				frame.setVisible(false);
				started = true;
			}
		});
		
		JTabbedPane pane = new JTabbedPane();
		pane.add("Fight Settings", fightSettings);
		pane.add("Start", startButton);
		
		frame = new JFrame("settings");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(pane);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void waitForInitialization()
	{
		while(!started)
		{
			System.out.println("waiting");
			Task.sleep(200);
		}
	}
	
	public List<Integer> getNpcIds()
	{
		String str = npcIdsField.getText();
		String[] values = str.split(", *");
		List<Integer> ids = new ArrayList<>();
		for(String s : values)
		{
			Integer id = Integer.parseInt(s);
			if(id < 0)
				throw new NumberFormatException();
			ids.add(id);
		}
		return ids;
	}
}
