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
package org.miradi.views.summary;

import javax.swing.Icon;

import org.miradi.actions.jump.ActionJumpSummaryWizardDefineProjecScope;
import org.miradi.dialogs.base.ObjectDataInputPanelWithSections;
import org.miradi.dialogs.iucnRedlistSpecies.IucnRedlistSpeciesSubPanel;
import org.miradi.dialogs.otherNotableSpecies.OtherNotableSpeciesSubPanel;
import org.miradi.icons.ProjectScopeIcon;
import org.miradi.layout.OneColumnGridLayout;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;
import org.miradi.questions.ReportTemplateContentQuestion;
import org.miradi.rtf.RtfWriter;
import org.miradi.rtf.viewExporters.SummaryViewRtfExporter;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.utils.CodeList;

public class SummaryScopeTabPanel extends ObjectDataInputPanelWithSections
{
	public SummaryScopeTabPanel(Project projectToUse, ORef[] orefsToUse) throws Exception
	{
		super(projectToUse, ProjectMetadataSchema.getObjectType());
		setLayout(new OneColumnGridLayout());
		
		ScopeAndVisionPanel scopeVision = new ScopeAndVisionPanel(getProject(), getRefForType(ProjectMetadataSchema.getObjectType()));
		addSubPanelWithTitledBorder(scopeVision);

		BiodiversityPanel biodiversity = new BiodiversityPanel(getProject(), getRefForType(ProjectMetadataSchema.getObjectType()));
		addSubPanelWithTitledBorder(biodiversity);
		
		addSubPanelWithTitledBorder(new IucnRedlistSpeciesSubPanel(getProject()));
		addSubPanelWithTitledBorder(new OtherNotableSpeciesSubPanel(getProject()));
		
		HumanStakeholderPanel humans = new HumanStakeholderPanel(getProject(), getRefForType(ProjectMetadataSchema.getObjectType()));
		addSubPanelWithTitledBorder(humans);
		
		ProtectedAreaPanel protectedAreas = new ProtectedAreaPanel(getProject(), getSelectedRefs().toArray());
		addSubPanelWithTitledBorder(protectedAreas);

		setObjectRefs(orefsToUse);
		updateFieldsFromProject();
	}

	@Override
	public String getPanelDescription()
	{
		return getScopePanelDescription();
	}

	public static String getScopePanelDescription()
	{
		return EAM.text("Project|Scope");
	}
	
	@Override
	public Icon getIcon()
	{
		return new ProjectScopeIcon();
	}
	
	@Override
	public Class getJumpActionClass()
	{
		return ActionJumpSummaryWizardDefineProjecScope.class;
	}
	
	@Override
	public boolean isRtfExportable()
	{
		return true;
	}
	
	@Override
	public void exportRtf(RtfWriter writer) throws Exception
	{
		SummaryViewRtfExporter viewExporter = new SummaryViewRtfExporter(getMainWindow());
		CodeList summaryCodes = new CodeList();
		summaryCodes.add(ReportTemplateContentQuestion.SUMMARY_VIEW_SCOPE_TAB_CODE);
		viewExporter.exportView(writer, summaryCodes);
	}
}
