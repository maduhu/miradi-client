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

package org.miradi.xml.xmpz2.objectImporters;

import org.miradi.objecthelpers.ORef;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.TncProjectData;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.questions.StaticQuestionManager;
import org.miradi.questions.TncFreshwaterEcoRegionQuestion;
import org.miradi.questions.TncMarineEcoRegionQuestion;
import org.miradi.questions.TncOperatingUnitsQuestion;
import org.miradi.questions.TncTerrestrialEcoRegionQuestion;
import org.miradi.schemas.TncProjectDataSchema;
import org.miradi.xml.xmpz2.Xmpz2XmlImporter;
import org.w3c.dom.Node;

public class TncProjectDataImporter extends SingletonObjectImporter
{
	public TncProjectDataImporter(Xmpz2XmlImporter importerToUse)
	{
		super(importerToUse, new TncProjectDataSchema());
	}
	
	@Override
	protected boolean isCustomImportField(String tag)
	{
		if (isImportedElseWhere(tag))
			return true;
		
		return super.isCustomImportField(tag);
	}

	private boolean isImportedElseWhere(String tag)
	{
		return tag.equals(TncProjectData.TAG_PROJECT_SHARING_CODE);
	}
	
	@Override
	public void importFields(Node baseObjectNode, ORef refToUse) throws Exception
	{
		super.importFields(baseObjectNode, refToUse);
		
		importFields();
	}
	
	public void importFields() throws Exception
	{
		Node tncProjectDataNode = getImporter().getNamedChildNode(getImporter().getRootNode(), getXmpz2ElementName());
		importProjectMetadataField(tncProjectDataNode, ProjectMetadata.TAG_TNC_DATABASE_DOWNLOAD_DATE);
		importProjectMetadataField(tncProjectDataNode, ProjectMetadata.TAG_OTHER_ORG_RELATED_PROJECTS);
		importProjectMetadataField(tncProjectDataNode, ProjectMetadata.TAG_TNC_PLANNING_TEAM_COMMENTS);
		getImporter().importCodeListField(tncProjectDataNode, TNC_PROJECT_DATA, getMetadataRef(), ProjectMetadata.TAG_TNC_OPERATING_UNITS, getQuestion(TncOperatingUnitsQuestion.class));
		getImporter().importCodeListField(tncProjectDataNode, TNC_PROJECT_DATA, getMetadataRef(), ProjectMetadata.TAG_TNC_TERRESTRIAL_ECO_REGION, getQuestion(TncTerrestrialEcoRegionQuestion.class));
		getImporter().importCodeListField(tncProjectDataNode, TNC_PROJECT_DATA, getMetadataRef(), ProjectMetadata.TAG_TNC_MARINE_ECO_REGION, getQuestion(TncMarineEcoRegionQuestion.class));
		getImporter().importCodeListField(tncProjectDataNode, TNC_PROJECT_DATA, getMetadataRef(), ProjectMetadata.TAG_TNC_FRESHWATER_ECO_REGION, getQuestion(TncFreshwaterEcoRegionQuestion.class));
	}
	
	private ChoiceQuestion getQuestion(Class questionClass)
	{
		return StaticQuestionManager.getQuestion(questionClass);
	}

	private void importProjectMetadataField(Node projectSummaryNode, String tag) throws Exception
	{
		importField(projectSummaryNode, getBaseObjectSchema(), getMetadataRef(), tag);
	}
}
