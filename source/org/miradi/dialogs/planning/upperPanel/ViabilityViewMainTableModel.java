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

import javax.swing.Icon;

import org.miradi.dialogs.tablerenderers.RowColumnBaseObjectProvider;
import org.miradi.icons.IconManager;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.CodeToUserStringMap;
import org.miradi.objects.AbstractTarget;
import org.miradi.objects.BaseObject;
import org.miradi.objects.FutureStatus;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.PlanningTreeRowColumnProvider;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.EmptyChoiceItem;
import org.miradi.questions.KeyEcologicalAttributeTypeQuestion;
import org.miradi.questions.ProgressReportShortStatusQuestion;
import org.miradi.questions.RatingSourceQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.StatusConfidenceQuestion;
import org.miradi.questions.StatusQuestion;
import org.miradi.questions.TaglessChoiceItem;
import org.miradi.questions.TextAndIconChoiceItem;
import org.miradi.questions.TrendQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.schemas.FutureStatusSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.TargetSchema;

public class ViabilityViewMainTableModel extends PlanningViewMainTableModel
{
	public ViabilityViewMainTableModel(Project projectToUse, RowColumnBaseObjectProvider providerToUse, PlanningTreeRowColumnProvider rowColumnProviderToUse) throws Exception
	{
		super(projectToUse, providerToUse, rowColumnProviderToUse);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		BaseObject baseObject = getBaseObjectForRow(row);
		if (isAbstractTargetViabilityModeCell(row, column))
			return true;
		
		if (isAbstractTargetSimpleViabilityRatingCell(row, column))
			return true;

		if (isKeaAttributeTypeCell(row, column))
			return true;
		
		if (Indicator.is(baseObject))
			return isIndicatorRowEditable(row, column);
		
		if (Measurement.is(baseObject))
			return isMeasurementCellEditable(row, column);
		
		if (FutureStatus.is(baseObject))
			return isFutureStatusCellEditable(row, column);
		
		return false;
	}
	
	@Override
	public Class getCellQuestion(int row, int modelColumn)
	{
		if (isAbstractTargetViabilityModeCell(row, modelColumn))
			return ViabilityModeQuestion.class;

		if (isAbstractTargetSimpleViabilityRatingCell(row, modelColumn))
			return StatusQuestion.class;

		if (isKeaAttributeTypeCell(row, modelColumn))
			return KeyEcologicalAttributeTypeQuestion.class;

		if (isIndicatorRatingSourceColumn(row, modelColumn))
			return RatingSourceQuestion.class;
		
		if (isMeasurementStatusConfidenceColumn(row, modelColumn))
			return StatusConfidenceQuestion.class;
		
		return null;
	}

	private boolean isAbstractTargetSimpleViabilityRatingCell(int row, int column)
	{
		String tag = COLUMN_TAGS_FOR_TARGETS[column];
		if (!tag.equals(AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY))
			return false;
		
		BaseObject baseObject = getBaseObjectForRow(row);
		if (!AbstractTarget.isAbstractTarget(baseObject))
			return false;
		
		AbstractTarget abstractTarget = (AbstractTarget) baseObject;
		return abstractTarget.isSimpleMode();
	}

	private boolean isAbstractTargetViabilityModeCell(int row, int column)
	{
		String tag = COLUMN_TAGS_FOR_TARGETS[column];
		BaseObject baseObject = getBaseObjectForRow(row);
		if (AbstractTarget.isAbstractTarget(baseObject) && tag.equals(AbstractTarget.TAG_VIABILITY_MODE))
			return true;
		
		return false;
	}

	private boolean isKeaAttributeTypeCell(int row, int column)
	{
		String tag = COLUMN_TAGS_KEAS[column];
		BaseObject baseObject = getBaseObjectForRow(row);
		if (KeyEcologicalAttribute.is(baseObject) && tag.equals(KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE))
			return true;
		
		return false;
	}

	private boolean isFutureStatusCellEditable(int row, int column)
	{
		if (isFutureStatusThresholdCell(row, column))
			return true;
		
		return false;
	}

	public boolean isFutureStatusThresholdCell(int row, int column)
	{
		return FutureStatus.is(getBaseObjectForRow(row)) && isThresholdColumn(column);
	}

