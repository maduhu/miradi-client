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
package org.miradi.dialogs.planning.propertiesPanel;

import java.awt.Color;
import java.text.ParseException;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.AppPreferences;
import org.miradi.objecthelpers.DateUnit;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.TimePeriodCosts;
import org.miradi.objects.Assignment;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ResourceAssignment;
import org.miradi.project.Project;
import org.miradi.questions.CustomPlanningColumnsQuestion;
import org.miradi.utils.OptionalDouble;

public class WorkUnitsTableModel extends AbstractWorkUnitsTableModel
{
	public WorkUnitsTableModel(Project projectToUse, RowColumnBaseObjectProvider providerToUse, String treeModelIdentifierAsTagToUse) throws Exception
	{
		super(projectToUse, providerToUse, treeModelIdentifierAsTagToUse);
	}
	
	@Override
	public boolean isWorkUnitColumn(int column)
	{
		return true;
	}
	
	@Override
	protected boolean canEditMultipleAssignments(BaseObject baseObjectForRow, DateUnit dateUnit) throws Exception
	{
		ORefList assignmentRefs = baseObjectForRow.getRefList(getAssignmentsTag());
		if (!isHorizontallyEditable(assignmentRefs, dateUnit))
			return false;
			
		return isTotalUnitsEqualForAssignments(assignmentRefs, dateUnit);
	}

	private boolean isTotalUnitsEqualForAssignments(ORefList assignmentRefs, DateUnit dateUnit) throws Exception
	{
		OptionalDouble resourceTotal = new OptionalDouble();
		for (int index = 0; index < assignmentRefs.size(); ++index)
		{
			Assignment assignment = Assignment.findAssignment(getProject(), assignmentRefs.get(index));
			TimePeriodCosts timePeriodCosts = assignment.calculateTimePeriodCosts(dateUnit);
			OptionalDouble thisResourceTotal = timePeriodCosts.calculateResourcesTotalUnits();
			if (index == 0)
				resourceTotal = thisResourceTotal;
			
			if (!thisResourceTotal.equals(resourceTotal))
				return false;
			
			resourceTotal = thisResourceTotal;
		}
		
		return true;
	}
	
	@Override
	public Color getCellBackgroundColor(int column)
	{
		DateUnit dateUnit = getDateUnit(column);
		return AppPreferences.getWorkUnitsBackgroundColor(dateUnit);
	}
	
	@Override
	public String getColumnGroupCode(int modelColumn)
	{
		return CustomPlanningColumnsQuestion.META_RESOURCE_ASSIGNMENT_COLUMN_CODE;
	}
	
	@Override
	protected OptionalDouble calculateValue(TimePeriodCosts timePeriodCosts)
	{
		return timePeriodCosts.calculateResourcesTotalUnits();
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return getTreeModelIdentifierAsTag() + "." + UNIQUE_TABLE_MODEL_IDENTIFIER;
	}
	
	@Override
	protected boolean isAssignmentForModel(Assignment assignment)
	{
		return ResourceAssignment.is(assignment);
	}

	@Override
	protected boolean isEditableModel()
	{
		return true;
	}
	
	@Override
	protected String getAssignmentsTag()
	{
		 return BaseObject.TAG_RESOURCE_ASSIGNMENT_IDS;
	}
	
	@Override
	protected int getAssignmentType()
	{
		return ResourceAssignment.getObjectType();
	}
	
	@Override
	protected CommandSetObjectData createAppendAssignmentCommand(BaseObject baseObjectForRowColumn, ORef assignmentRef) throws ParseException
	{
		return CommandSetObjectData.createAppendIdCommand(baseObjectForRowColumn, getAssignmentsTag(), assignmentRef.getObjectId());
	}
	
	private static final String UNIQUE_TABLE_MODEL_IDENTIFIER = "WorkUnitsTableModel";
}
