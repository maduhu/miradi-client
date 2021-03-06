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

package org.miradi.schemas;

import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.TncProjectData;
import org.miradi.questions.ProjectFocusQuestion;
import org.miradi.questions.ProjectScaleQuestion;
import org.miradi.questions.ProjectSharingQuestion;

public class TncProjectDataSchema extends BaseObjectSchema
{
	public TncProjectDataSchema()
	{
		super();
	}
	
	@Override
	protected void fillFieldSchemas()
	{
		super.fillFieldSchemas();
		
		createFieldSchemaChoice(TncProjectData.TAG_PROJECT_SHARING_CODE, ProjectSharingQuestion.class);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_CON_PRO_PARENT_CHILD_PROJECT_TEXT);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_PROJECT_RESOURCES_SCORECARD);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_PROJECT_LEVEL_COMMENTS);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_PROJECT_CITATIONS);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_CAP_STANDARDS_SCORECARD);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_MAKING_THE_CASE);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_RISKS);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_CAPACITY_AND_FUNDING);
		createFieldSchemaChoice(TncProjectData.TAG_PROJECT_FOCUS, ProjectFocusQuestion.class);
		createFieldSchemaChoice(TncProjectData.TAG_PROJECT_SCALE, ProjectScaleQuestion.class);
		createFieldSchemaMultiLineUserText(TncProjectData.TAG_FUNDRAISING_PLAN);
	}
	
	public static int getObjectType()
	{
		return ObjectType.TNC_PROJECT_DATA;
	}
	
	@Override
	public int getType()
	{
		return getObjectType();
	}
	
	@Override
	public String getXmpz2ElementName()
	{
		return TNC_PROJECT_DATA;
	}

	@Override
	public String getObjectName()
	{
		return OBJECT_NAME;
	}
	
	public static final String OBJECT_NAME = "TncProjectData";
}
