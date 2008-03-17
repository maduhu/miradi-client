/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.dialogs.indicator;

import java.awt.BorderLayout;

import org.miradi.dialogs.base.DisposablePanelWithDescription;
import org.miradi.dialogs.base.ObjectDataInputPanel;
import org.miradi.dialogs.planning.MeasurementPropertiesPanel;
import org.miradi.dialogs.planning.propertiesPanel.BlankPropertiesPanel;
import org.miradi.dialogs.viability.IndicatorFutureStatusSubPanel;
import org.miradi.dialogs.viability.IndicatorPropertiesPanel;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Goal;
import org.miradi.objects.Indicator;
import org.miradi.objects.Measurement;

public class DirectIndicatorPropertiesPanel extends ObjectDataInputPanel
{
	public DirectIndicatorPropertiesPanel(MainWindow mainWindowToUse, ORef orefToUse) throws Exception
	{
		super(mainWindowToUse.getProject(), orefToUse);
		
		mainWindow = mainWindowToUse;
		setLayout(new BorderLayout());
		createPropertiesPanels();
	}
	
	public void dispose()
	{
		super.dispose();
		indicatorPropertiesPanel.dispose();
		measurementPropertiesPanel.dispose();
		futureStatusPropertiesPanel.dispose();
	}
	
	private void createPropertiesPanels() throws Exception
	{
		indicatorPropertiesPanel = new IndicatorPropertiesPanel(getMainWindow());
		measurementPropertiesPanel = new MeasurementPropertiesPanel(getProject());
		futureStatusPropertiesPanel = new IndicatorFutureStatusSubPanel(getProject());
		blankPropertiesPanel = new BlankPropertiesPanel();
		
	}
	
	public String getPanelDescription()
	{
		return PANEL_DESCRIPTION;
	}

	public void setObjectRefs(ORef[] orefsToUse)
	{
		super.setObjectRefs(orefsToUse);
		DisposablePanelWithDescription panel = findPanel(orefsToUse);
		removeAll();
		add(panel, BorderLayout.CENTER);
	
		indicatorPropertiesPanel.setObjectRefs(orefsToUse);
		measurementPropertiesPanel.setObjectRefs(orefsToUse);
		futureStatusPropertiesPanel.setObjectRefs(orefsToUse);
		
		if(getTopLevelAncestor() != null)
			getTopLevelAncestor().validate();
		
		// NOTE: The following are an attempt to fix a reported problem 
		// where the screen was not fully repainted when switching objects
		// This code is duplicated in PlanningTreePropertiesPanel.java
		// and in TargetViabilityTreePropertiesPanel.java
		validate();
		repaint();
	}
	
	private DisposablePanelWithDescription findPanel(ORef[] orefsToUse)
	{
		if(orefsToUse.length == 0)
			return blankPropertiesPanel;
		
		ORef firstRef = orefsToUse[0];
		int objectType = firstRef.getObjectType();
		
		if (Indicator.getObjectType() == objectType)
			return indicatorPropertiesPanel;
		
		if (Measurement.getObjectType() == objectType)
			return measurementPropertiesPanel;
		
		if (Goal.is(objectType))
			return futureStatusPropertiesPanel;
		
		return blankPropertiesPanel;
	}
	
	private MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public static final String PANEL_DESCRIPTION = "Planning Properties Panel";
	
	private MainWindow mainWindow;
	
	private IndicatorPropertiesPanel indicatorPropertiesPanel;
	private BlankPropertiesPanel blankPropertiesPanel;
	private MeasurementPropertiesPanel measurementPropertiesPanel;
	private IndicatorFutureStatusSubPanel futureStatusPropertiesPanel; 
}
