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
package org.miradi.dialogs.activity;

import org.miradi.actions.*;
import org.miradi.dialogs.base.ObjectListTablePanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.Strategy;
import org.miradi.schemas.StrategySchema;
import org.miradi.views.umbrella.StaticPicker;

public class ActivityListTablePanel extends ObjectListTablePanel
{
	public ActivityListTablePanel(MainWindow mainWindowToUse, ORefList selectedHierarchy)
	{
		super(mainWindowToUse, new ActivityListTableModel(mainWindowToUse.getProject(), selectedHierarchy), new StaticPicker(selectedHierarchy));
		
		addObjectActionButton(ActionCreateActivity.class, getParentPicker());
		addUnknownTypeOfButton(ActionDeleteActivity.class);
		addObjectActionButton(ActionMoveActivity.class, getParentPicker());
		addUnknownTypeOfButton(ActionActivityMoveUp.class);
		addUnknownTypeOfButton(ActionActivityMoveDown.class);
	}
	
	@Override
	public void handleCommandEventImmediately(CommandExecutedEvent event)
	{
		super.handleCommandEventImmediately(event);
		
		if (event.isSetDataCommandWithThisTypeAndTag(StrategySchema.getObjectType(), Strategy.TAG_ACTIVITY_IDS))
		{
			ORef selectedObjectRef = getSelectedObjectRef();
			getTable().getObjectTableModel().resetRows();
			getTable().setSelectedRow(selectedObjectRef);
		}
	}
}
