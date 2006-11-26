/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary;

import org.conservationmeasures.eam.dialogs.ObjectDataInputPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.project.Project;

public class TNCSummaryPanel extends ObjectDataInputPanel
{
	public TNCSummaryPanel(Project projectToUse, ProjectMetadata metadata)
	{
		super(projectToUse, metadata.getType(), metadata.getId());

		addField(createReadonlyTextField(metadata.TAG_TNC_WORKBOOK_VERSION_NUMBER));
		addField(createReadonlyTextField(metadata.TAG_TNC_WORKBOOK_VERSION_DATE));
		addField(createReadonlyTextField(metadata.TAG_TNC_DATABASE_DOWNLOAD_DATE));
		addField(createMultilineField(metadata.TAG_TNC_LESSONS_LEARNED));
		addField(createMultilineField(metadata.TAG_TNC_PLANNING_TEAM_COMMENT));

		updateFieldsFromProject();
	}
	
	public String getPanelDescription()
	{
		return EAM.text("TNC");
	}
	
}
