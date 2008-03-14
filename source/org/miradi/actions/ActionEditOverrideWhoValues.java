/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.miradi.actions;

import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

//FIXME remove this action and its doer
public class ActionEditOverrideWhoValues extends ObjectsAction
{
	public ActionEditOverrideWhoValues(MainWindow mainWindow)
	{
		super(mainWindow, getLabel());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Edit Who...");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Edit the list of overriden resources");
	}
}
