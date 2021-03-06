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

import java.awt.Dimension;

import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.objects.BaseObject;
import org.miradi.views.ObjectsDoer;
 
public abstract class AbstractEditListDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if(!isProjectOpen())
			return false;
		return (getSelectedObject() != null);
	}
	
	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		try
		{
			final DisposablePanel editPanel = createEditPanel();
			Dimension preferredSize = null;
			if (getDialogPreferredSize() != null)
				preferredSize = getDialogPreferredSize();
			
			showDialog(editPanel, preferredSize);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}

	private void showDialog(final DisposablePanel editPanel,	Dimension preferredSize)
	{
		if (shouldHaveScrollBars())
			editPanel.showModalDialogWithScrollBar(getMainWindow(), getDialogTitle(), preferredSize);
		else
			editPanel.showModalDialogWithoutScrollBar(getMainWindow(), getDialogTitle(), preferredSize);
	}

	protected Dimension getDialogPreferredSize()
	{
		return null;
	}
	
	protected BaseObject getSelectedObject()
	{
		BaseObject singleSelected = getSingleSelected(getObjectType());
		if (singleSelected == null)
			return null;
			
	
		return singleSelected;
	}

	abstract protected int getObjectType();
	
	abstract protected DisposablePanel createEditPanel() throws Exception;
	
	abstract protected String getDialogTitle();
	
	abstract protected boolean shouldHaveScrollBars();
}
