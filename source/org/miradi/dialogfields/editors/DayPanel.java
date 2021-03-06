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

package org.miradi.dialogfields.editors;

import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.layout.TwoColumnPanel;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.utils.DateEditorComponent;
import org.miradi.utils.DateEditorComponentInsideTable;

public class DayPanel extends TwoColumnPanel
{
	public DayPanel(DateUnit dateUnit, String title)
	{
		add(new PanelTitleLabel(title));
		dateEditor = new DateEditorComponentInsideTable();
		add(dateEditor);
		
		setSelectedDateUnit(dateUnit);
	}
	
	@Override
	public void dispose()
	{
		dateEditor.dispose();
		dateEditor = null;
		
		super.dispose();
	}

	private void setSelectedDateUnit(DateUnit dateUnit)
	{
		if (dateUnit != null && dateUnit.isDay())
			dateEditor.setText(dateUnit.getDateUnitCode());
	}

	public DateUnit getDateUnit()
	{
		String dateString = dateEditor.getText();
		if(dateString.length() == 0)
			return null;
		return DateUnit.createDayDateUnit(dateString); 
	}

	public void setDateUnit(DateUnit dateUnitToUse)
	{
		setSelectedDateUnit(dateUnitToUse);
	}

	private DateEditorComponent dateEditor;
}
