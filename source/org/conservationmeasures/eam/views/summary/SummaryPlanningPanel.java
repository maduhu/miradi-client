/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.summary;

import java.awt.Component;

import javax.swing.BorderFactory;

import org.conservationmeasures.eam.dialogs.base.AbstractObjectDataInputPanel;
import org.conservationmeasures.eam.layout.OneColumnGridLayout;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ORef;

public class SummaryPlanningPanel extends AbstractObjectDataInputPanel
{
	public SummaryPlanningPanel(MainWindow mainWindowToUse, ORef orefToUse)
	{
		super(mainWindowToUse.getProject(), orefToUse);
		setLayout(new OneColumnGridLayout());

		SummaryPlanningWorkPlanSubPanel workPlanSubPanel = new SummaryPlanningWorkPlanSubPanel(mainWindowToUse.getProject(), orefToUse);
		workPlanSubPanel.setBorder(BorderFactory.createTitledBorder(EAM.text("Workplan")));
		addSubPanel(workPlanSubPanel);
		add(workPlanSubPanel);
		
		SummaryFinancialPanel financialSubPanel = new SummaryFinancialPanel(mainWindowToUse);
		financialSubPanel.setBorder(BorderFactory.createTitledBorder(EAM.text("Financial")));
		addSubPanel(financialSubPanel);
		add(financialSubPanel);
		
		updateFieldsFromProject();
	}

	public String getPanelDescription()
	{
		return EAM.text("Planning");
	}

	public void addFieldComponent(Component component)
	{
	}
}