	private boolean isMeasurementCellEditable(int row, int column)
	{
		if (isMeasurementStatusConfidenceColumn(row, column))
			return true;
		
		if (isMeasurementThresholdCell(row, column))
			return true;
		
		return false;
	}

	public boolean isMeasurementThresholdCell(int row, int column)
	{
		return Measurement.is(getBaseObjectForRow(row)) && isThresholdColumn(column);
	}

	private boolean isIndicatorRowEditable(int row, int modelColumn)
	{
		if (isThresholdColumn(modelColumn))
			return true;
		
		if (isIndicatorRatingSourceColumn(row, modelColumn))
			return true;
		
		return false;
	}

	private boolean isIndicatorRatingSourceColumn(int row, int modelColumn)
	{
		final BaseObject baseObject = getBaseObjectForRow(row);
		if (!Indicator.is(baseObject))
			return false;
		
		String columnTag = COLUMN_TAGS_FOR_INDICATORS[modelColumn];
		if (!isViabilityIndicator((Indicator) baseObject))
			return false;
		
		return columnTag.equals(Indicator.TAG_RATING_SOURCE);
	}

	private boolean isMeasurementStatusConfidenceColumn(int row, int modelColumn)
	{
		String columnTag = COLUMN_TAGS_FOR_MEASUREMENTS[modelColumn];
		
		return Measurement.is(getBaseObjectForRow(row)) && columnTag.equals(Measurement.TAG_STATUS_CONFIDENCE);
	}

	private boolean isThresholdColumn(int modelColumn)
	{
		final String columnTag = getColumnTag(modelColumn);
		if (columnTag.equals(POOR))
			return true;
		
		if (columnTag.equals(FAIR))
			return true;
		
		if (columnTag.equals(GOOD))
			return true;
		
		if (columnTag.equals(VERY_GOOD))
			return true;
		
		return false;
	}
	
	@Override
	public void setValueAt(Object value, int row, int column)
	{
		if (ignoreSettingSameValue(value, row, column))
			return;

		BaseObject baseObject = getBaseObjectForRow(row);
		if (isAbstractTargetViabilityModeCell(row, column))
		{
			setChoiceValueUsingCommand(baseObject, AbstractTarget.TAG_VIABILITY_MODE, (ChoiceItem) value);
		}
		if (isAbstractTargetSimpleViabilityRatingCell(row, column))
		{
			setChoiceValueUsingCommand(baseObject, AbstractTarget.TAG_TARGET_STATUS, (ChoiceItem) value);
		}
		if (isKeaAttributeTypeCell(row, column))
		{
			setChoiceValueUsingCommand(baseObject, KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE, (ChoiceItem) value);
		}
		if (Indicator.is(baseObject))
		{
			setIndicatorValue(baseObject, value, row, column);
		}
		if (Measurement.is(baseObject))
		{
			setMeasurementValue(baseObject, value, row, column);
		}
		if (FutureStatus.is(baseObject))
		{
			setFutureStatusValue(baseObject, value, row, column);
		}
	}
	
	private boolean ignoreSettingSameValue(Object value, int row, int column)
	{
		return getValueAt(row, column).toString().equals(value.toString());
	}

	private void setMeasurementValue(BaseObject baseObject, Object value, int row, int column)
	{
		if (isMeasurementStatusConfidenceColumn(row, column))
		{
			setChoiceValueUsingCommand(baseObject, Measurement.TAG_STATUS_CONFIDENCE, (ChoiceItem) value);
		}
		
		if (isThresholdColumn(column))
		{
			setValueUsingCommand(baseObject.getRef(), Measurement.TAG_SUMMARY, value.toString());
			final String columnTag = COLUMN_TAGS_FOR_MEASUREMENTS[column];
			setValueUsingCommand(baseObject.getRef(), Measurement.TAG_STATUS, columnTag);
		}
	}
	
