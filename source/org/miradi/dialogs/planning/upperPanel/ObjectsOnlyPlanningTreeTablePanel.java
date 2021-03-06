/* 
Copyright 2005-2015, Foundations of Success, Bethesda, Maryland
on behalf of the Conservation Measures Partnership ("CMP").
Material developed between 2005-2013 is jointly copyright by Beneficent Technology, Inc. ("The Benetech Initiative"), Palo Alto, California.

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
package org.miradi.dialogs.planning.upperPanel;

import org.miradi.actions.ActionCreateCustomFromCurrentTreeTableConfiguration;
import org.miradi.actions.ActionDeletePlanningViewTreeNode;
import org.miradi.dialogs.planning.ObjectsOnlyRowColumnProvider;
import org.miradi.dialogs.planning.PlanningViewObjectsOnlyDropDownPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.ViewData;
import org.miradi.schemas.ViewDataSchema;

public class ObjectsOnlyPlanningTreeTablePanel extends PlanningTreeTablePanel
{
	protected ObjectsOnlyPlanningTreeTablePanel(MainWindow mainWindowToUse,
												PlanningTreeTable treeToUse, 
												PlanningTreeTableModel modelToUse, 
												PlanningTreeRowColumnProvider rowColumnProvider, Class[] buttonActions
												) throws Exception
	{
		super(mainWindowToUse, treeToUse, modelToUse, buttonActions, rowColumnProvider);
		
		customizationPanel = new PlanningViewObjectsOnlyDropDownPanel(getProject());
		addComponentAsFirst(customizationPanel);
	}
	
	public static PlanningTreeTablePanel createPlanningTreeTablePanel(MainWindow mainWindowToUse, PlanningTreeTableModel model) throws Exception
	{
		PlanningTreeTable treeTable = new PlanningTreeTable(mainWindowToUse, model);
		
		ObjectsOnlyRowColumnProvider rowColumnProvider = new ObjectsOnlyRowColumnProvider(mainWindowToUse.getProject());
		
		return new ObjectsOnlyPlanningTreeTablePanel(mainWindowToUse, treeTable, model, rowColumnProvider, getButtonActions());
	}
	
	@Override
	protected boolean doesCommandForceRebuild(CommandExecutedEvent event) throws Exception
	{
		if (super.doesCommandForceRebuild(event))
			return true;
			
		return isSingleLevelChoice(event);
	}
	
	public boolean isSingleLevelChoice(CommandExecutedEvent event)
	{
		return event.isSetDataCommandWithThisTypeAndTag(ViewDataSchema.getObjectType(), ViewData.TAG_PLANNING_SINGLE_LEVEL_CHOICE);
	}
		
	@Override
	public void dispose()
	{
		super.dispose();
		
		customizationPanel.dispose();
	}
	
	private static Class[] getButtonActions()
	{
		return new Class[] {
			ActionDeletePlanningViewTreeNode.class,				
			ActionCreateCustomFromCurrentTreeTableConfiguration.class,
		};
	}
	
	private PlanningViewObjectsOnlyDropDownPanel customizationPanel; 
}
