/* 
* Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
* (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/
package org.miradi.actions;

import org.miradi.icons.MeasurementIcon;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class ActionCreateKeyEcologicalAttributeMeasurement extends ObjectsAction
{
	public ActionCreateKeyEcologicalAttributeMeasurement(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse, getLabel(), new MeasurementIcon());
	}

	private static String getLabel()
	{
		return EAM.text("Action|Manage|Create Measurement");
	}

	public String getToolTipText()
	{
		return EAM.text("TT|Create a new Measurement");
	}
}