	private void setFutureStatusValue(BaseObject baseObject, Object value, int row, int column)
	{
		if (isThresholdColumn(column))
		{
			setValueUsingCommand(baseObject.getRef(), FutureStatusSchema.TAG_FUTURE_STATUS_SUMMARY, value.toString());
			
			final String columnTag = COLUMN_TAGS_FOR_FUTURE_RESULTS[column];
			setValueUsingCommand(baseObject.getRef(), FutureStatusSchema.TAG_FUTURE_STATUS_RATING, columnTag);
		}
	}
	
	private void setIndicatorValue(BaseObject baseObject, Object value, int row, int column)
	{
		if (isThresholdColumn(column))
		{
			int thresholdColumn = calculateRatingCodeFromColumn(column);
			Indicator indicator = (Indicator)baseObject;
			final CodeToUserStringMap map = indicator.getThresholdsMap().getCodeToUserStringMap();
			map.putUserString(Integer.toString(thresholdColumn), value.toString());
			setValueUsingCommand(baseObject.getRef(), Indicator.TAG_THRESHOLDS_MAP, map.toJsonString());
		}
		if (isIndicatorRatingSourceColumn(row, column))
		{
			setChoiceValueUsingCommand(baseObject, Indicator.TAG_RATING_SOURCE, (ChoiceItem) value);
		}
	}

	@Override
	public ChoiceItem getChoiceItemAt(int row, int column)
	{
		BaseObject baseObject = getBaseObjectForRow(row);
		if (ProjectMetadata.is(baseObject))
			return getValueForProject((ProjectMetadata) baseObject, row, column);
		
		if (AbstractTarget.isAbstractTarget(baseObject))
			return getValueForTarget(baseObject, row, column);
		
		if (KeyEcologicalAttribute.is(baseObject))
			return getValueForKea((KeyEcologicalAttribute) baseObject, row, column);
		
		if (Indicator.is(baseObject))
			return getValueForIndicator(baseObject, row, column);
		
		if (Measurement.is(baseObject))
			return getValueForMeasurement(baseObject, row, column);
		
		if (FutureStatus.is(baseObject))
			return getValueForFutureResultAsGoal(baseObject, row, column);
		
		return super.getChoiceItemAt(row, column);
	}
	
	private ChoiceItem getValueForProject(ProjectMetadata projectMetadata, int row, int column)
	{
		String tag = COLUMN_TAGS_FOR_PROJECT[column];
		if (tag.equals(AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY))
			return getStatusQuestion().findChoiceByCode(AbstractTarget.computeTNCViability(getProject()));
				
		return new EmptyChoiceItem();
	}

	private ChoiceItem getValueForKea(KeyEcologicalAttribute kea, int row, int column)
	{
		String tag = COLUMN_TAGS_KEAS[column];
		String rawValue = kea.getData(tag);
		if (tag.equals(KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS))
			return getStatusQuestion().findChoiceByCode(rawValue);
		
		if (tag.equals(KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE))
			return StaticQuestionManager.getQuestion(KeyEcologicalAttributeTypeQuestion.class).findChoiceByCode(rawValue);
		
		if(tag.equals(BaseObject.TAG_EMPTY))
			return new EmptyChoiceItem();
		
		return new TaglessChoiceItem(kea.getData(tag));
	}

	private ChoiceItem getValueForIndicator(BaseObject baseObject, int row,	int column)
	{
		String tag = COLUMN_TAGS_FOR_INDICATORS[column];
		Indicator indicator = (Indicator)baseObject;
		if (tag.equals(Indicator.PSEUDO_TAG_STATUS_VALUE))
			return getStatusQuestion().findChoiceByCode(baseObject.getPseudoData(tag));
		
		if (tag.equals(BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE))
			return StaticQuestionManager.getQuestion(ProgressReportShortStatusQuestion.class).findChoiceByCode(baseObject.getPseudoData(tag));
		
		if(tag.equals(BaseObject.TAG_EMPTY))
			return new EmptyChoiceItem();
		
		String data = baseObject.getData(tag);
		
		if(tag.equals(Indicator.TAG_RATING_SOURCE) && isViabilityIndicator(indicator))
			return StaticQuestionManager.getQuestion(RatingSourceQuestion.class).findChoiceByCode(data);
		
		if (tag.equals(Indicator.TAG_THRESHOLDS_MAP))
		{
			int thresholdColumn = calculateRatingCodeFromColumn(column);
			String key = Integer.toString(thresholdColumn);
			CodeToUserStringMap map = indicator.getThresholdsMap().getCodeToUserStringMap();
			String thresholdValue = map.getUserString(key);
			
			return new TaglessChoiceItem(thresholdValue);
		}
		
		return new TaglessChoiceItem(data);
	}

