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
package org.miradi.dialogs.diagram;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.project.Project;

public class ConceptualModelPoolTableModel extends DiagramObjectPoolTableModel
{
	public ConceptualModelPoolTableModel(Project projectToUse, int listedItemType)
	{
		super(projectToUse, listedItemType);
	}

	@Override
	public String getValueToDisplay(ORef rowObjectRef, String tag)
	{
		String valueToDisplay = super.getValueToDisplay(rowObjectRef, tag);
		if (valueToDisplay.trim().length() != 0)
			return valueToDisplay;
		
		ConceptualModelDiagram conceptualModelDiagram = (ConceptualModelDiagram) getProject().findObject(rowObjectRef);
		ORefList diagramPageRefs = getProject().getConceptualModelDiagramPool().getORefList();
		if (conceptualModelDiagram.toString().trim().length() == 0 && diagramPageRefs.size() == 1)
			return ConceptualModelDiagram.DEFAULT_MAIN_NAME;
		
		return ConceptualModelDiagram.DEFAULT_BLANK_NAME;
	}
	
	@Override
	public String getUniqueTableModelIdentifier()
	{
		return UNIQUE_MODEL_IDENTIFIER;
	}
	
	private static final String UNIQUE_MODEL_IDENTIFIER = "ConceptualModelPoolTableModel";
}
