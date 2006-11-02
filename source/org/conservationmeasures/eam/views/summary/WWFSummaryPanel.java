/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary;

import org.conservationmeasures.eam.dialogfields.ObjectDataInputPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objects.ProjectMetadata;
import org.conservationmeasures.eam.project.Project;
import org.martus.swing.UiLabel;

public class WWFSummaryPanel extends ObjectDataInputPanel
{
	
	public WWFSummaryPanel(Project projectToUse, ProjectMetadata metadata)
	{
		super(projectToUse, metadata.getType(), metadata.getId());
		add(new UiLabel("Not implemented yet"));
	}

	public String getPanelDescription()
	{
		return EAM.text("Label|WWF");
	}

}
