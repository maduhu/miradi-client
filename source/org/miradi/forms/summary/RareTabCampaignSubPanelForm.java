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
package org.miradi.forms.summary;

import org.miradi.forms.FieldPanelSpec;
import org.miradi.main.EAM;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.RareProjectData;
import org.miradi.questions.ThreatClassificationQuestion;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.schemas.RareProjectDataSchema;
import org.miradi.views.summary.RareCampaignSummarySubPanel;

public class RareTabCampaignSubPanelForm extends FieldPanelSpec
{
	public RareTabCampaignSubPanelForm()
	{		
		setTranslatedTitle(RareCampaignSummarySubPanel.PANEL_DESCRIPTION);
	
		addLabelAndField(RareProjectDataSchema.getObjectType(), RareProjectData.LEGACY_TAG_THREATS_ADDRESSED_NOTES);
		addCodeListField(ProjectMetadataSchema.getObjectType(), ProjectMetadata.PSEUDO_TAG_ALL_THREAT_CLASSIFICATIONS, new ThreatClassificationQuestion());
		addLeftRightConstants(THREATS_AT_SITE_CONSTANT, SEE_DIAGRAM_CONSTANT);
		addLabelAndField(RareProjectDataSchema.getObjectType(), RareProjectData.TAG_NUMBER_OF_COMMUNITIES_IN_CAMPAIGN_AREA);
		addLeftRightConstants(HUMAN_STAKEHOLDER_POP_SIZE_CONSTANT, SEE_SCOPE_TAB_CONSTANT);
		addLeftRightConstants(BIODIVERSITY_AREA_HA_CONSTANT, SEE_SCOPE_TAB_CONSTANT);
		addLeftRightConstants(HABITAT_CONSTANT, SEE_TNC_TAB_FOR_ECOREGIONS_CONSTANT);
		addLabelAndField(RareProjectDataSchema.getObjectType(), RareProjectData.TAG_BIODIVERSITY_HOTSPOTS);
		addLabelAndField(RareProjectDataSchema.getObjectType(), RareProjectData.TAG_FLAGSHIP_SPECIES_COMMON_NAME);
		addLabelAndField(RareProjectDataSchema.getObjectType(), RareProjectData.TAG_FLAGSHIP_SPECIES_SCIENTIFIC_NAME);
		addLabelAndField(RareProjectDataSchema.getObjectType(), RareProjectData.TAG_FLAGSHIP_SPECIES_DETAIL);
	}
	
	public static final String THREATS_AT_SITE_CONSTANT = EAM.text("Threats at Site");
	public static final String SEE_DIAGRAM_CONSTANT = EAM.text("(see Diagram)");
	public static final String HUMAN_STAKEHOLDER_POP_SIZE_CONSTANT = EAM.text("Human Stakeholder Pop Size");
	public static final String SEE_SCOPE_TAB_CONSTANT = EAM.text("(see Scope tab)");
	public static final String BIODIVERSITY_AREA_HA_CONSTANT = EAM.text("Biodiversity Area (ha)");
	public static final String HABITAT_CONSTANT = EAM.text("Habitat");
	public static final String SEE_TNC_TAB_FOR_ECOREGIONS_CONSTANT = EAM.text("(see TNC tab for Ecoregions)");
}
