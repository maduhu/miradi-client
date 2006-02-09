/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.threatmatrix;

import java.awt.Color;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.conservationmeasures.eam.objects.ThreatPriority;
import org.conservationmeasures.eam.project.Project;

public class ThreatSummaryPanel extends JPanel
{
	public ThreatSummaryPanel()
	{
		ThreatPriority priority = getPriority();
		JButton highButton = new JButton(priority.toString());
		Color priorityColor = Project.getPriorityColor(priority);
		highButton.setBackground(priorityColor);
		highButton.addActionListener(new RatingSummaryButtonHandler(this));
		add(highButton);
		setBackground(priorityColor);
	}
	
	public ThreatPriority getPriority()
	{
		int value = Math.abs(new Random().nextInt()) % 4;
		return ThreatPriority.createFromInt(value);
	}
}
