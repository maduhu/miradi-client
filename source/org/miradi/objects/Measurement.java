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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.StatusConfidenceQuestion;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.MeasurementSchema;

public class Measurement extends BaseObject
{
	public Measurement(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, createSchema(objectManager));
	}

	public static MeasurementSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static MeasurementSchema createSchema(ObjectManager objectManager)
	{
		return (MeasurementSchema) objectManager.getSchemas().get(ObjectType.MEASUREMENT);
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return new int[] {IndicatorSchema.getObjectType()};
	}
	
	public static boolean canReferToThisType(int type)
	{
		return false;
	}
	
	public boolean isSampleBased()
	{
		return getChoiceItemData(TAG_STATUS_CONFIDENCE).isSameCode(StatusConfidenceQuestion.SAMPLING_BASED);
	}
	
	public String getStatus()
	{
		return getData(TAG_STATUS);
	}
	
	public String getSummary()
	{
		return getStringData(TAG_SUMMARY);
	}
	
	@Override
	public String toString()
	{
		return getFullName();
	}
	
	@Override
	public String getFullName()
	{
		return getData(TAG_DATE) + ": " + getSummary();
	}
	
	public static boolean is(BaseObject object)
	{
		if(object == null)
			return false;
		return is(object.getRef());
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}
	
	public static boolean is(int objectType)
	{
		return objectType == MeasurementSchema.getObjectType();
	}
	
	public static Measurement find(ObjectManager objectManager, ORef measurementRef)
	{
		return (Measurement) objectManager.findObject(measurementRef);
	}
	
	public static Measurement find(Project project, ORef measurementRef)
	{
		return find(project.getObjectManager(), measurementRef);
	}
	
	public static final String TAG_TREND = "Trend";
	public static final String TAG_STATUS  = "Status";
	public static final String TAG_DATE = "Date";
	public static final String TAG_SUMMARY = "Summary";
	public static final String TAG_DETAIL = "Detail";
	public static final String TAG_STATUS_CONFIDENCE = "StatusConfidence";
	public static final String TAG_COMMENTS = "Comments";
	public static final String TAG_SAMPLE_SIZE ="SampleSize";
	public static final String TAG_SAMPLE_PRECISION ="SamplePrecision";
	public static final String TAG_SAMPLE_PRECISION_TYPE ="SamplePrecisionType";

	public static final String META_COLUMN_TAG = "MeasurementMetaColumnTag";
}
