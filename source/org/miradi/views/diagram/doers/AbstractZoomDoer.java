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

package org.miradi.views.diagram.doers;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.objects.DiagramObject;
import org.miradi.views.ViewDoer;

abstract public class AbstractZoomDoer extends ViewDoer
{
	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	protected void doIt() throws Exception
	{
		try
		{
			DiagramObject currentDiagramObject = getDiagramView().getCurrentDiagramObject();
			double newScale = getScale(currentDiagramObject.getZoomScale());
			CommandSetObjectData setZoom = new CommandSetObjectData(currentDiagramObject, DiagramObject.TAG_ZOOM_SCALE, Double.toString(newScale));
			getProject().executeCommand(setZoom);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
	
	abstract protected double getScale(double currentScale);
	
	public static double ZOOM_FACTOR = 1.2;
}
