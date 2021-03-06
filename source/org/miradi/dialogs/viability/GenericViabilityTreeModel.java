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
package org.miradi.dialogs.viability;

import org.miradi.dialogs.treetables.GenericTreeTableModel;
import org.miradi.main.EAM;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Measurement;
import org.miradi.objects.Target;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.StatusQuestion;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.TargetSchema;

abstract public class GenericViabilityTreeModel extends GenericTreeTableModel
{
	public GenericViabilityTreeModel(Object root)
	{
		super(root);
		statusQuestion = new StatusQuestion();
	}

	public int getColumnCount()
	{
		return getColumnTags().length;
	}

	public String getColumnTag(int modelColumn)
	{
		return getColumnTags()[modelColumn];
	}

	public String getColumnName(int column)
	{
		String columnTag = getColumnTag(column);
		if(isChoiceItemColumn(columnTag))
			return getColumnChoiceItem(columnTag).getLabel();
		
		return EAM.fieldLabel(getObjectTypeForColumnLabel(columnTag), columnTag);
	}
	
	private int getObjectTypeForColumnLabel(String tag)
	{
		if(tag.equals(Target.TAG_VIABILITY_MODE))
			return TargetSchema.getObjectType();
			
		else if(tag.equals(Measurement.TAG_STATUS_CONFIDENCE))
			return MeasurementSchema.getObjectType();
			
		else if (tag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
			return IndicatorSchema.getObjectType();
		
		else if(tag.equals(Measurement.TAG_SUMMARY))
			return MeasurementSchema.getObjectType();
		
		return KeyEcologicalAttributeSchema.getObjectType();
	}
	
	public boolean isChoiceItemColumn(String columnTag)
	{
		ChoiceItem choiceItem = statusQuestion.findChoiceByCode(columnTag);
		return (choiceItem != null);
	}

	private ChoiceItem getColumnChoiceItem(String columnTag)
	{
		return statusQuestion.findChoiceByCode(columnTag);	
	}
	
	abstract public String[] getColumnTags();

	private StatusQuestion statusQuestion;
}
