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
package org.miradi.views.umbrella.doers;

import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Indicator;
import org.miradi.project.Project;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.views.ObjectsDoer;

public class CreateMethodDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if(getSelectedIndicator() == null)
			return false;
		
		return true;
	}

	@Override
	protected void doIt() throws Exception
	{
		if(!isAvailable())
			return;
		
		Indicator indicator = getSelectedIndicator();
		getProject().executeCommand(new CommandBeginTransaction());
		try
		{
			ORef createdRef = createAndAppendId(indicator, Indicator.TAG_METHOD_IDS, TaskSchema.getObjectType());
			getPicker().ensureOneCopyOfObjectSelectedAndVisible(createdRef);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
		finally
		{
			getProject().executeCommand(new CommandEndTransaction());
		}
		
	}

	private ORef createAndAppendId(BaseObject parent, String idsTag, int objectType) throws Exception
	{
		Project project = parent.getProject();
		CommandCreateObject create = new CommandCreateObject(objectType);
		project.executeCommand(create);
		BaseId createdId = create.getCreatedId();

		CommandSetObjectData addChild = CommandSetObjectData.createAppendIdCommand(parent, idsTag, createdId);
		project.executeCommand(addChild);
		return create.getObjectRef();
	}

	public Indicator getSelectedIndicator()
	{
		if(getSelectedHierarchies().length != 1)
			return null;
		
		ORef indicatorRef = getSelectedHierarchies()[0].getRefForType(IndicatorSchema.getObjectType());
		if(indicatorRef == null || indicatorRef.isInvalid())
			return null;
		
		return Indicator.find(getProject(), indicatorRef);
	}
}
