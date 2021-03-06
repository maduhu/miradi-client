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

package org.miradi.dialogs.base;

import java.util.Comparator;

import org.miradi.dialogs.threatrating.upperPanel.TableModelChoiceItemComparator;
import org.miradi.main.MiradiUiSchema;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.SortableTableModel;

abstract public class AbstractObjectTableModel extends SortableTableModel
{
	public AbstractObjectTableModel(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public ChoiceQuestion getColumnQuestion(int column)
	{
		return null;
	}
	
	public boolean isChoiceItemColumn(int column)
	{
		return getColumnQuestion(column) != null;
	}
	
	public boolean isCodeListColumn(int column)
	{
		return false;
	}
	
	public boolean isSingleLineTextCell(int row, int column)
	{
		ORef ref = getObjectRef(row);
		BaseObject object = BaseObject.find(getProject(), ref);
		String tag = getColumnTag(column);
		return MiradiUiSchema.isSingleLineTextCell(object, tag);
	}

	public boolean isMultiLineTextCell(int row, int column)
	{
		ORef ref = getObjectRef(row);
		BaseObject object = BaseObject.find(getProject(), ref);
		String tag = getColumnTag(column);
		return MiradiUiSchema.isMultiLineTextCell(object, tag);
	}

	protected ORef getObjectRef(int row)
	{
		return getRowObjectRefs().get(row);
	}
	
	public Project getProject()
	{
		return project;
	}
	
	@Override
	public void setSortedRefs(ORefList sortedRefs)
	{
		ORefList newList = new ORefList(sortedRefs);
		setRowObjectRefs(newList);
	}
		
	@Override
	public Comparator<ORef> createComparator(int sortColumn)
	{
		if (isChoiceItemColumn(sortColumn))
			return new TableModelChoiceItemComparator(this, sortColumn, getColumnQuestion(sortColumn));
		
		return super.createComparator(sortColumn);
	}
	
	public int getProportionShares(int row)
	{
		throw new RuntimeException("getProportionShares has not been implemented by ObjectTableModel");
	}
	
	public boolean areBudgetValuesAllocated(int row)
	{
		throw new RuntimeException("areBudgetValuesAllocated has not been implemented by ObjectTableModel");
	}
	
	public ORefList getObjectHierarchy(int row, int column)
	{
		throw new RuntimeException("Method is currently unused and has no implementation");
	}
	
	abstract public void setRowObjectRefs(ORefList objectRowRefs);
	
	abstract protected ORefList getRowObjectRefs();

	private Project project;
}
