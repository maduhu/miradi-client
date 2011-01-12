/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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
package org.miradi.dialogs.threatrating.upperPanel;

import java.util.Comparator;

import javax.swing.table.TableModel;

import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.BaseObject;

public abstract class AbstractTableModelComparator implements Comparator<Integer>
{
	public AbstractTableModelComparator(TableModel modelToUse, int columnToSort)
	{
		model = modelToUse;
		columnToSortBy = columnToSort;	
	}
	
	protected Object getValue(int row)
	{
		return model.getValueAt(row, columnToSortBy);
	}
	
	protected int compareDetails(Comparable[] sortValues1, Comparable[] sortValues2)
	{
		if (sortValues1.length != sortValues2.length)
			throw new RuntimeException("Arrays being compared have different lengths");
		
		for (int index = 0; index < sortValues1.length; ++index)
		{
			Comparable element1 = sortValues1[index];
			Comparable element2 = sortValues2[index];
			int compareToValue = compareValues(element1, element2);
			if (compareToValue != 0)
				return compareToValue;
		}
		
		EAM.logWarning("Arrays had identical elements");
		return 0;
	}

	abstract protected int compareValues(Comparable element1, Comparable element2);
	
	protected ORef getRefForRow(int row)
	{
		RowColumnBaseObjectProvider provider = ((RowColumnBaseObjectProvider)model);
		BaseObject baseObject = provider.getBaseObjectForRowColumn(row, 0);
		
		return baseObject.getRef();
	}
	
	abstract public int compare(Integer row1, Integer row2);

	protected TableModel model;
	protected int columnToSortBy;
}
