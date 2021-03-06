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
package org.miradi.dialogs.planning.upperPanel;

import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.dialogs.treetables.TreeTableNode;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

abstract public class PlanningTreeTableModel extends GenericTreeTableModel
{	
	public PlanningTreeTableModel(Project projectToUse, TreeTableNode rootNode, PlanningTreeRowColumnProvider rowColumnProviderToUse) throws Exception
	{
		super(rootNode);
		
		project = projectToUse;
		rowColumnProvider = rowColumnProviderToUse;
	}
	
	public PlanningTreeRowColumnProvider getRowColumnProvider()
	{
		return rowColumnProvider;
	}

	public int getColumnCount()
	{
		return getColumnTags().size();
	}
	
	public String getColumnName(int column)
	{
		return EAM.fieldLabel(ObjectType.FAKE, getColumnTag(column));
	}
	
	public String getColumnTag(int modelColumn)
	{
		return getColumnTags().get(modelColumn);
	}
	
	public String getColumnTagForNode(int nodeType, int column)
	{
		return getColumnTag(column);
	}

	@Override
	protected void rebuildNode()
	{
		try
		{
			getRootNode().setVisibleRowCodes(getRowCodesToShow());
			super.rebuildNode();
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}
	
	//NOTE: PTTM is always just one column wide
	@Override
	public Object getValueAt(Object rawNode, int col)
	{
		throw new RuntimeException("TreeTables with more than one column are not supported");
	}

	public CodeList getColumnTags()
	{
		CodeList columnsToShow = new CodeList();
		columnsToShow.add(DEFAULT_COLUMN);
		return columnsToShow;
	}
	
	public CodeList getRowCodesToShow()
	{
		try
		{
			return rowColumnProvider.getRowCodesToShow();
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return new CodeList();
		}
	}
	
	protected Project getProject()
	{
		return project;
	}
	
	private Project project;
	private PlanningTreeRowColumnProvider rowColumnProvider;
}