	private ChoiceItem getValueForTarget(BaseObject baseObject, int row, int column)
	{
		String tag = COLUMN_TAGS_FOR_TARGETS[column];
		String rawValue = baseObject.getData(tag);

		if(tag.equals(AbstractTarget.TAG_VIABILITY_MODE))
			return StaticQuestionManager.getQuestion(ViabilityModeQuestion.class).findChoiceByCode(rawValue);
		
		if (tag.equals(AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY))
			return getStatusQuestion().findChoiceByCode(rawValue);
		
		return new EmptyChoiceItem();
	}

	private ChoiceItem getValueForMeasurement(BaseObject baseObject, int row, int column)
	{
		String tag = COLUMN_TAGS_FOR_MEASUREMENTS[column];
		if (tag.equals(Measurement.TAG_STATUS_CONFIDENCE))
			return createStatusConfidenceChoiceItem(baseObject, tag);

		return getStatusColumnChoiceItem(tag, baseObject, Measurement.TAG_SUMMARY, Measurement.TAG_STATUS, getTrendIcon(baseObject));
	}

	private ChoiceItem getValueForFutureResultAsGoal(BaseObject baseObject, int row, int column)
	{
		String tag = COLUMN_TAGS_FOR_FUTURE_RESULTS[column];
		
		return getStatusColumnChoiceItem(tag, baseObject, FutureStatusSchema.TAG_FUTURE_STATUS_SUMMARY, FutureStatusSchema.TAG_FUTURE_STATUS_RATING, IconManager.getFutureStatusIcon());
	}

	private ChoiceItem getStatusColumnChoiceItem(String tag, BaseObject baseObject, final String summaryTag, final String statusTag, final Icon icon)
	{
		String summaryData = baseObject.getData(summaryTag);
		String statusData = baseObject.getData(statusTag);
		TextAndIconChoiceItem textAndIconChoiceItem = new TextAndIconChoiceItem(summaryData, icon);		
		if (isStatusColumn(tag, statusData, POOR))
			return textAndIconChoiceItem;
		
		if (isStatusColumn(tag, statusData, FAIR))
			return textAndIconChoiceItem;
		
		if (isStatusColumn(tag, statusData, GOOD))
			return textAndIconChoiceItem;
		
		if (isStatusColumn(tag, statusData, VERY_GOOD))
			return textAndIconChoiceItem;
		
		return new EmptyChoiceItem();
	}
	
	public ChoiceItem getStatusColumnChoiceItem(String tag, String statusData, String summaryData, final Icon icon)
	{
		TextAndIconChoiceItem textAndIconChoiceItem = new TextAndIconChoiceItem(summaryData, icon);		
		if (isStatusColumn(tag, statusData, POOR))
			return textAndIconChoiceItem;

		if (isStatusColumn(tag, statusData, FAIR))
			return textAndIconChoiceItem;

		if (isStatusColumn(tag, statusData, GOOD))
			return textAndIconChoiceItem;

		if (isStatusColumn(tag, statusData, VERY_GOOD))
			return textAndIconChoiceItem;
		
		return new EmptyChoiceItem();
	}

	private boolean isStatusColumn(String tag, String statusData, final String statusCode)
	{
		return tag.equals(statusCode) && statusCode.equals(statusData);
	}

	@Override
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
	
	@Override
	public boolean isChoiceItemColumn(int column)
	{
		String columnTag = getColumnTag(column);
		if(columnTag.equals(Measurement.TAG_STATUS_CONFIDENCE))
			return true;
		
		if(columnTag.equals(Indicator.TAG_RATING_SOURCE))
			return true;
		
		if(columnTag.equals(Measurement.TAG_STATUS))
			return true;

		if(columnTag.equals(AbstractTarget.TAG_VIABILITY_MODE))
			return true;

		if (isChoiceItemColumn(columnTag))
			return true;
		
		return super.isChoiceItemColumn(column);
	}
	
