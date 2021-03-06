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

import org.miradi.dialogs.planning.propertiesPanel.AbstractExpenseTableModel;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.project.Project;
import org.miradi.utils.OptionalDouble;

abstract public class CategorizedExpenseAmountsTableModel extends AbstractExpenseTableModel
{
	public CategorizedExpenseAmountsTableModel(Project projectToUse, PlanningTreeRowColumnProvider rowColumnProvider, RowColumnBaseObjectProvider providerToUse, String treeModelIdentifierAsTagToUse) throws Exception
	{
		super(projectToUse, rowColumnProvider, providerToUse, treeModelIdentifierAsTagToUse);
	}

	@Override
	protected OptionalDouble getOptionalDoubleAt(int row, int column)
	{
		return calculateRollupValue(row, column);
	}

	@Override
	protected void retainDataRelatedToAllOf(TimePeriodCosts timePeriodCosts, ORefSet objectHierarchy)
	{
		timePeriodCosts.retainExpenseDataRelatedToAllOf(objectHierarchy);
	}
}
