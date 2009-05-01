/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

This file is part of Miradi

Miradi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License version 3, 
as published by the Free Software Foundation.

Miradi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Miradi.  If not, see <http://www.gnu.org/licenses/>. 
*/ 
package org.miradi.views.planning;

import javax.swing.Icon;

import org.miradi.dialogs.planning.PlanningTreeManagementPanel;
import org.miradi.dialogs.planning.propertiesPanel.PlanningTreeMultiPropertiesPanel;
import org.miradi.dialogs.planning.upperPanel.MonitoringPlanningTreeTablePanel;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTable;
import org.miradi.dialogs.planning.upperPanel.PlanningTreeTablePanel;
import org.miradi.icons.IconManager;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;

class MonitoringPlanManagementPanel extends PlanningTreeManagementPanel
{
	public MonitoringPlanManagementPanel(MainWindow mainWindowToUse,
			PlanningTreeTablePanel planningTreeTablePanel,
			PlanningTreeMultiPropertiesPanel planningTreePropertiesPanel)
			throws Exception
	{
		super(mainWindowToUse, planningTreeTablePanel, planningTreePropertiesPanel);
	}
	
	@Override
	public String getPanelDescription()
	{
		return EAM.text("Tab|Monitoring Plan");
	}

	@Override
	public Icon getIcon()
	{
		return IconManager.getIndicatorIcon();
	}

	public static MonitoringPlanManagementPanel createMonitoringPlanPanel(MainWindow mainWindowToUse) throws Exception
	{
		PlanningTreeTablePanel monitoringPlanTreeTablePanel = MonitoringPlanningTreeTablePanel.createPlanningTreeTablePanel(mainWindowToUse);
		PlanningTreeTable treeAsObjectPicker = (PlanningTreeTable)monitoringPlanTreeTablePanel.getTree();
		PlanningTreeMultiPropertiesPanel monitoringPlanPropertiesPanel = new PlanningTreeMultiPropertiesPanel(mainWindowToUse, ORef.INVALID, treeAsObjectPicker);
		
		return new MonitoringPlanManagementPanel(mainWindowToUse, monitoringPlanTreeTablePanel, monitoringPlanPropertiesPanel);
	}

}