	private boolean isChoiceItemColumn(String columnTag)
	{
		ChoiceItem choiceItem = getStatusQuestion().findChoiceByCode(columnTag);
		return (choiceItem != null);
	}

	private ChoiceItem getColumnChoiceItem(String columnTag)
	{
		return getStatusQuestion().findChoiceByCode(columnTag);	
	}
	
	private ChoiceItem createStatusConfidenceChoiceItem(BaseObject baseObject, String tag)
	{
		String rawValue = baseObject.getData(tag);
		return StaticQuestionManager.getQuestion(StatusConfidenceQuestion.class).findChoiceByCode(rawValue);
	}

	private Icon getTrendIcon(BaseObject baseObject)
	{
		String trendData = baseObject.getData(Measurement.TAG_TREND);
		ChoiceQuestion trendQuestion = StaticQuestionManager.getQuestion(TrendQuestion.class);
		ChoiceItem findChoiceByCode = trendQuestion.findChoiceByCode(trendData);
		
		return findChoiceByCode.getIcon();
	}
	
	private int calculateRatingCodeFromColumn(int column)
	{
		int threasholdColumn = (1 + column) - getFirstColumnIndexOfThreshold();
		return threasholdColumn;
	}
	
	private int getFirstColumnIndexOfThreshold()
	{
		for (int columnIndex = 0; columnIndex < COLUMN_TAGS_FOR_INDICATORS.length; ++columnIndex)
		{
			if (COLUMN_TAGS_FOR_INDICATORS[columnIndex].equals(Indicator.TAG_THRESHOLDS_MAP))
				return columnIndex;
		}
		
		throw new RuntimeException("Could not find Threshold in array.");
	}
	
	private boolean isViabilityIndicator(Indicator indicator)
	{
		return indicator.isViabilityIndicator();
	}
	
	private ChoiceQuestion getStatusQuestion()
	{
		return StaticQuestionManager.getQuestion(StatusQuestion.class);
	}
	
	private static final String POOR = StatusQuestion.POOR;
	private static final String FAIR = StatusQuestion.FAIR;
	private static final String GOOD = StatusQuestion.GOOD;
	private static final String VERY_GOOD = StatusQuestion.VERY_GOOD;
	
	private static final String[] COLUMN_TAGS_FOR_PROJECT = {
		BaseObject.TAG_EMPTY, 
		AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		};
	
	private static final String[] COLUMN_TAGS_FOR_INDICATORS = {
		BaseObject.TAG_EMPTY,
		Indicator.PSEUDO_TAG_STATUS_VALUE,
		BaseObject.TAG_EMPTY,
		Indicator.TAG_THRESHOLDS_MAP,
		Indicator.TAG_THRESHOLDS_MAP,
		Indicator.TAG_THRESHOLDS_MAP,
		Indicator.TAG_THRESHOLDS_MAP,
		Indicator.TAG_RATING_SOURCE,
		BaseObject.PSEUDO_TAG_LATEST_PROGRESS_REPORT_CODE,
	};
	
	private static final String[] COLUMN_TAGS_FOR_TARGETS = {
		Target.TAG_VIABILITY_MODE, 
		AbstractTarget.PSEUDO_TAG_TARGET_VIABILITY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		};
	
	private static final String[] COLUMN_TAGS_KEAS = {
		BaseObject.TAG_EMPTY,
		KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS, 
		KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		};
	
	private static final String[] COLUMN_TAGS_FOR_MEASUREMENTS = { 
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		POOR,
		FAIR,
		GOOD,
		VERY_GOOD,
		Measurement.TAG_STATUS_CONFIDENCE,
		BaseObject.TAG_EMPTY,};
	
	private static final String[] COLUMN_TAGS_FOR_FUTURE_RESULTS = {
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		BaseObject.TAG_EMPTY,
		POOR,
		FAIR,
		GOOD,
	    VERY_GOOD,
	    BaseObject.TAG_EMPTY,
	    BaseObject.TAG_EMPTY,
	};
}
