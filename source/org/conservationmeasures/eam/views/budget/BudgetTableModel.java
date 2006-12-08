/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.budget;

import javax.swing.table.AbstractTableModel;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.IdList;
import org.conservationmeasures.eam.ids.ProjectResourceId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.DateRangeEffortList;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Assignment;
import org.conservationmeasures.eam.objects.ProjectResource;
import org.conservationmeasures.eam.objects.Task;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.DateRange;
import org.conservationmeasures.eam.utils.DateRangeEffort;
import org.martus.util.MultiCalendar;

public class BudgetTableModel extends AbstractTableModel
{
	public BudgetTableModel(Project projectToUse, IdList assignmentIdListToUse) throws Exception
	{
		project = projectToUse;
		assignmentIdList  = assignmentIdListToUse;
		setProjectDateRanges();
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return true;
	}
	
	private void setProjectDateRanges() throws Exception
	{
		String startDate = project.getMetadata().getStartDate();
		projectStartDate = MultiCalendar.createFromGregorianYearMonthDay(2007, 1, 1);
		if (startDate.length() > 0 )
			projectStartDate = MultiCalendar.createFromIsoDateString(startDate);
		
		int year = projectStartDate.getGregorianYear();
		dateRanges = new DateRange[4];
		MultiCalendar start = MultiCalendar.createFromGregorianYearMonthDay(year, 1, 1);
		MultiCalendar end = null;
		for (int i = 0; i < dateRanges.length; i++)
		{
			int endMonth  =  (i + 1) * 3;
			end = MultiCalendar.createFromGregorianYearMonthDay(year, endMonth, 1);
			
			dateRanges[i] = new DateRange(start, end);
			start = MultiCalendar.createFromGregorianYearMonthDay(end.getGregorianYear(), end.getGregorianMonth(), end.getGregorianDay());
			start.addDays(-1);
		}
	}

	public void setTask(Task taskToUse)
	{
		task = taskToUse;
		dataWasChanged();
	}
	
	public void dataWasChanged()
	{
		assignmentIdList = task.getAssignmentIdList();
		fireTableDataChanged();
	}
	
	public int getColumnCount()
	{
		return dateRanges.length + EXTRA_COLUMN_COUNT;
	}

	public int getRowCount()
	{
		if (assignmentIdList != null)
			return assignmentIdList.size();
		
		return 0;
	}

	public String getColumnName(int col)
	{
		if (col == 0)
			return "Resources";
			
		return dateRanges[col - 1].getLabel();
	}
	
	public Object getValueAt(int row, int col)
	{
		if (col == 0)
			return getSelectedResource(row);

		return getUnitsFor(row, col - 1);
	}
	
	public String getUnitsFor(int row, int timeIndex)
	{
		double units = 0;
		try
		{
			DateRangeEffortList effortList = getDateRangeEffortList(row);
			units = effortList.get(timeIndex).getUnitQuantity();
		}
		catch (Exception e)
		{
			//FIXME budget code - uncomment when done with setting units
			//EAM.logException(e);
		}
		
		return Double.toString(units);
	}

	private DateRangeEffortList getDateRangeEffortList(int row) throws Exception
	{
		Assignment assignment = getAssignment(row);
		String dREffortListAsString = assignment.getData(Assignment.TAG_DATERANGE_EFFORTS);
		DateRangeEffortList dREffortList = new DateRangeEffortList(dREffortListAsString);
		return dREffortList;
	}
	
	public DateRangeEffort getDateRangeEffort(int row, int timeIndex)
	{
		DateRangeEffort dateRangeEffort = null;
		try
		{
			DateRangeEffortList effortList = getDateRangeEffortList(row);
			if (effortList.size() < timeIndex)
				return null;
			
			dateRangeEffort = effortList.get(timeIndex);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
		
		return dateRangeEffort;
	}
	
	public Assignment getAssignment(int row)
	{
		return (Assignment)project.findObject(ObjectType.ASSIGNMENT, getSelectedAssignment(row));
	}
	
	public BaseId getSelectedAssignment(int row)
	{
		return assignmentIdList.get(row);
	}
	
	public ProjectResource getSelectedResource(int row)
	{
		BaseId assignmentId = getSelectedAssignment(row);
		String stringId = project.getObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_ASSIGNMENT_RESOURCE_ID);
		BaseId resourceId = new BaseId(stringId);
		
		ProjectResource resource = (ProjectResource)project.findObject(ObjectType.PROJECT_RESOURCE, resourceId);
		
		return resource;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		if (value == null)
		{
			EAM.logDebug("value in setValueAt is null");
			return;
		}
		if (col == 0)
		{
			setResource(value, row);
			return;
		}
		if (col < 5)
			setUnits(value, row, col - 1);
	}

	private void setUnits(Object value, int row, int timeIndex)
	{
		try
		{
			Assignment assignment = getAssignment(row);
			DateRangeEffort dateRangeEffort = getDateRangeEffort(row, timeIndex);
			DateRangeEffortList dateRangeEffortList = getDateRangeEffortList(row);
			double units = Double.parseDouble(value.toString());
			
			//FIXME budget code - take out daterange
			if (dateRangeEffort == null)
				dateRangeEffort = new DateRangeEffort("", units, dateRanges[timeIndex]);
			
			dateRangeEffort.setUnitQuantity(units);
			dateRangeEffortList.setDateRangeEffort(dateRangeEffort);
			Command command = new CommandSetObjectData(assignment.getType(), assignment.getId(), assignment.TAG_DATERANGE_EFFORTS, dateRangeEffortList.toString());
			project.executeCommand(command);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	public void setResource(Object value, int row)
	{
		try
		{
			ProjectResource projectResource = (ProjectResource)value;
			ProjectResourceId resourceId = (ProjectResourceId)(projectResource).getId();
			
			BaseId  assignmentId = assignmentIdList.get(row);
			Command command = new CommandSetObjectData(ObjectType.ASSIGNMENT, assignmentId, Assignment.TAG_ASSIGNMENT_RESOURCE_ID, resourceId.toString());
			project.executeCommand(command);
		}
		catch(CommandFailedException e)
		{
			EAM.logException(e);
		}
		
	}
	
	Project project;
	MultiCalendar projectStartDate;
	MultiCalendar projectEndDate;
	DateRange[] dateRanges;
	IdList assignmentIdList;
	Task task;
	
	public static final int NUM_OF_RESOURCE_COLUMNS = 1;
	public static final int NUM_OF_TOTALS_COLUMNS = 0;
	public static final int EXTRA_COLUMN_COUNT = NUM_OF_RESOURCE_COLUMNS + NUM_OF_TOTALS_COLUMNS;	 
}
