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
package org.miradi.dialogfields;

import javax.swing.JComponent;

import org.miradi.dialogs.base.ObjectListTable;
import org.miradi.dialogs.base.ObjectListTableModel;
import org.miradi.dialogs.base.ObjectReadOnlyListTableModel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;

public class ObjectReadonlyObjectListTableField extends ObjectDataInputField
{
	public ObjectReadonlyObjectListTableField(MainWindow mainWindowToUse, ORef refToUse, String listFieldTag, int listedType, String[] columnTags)
	{
		super(mainWindowToUse.getProject(), refToUse, listFieldTag);
		
		model = new ObjectReadOnlyListTableModel(mainWindowToUse.getProject(), refToUse, listFieldTag, listedType, columnTags);
	    table = new ObjectListTable(mainWindowToUse, model);
	    
		setDefaultFieldBorder();
		table.setForeground(EAM.READONLY_FOREGROUND_COLOR);
		table.setBackground(EAM.READONLY_BACKGROUND_COLOR);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		
		table.dispose();
	}

	@Override
	public String getText()
	{
		return null;
	}

	@Override
	public void setText(String newValue)
	{
		try
		{
			ORefList orefList = new ORefList(newValue);
			model.setRowObjectRefs(orefList);
			model.fireTableStructureChanged();	
			table.updateAutomaticRowHeights();
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}
	
	@Override
	public boolean allowEdits()
	{
		return false;
	}

	@Override
	public JComponent getComponent()
	{
		return table;
	}
	
	private ObjectListTableModel model;
	private ObjectListTable table;
}
