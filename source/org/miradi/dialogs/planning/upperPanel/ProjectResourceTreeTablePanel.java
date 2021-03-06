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

import org.miradi.actions.ActionCreateResource;
import org.miradi.actions.ActionDeleteResource;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.schemas.ProjectResourceSchema;

public class ProjectResourceTreeTablePanel  extends PlanningTreeTablePanel
{
	protected ProjectResourceTreeTablePanel (MainWindow mainWindowToUse,
			PlanningTreeTable treeToUse, 
			PlanningTreeTableModel modelToUse,
			Class[] buttonClasses,
			PlanningTreeRowColumnProvider rowColumnProvider
	) throws Exception
	{
		super(mainWindowToUse, treeToUse, modelToUse, buttonClasses, rowColumnProvider);

	}

	public static PlanningTreeTablePanel createPlanningTreeTablePanel(MainWindow mainWindowToUse, PlanningTreeTableModel model, PlanningTreeRowColumnProvider rowColumnProvider) throws Exception
	{
		PlanningTreeTable treeTable = new PlanningTreeTable(mainWindowToUse, model);

		return new ProjectResourceTreeTablePanel (mainWindowToUse, treeTable, model, getButtonActions(), rowColumnProvider);
	}
	
	@Override
	protected boolean doesCommandForceRebuild(CommandExecutedEvent event) throws Exception
	{
		if (super.doesCommandForceRebuild(event))
			return true;
		
		return wasTypeCreatedOrDeleted(event, ProjectResourceSchema.getObjectType());
	}
	
	private static Class[] getButtonActions()
	{
		return new Class[] {
				ActionCreateResource.class, 
				ActionDeleteResource.class,
		};
	}
}
