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

package org.miradi.xml.generic;

import org.miradi.objects.ProjectMetadata;

class ProjectSummarySchemaElement extends ObjectSchemaElement
{
	public ProjectSummarySchemaElement()
	{
		super("ProjectSummary");
		createTextField(ProjectMetadata.TAG_PROJECT_NAME);
		createDateField(ProjectMetadata.TAG_DATA_EFFECTIVE_DATE);
		createTextField(ProjectMetadata.TAG_OTHER_ORG_PROJECT_NUMBER);
		createTextField(ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
		createTextField(ProjectMetadata.TAG_PROJECT_URL);
		createTextField(ProjectMetadata.TAG_PROJECT_DESCRIPTION);
		createTextField(ProjectMetadata.TAG_PROJECT_STATUS);
		createTextField(ProjectMetadata.TAG_NEXT_STEPS);
	}

}
