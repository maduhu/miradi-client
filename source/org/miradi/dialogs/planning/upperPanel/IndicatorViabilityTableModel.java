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

import java.awt.Color;

import org.miradi.dialogs.planning.RowColumnProvider;
import org.miradi.dialogs.planning.propertiesPanel.PlanningViewAbstractTreeTableSyncedTableModel;
import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.objects.FutureStatus;
import org.miradi.objects.Measurement;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.EmptyChoiceItem;
import org.miradi.questions.StatusConfidenceQuestion;
import org.miradi.questions.TaglessChoiceItem;
import org.miradi.schemas.FutureStatusSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.utils.CodeList;


public class IndicatorViabilityTableModel extends PlanningViewAbstractTreeTableSyncedTableModel
{
	public IndicatorViabilityTableModel(Project projectToUse, RowColumnBaseObjectProvider adapterToUse, RowColumnProvider rowColumnProviderToUse) throws Exception
	{
		super(projectToUse, adapterToUse);
		
		rowColumnProvider = rowColumnProviderToUse;
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (Measurement.is(getBaseObjectForRow(row)))
		{
			if (isStatusConfidenceColumn(column))
				return true;

			if (isSummaryColumn(column))
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isFormattedColumn(int modelColumn)
	{
		if (isSummaryColumn(modelColumn))
			return true;
		
		return super.isFormattedColumn(modelColumn);
	}
	
	@Override
	public boolean isChoiceItemColumn(int column)
	{
		if(isStatusConfidenceColumn(column))
			return true;
		
		return super.isChoiceItemColumn(column);
	}

	@Override
	public Class getCellQuestion(int row, int modelColumn)
	{
		if (isStatusConfidenceColumn(modelColumn))
			return StatusConfidenceQuestion.class;
		
		return super.getCellQuestion(row, modelColumn);
	}
	
	private boolean isStatusConfidenceColumn(int column)
	{
		return getColumnTag(column).equals(Measurement.TAG_STATUS_CONFIDENCE);
	}
	
	private boolean isSummaryColumn(int column)
	{
		return getColumnTag(column).equals(Measurement.TAG_SUMMARY);
	}

	public int getColumnCount()
	{
		return getColumnTags().size();
	}
	
	public String getColumnTag(int modelColumn)
	{
		return getColumnTags().get(modelColumn);
	}
	
	@Override
	public String getColumnName(int column)
	{
		return EAM.fieldLabel(MeasurementSchema.getObjectType(), getColumnTag(column));
	}
	
	public Object getValueAt(int row, int column)
	{
		return getChoiceItemAt(row, column);
	}

	public ChoiceItem getChoiceItemAt(int row, int column)
	{
		String tag = getColumnTag(column);
		BaseObject baseObject = getBaseObjectForRow(row);
		if (Measurement.is(baseObject))
		{
			if (isChoiceItemColumn(column))
				return baseObject.getChoiceItemData(getColumnTag(column));

			return new TaglessChoiceItem(baseObject.getData(tag));
		}
		if (FutureStatus.is(baseObject) && isSummaryColumn(column))
		{
			return new TaglessChoiceItem(baseObject.getData(FutureStatusSchema.TAG_FUTURE_STATUS_SUMMARY));
		}

		return new EmptyChoiceItem();
	}
	
	@Override
	public void setValueAt(Object value, int row, int column)
	{
		BaseObject baseObject = getBaseObjectForRow(row);
		if (Measurement.is(baseObject))
		{
			if (isChoiceItemColumn(column))
				setChoiceValueUsingCommand(baseObject, getColumnTag(column), (ChoiceItem) value);
			else
				setValueUsingCommand(baseObject.getRef(), getColumnTag(column), value.toString());
		}
	}

	@Override
	public Color getCellBackgroundColor(int column)
	{
		return Color.WHITE;
	}
	
	public void updateColumnsToShow() throws Exception
	{
		fireTableStructureChanged();
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
	
	private CodeList getColumnTags()
	{
		try
		{
		return rowColumnProvider.getColumnCodesToShow();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			return new CodeList();
		}
	}
				
	private RowColumnProvider rowColumnProvider;
	private static final String UNIQUE_MODEL_IDENTIFIER = "TargetViabilityTableModel";
}
