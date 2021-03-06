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
package org.miradi.rtf.viewExporters;

import org.miradi.dialogs.audience.AudienceEditablePoolTableModel;
import org.miradi.dialogs.base.EditableObjectPoolRefsTableModel;
import org.miradi.dialogs.iucnRedlistSpecies.IucnRedlistSpeciesEditablePoolTableModel;
import org.miradi.dialogs.organization.OrganizationPoolTableModel;
import org.miradi.dialogs.otherNotableSpecies.OtherNotableSpeciesEditablePoolTableModel;
import org.miradi.dialogs.summary.TeamPoolTableModel;
import org.miradi.forms.PropertiesPanelSpec;
import org.miradi.forms.objects.MiradiShareTabForm;
import org.miradi.forms.summary.FosTabForm;
import org.miradi.forms.summary.LocationTabForm;
import org.miradi.forms.summary.PlanningTabForm;
import org.miradi.forms.summary.ProjectTabForm;
import org.miradi.forms.summary.RareTabForm;
import org.miradi.forms.summary.ScopeTabForm;
import org.miradi.forms.summary.TncTabForm;
import org.miradi.forms.summary.WcsTabForm;
import org.miradi.forms.summary.WwfTabForm;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.questions.ReportTemplateContentQuestion;
import org.miradi.rtf.RtfFormExporter;
import org.miradi.rtf.RtfWriter;
import org.miradi.schemas.FosProjectDataSchema;
import org.miradi.schemas.MiradiShareProjectDataSchema;
import org.miradi.schemas.RareProjectDataSchema;
import org.miradi.schemas.TncProjectDataSchema;
import org.miradi.schemas.WcpaProjectDataSchema;
import org.miradi.schemas.WcsProjectDataSchema;
import org.miradi.schemas.WwfProjectDataSchema;
import org.miradi.schemas.XenodataSchema;
import org.miradi.utils.CodeList;

public class SummaryViewRtfExporter extends RtfViewExporter
{
	public SummaryViewRtfExporter(MainWindow mainWindow)
	{
		super(mainWindow);
	}

	@Override
	public void exportView(RtfWriter writer, CodeList reportTemplateContent) throws Exception
	{
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_PROJECT_TAB_CODE))
			exportProjectTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_TEAM_TAB_CODE))
			exportTeamTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_ORGANIZATION_TAB_CODE))
			exportOrganizationTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_SCOPE_TAB_CODE))
			exportScopeTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_LOCATION_TAB_CODE))
			exportLocationTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_PLANNING_TAB_CODE))
			exportPlanningTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_TNC_TAB_CODE))
			exportTncTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_WWF_TAB_CODE))
			exportWwfTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_WCS_TAB_CODE))
			exportWcsTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_RARE_TAB_CODE))
			exportRareTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_FOS_TAB_CODE))
			exportFosTab(writer);
		
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.SUMMARY_VIEW_MIRADI_SHARE_TAB_CODE))
			exportMiradiShareTab(writer);
	}

	private void exportProjectTab(RtfWriter writer) throws Exception
	{
		createMetadataRtfFormExporter(writer).exportForm(new ProjectTabForm());
		writer.newParagraph();
	}

	private void exportTeamTab(RtfWriter writer) throws Exception
	{
		exportObjectTableModel(writer, new TeamPoolTableModel(getProject()), ReportTemplateContentQuestion.getTeamLabel());
	}
	
	private void exportOrganizationTab(RtfWriter writer) throws Exception
	{
		exportObjectTableModel(writer, new OrganizationPoolTableModel(getProject()), ReportTemplateContentQuestion.getOraganizationLabel());
	}	

	private void exportScopeTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new ScopeTabForm(), WcpaProjectDataSchema.getObjectType());
		exportModel(writer, new IucnRedlistSpeciesEditablePoolTableModel(getProject()), EAM.text("IUCN Redlist Species"));
		exportModel(writer, new OtherNotableSpeciesEditablePoolTableModel(getProject()), EAM.text("Other Notable Species"));
	}

	private void exportLocationTab(RtfWriter writer) throws Exception
	{
		createMetadataRtfFormExporter(writer).exportForm(new LocationTabForm());
		writer.newParagraph();
	}
	
	private void exportPlanningTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new PlanningTabForm(), new ORefList(getProjectMetadataRef()));
	}
	
	private void exportTncTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new TncTabForm(), TncProjectDataSchema.getObjectType());
	}

	private void exportWwfTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new WwfTabForm(), WwfProjectDataSchema.getObjectType());
	}

	private void exportWcsTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new WcsTabForm(), WcsProjectDataSchema.getObjectType());
	}
	
	private void exportRareTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new RareTabForm(), RareProjectDataSchema.getObjectType());
		exportModel(writer, new AudienceEditablePoolTableModel(getProject()), EAM.text("Audience"));
	}

	private void exportModel(RtfWriter writer, EditableObjectPoolRefsTableModel model, String reportLabel) throws Exception
	{
		//TODO  this call to setObjectRefs(empty List) causes extractOutEditableRefs to be called which loads the refs from the pool.  
		// Need to come up with a sort of a loadModel() that doesnt require an empty arg
		model.setObjectRefs(new ORefList());
		exportObjectTableModel(writer, model, reportLabel);
	}

	private void exportFosTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new FosTabForm(), FosProjectDataSchema.getObjectType());
	}
	
	private void exportMiradiShareTab(RtfWriter writer) throws Exception
	{
		exportForm(writer, new MiradiShareTabForm(), MiradiShareProjectDataSchema.getObjectType());
	}

	private void exportForm(RtfWriter writer, PropertiesPanelSpec form, int otherProjectDataType) throws Exception
	{
		ORefList singletonRefPlusMetadataRef = new ORefList(getProject().getSingletonObjectRef(otherProjectDataType));
		singletonRefPlusMetadataRef.add(getProjectMetadataRef());
		singletonRefPlusMetadataRef.add(getProject().getSafeSingleObjectRef(XenodataSchema.getObjectType()));
		exportForm(writer, form, singletonRefPlusMetadataRef);
	}
	
	private void exportForm(RtfWriter writer, PropertiesPanelSpec form, ORefList refsForForm) throws Exception
	{
		RtfFormExporter formExporter = new RtfFormExporter(getProject(), writer, refsForForm);
		formExporter.exportForm(form);
		writer.newParagraph();
	}
	
	private RtfFormExporter createMetadataRtfFormExporter(RtfWriter writer)
	{
		return new RtfFormExporter(getProject(), writer, getProjectMetadataRef());
	}
	
	private ORef getProjectMetadataRef()
	{
		return getProject().getMetadata().getRef();
	}
}
