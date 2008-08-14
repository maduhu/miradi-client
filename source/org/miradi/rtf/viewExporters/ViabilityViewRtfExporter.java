/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.rtf.viewExporters;

import org.miradi.dialogs.planning.upperPanel.TreeTableModelExporter;
import org.miradi.dialogs.viability.ViabilityTreeModel;
import org.miradi.dialogs.viability.nodes.ViabilityRoot;
import org.miradi.main.MainWindow;
import org.miradi.questions.ReportTemplateContentQuestion;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.CodeList;

public class ViabilityViewRtfExporter extends RtfViewExporter
{
	public ViabilityViewRtfExporter(MainWindow mainWindow)
	{
		super(mainWindow);
	}
	
	@Override
	public void ExportView(RtfWriter writer, CodeList reportTemplateContent) throws Exception
	{
		if (reportTemplateContent.contains(ReportTemplateContentQuestion.VIABILITY_VIEW_VIABILITY_TAB_CODE))
			exportTable(writer, new TreeTableModelExporter(getProject(), createModel()));
	}
	
	private ViabilityTreeModel createModel() throws Exception
	{
		return new ViabilityTreeModel(new ViabilityRoot(getProject()));
	}
}
