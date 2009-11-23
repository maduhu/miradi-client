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
package org.miradi.views.diagram.doers;

import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.objects.ProgressReport;
import org.miradi.project.Project;
import org.miradi.views.diagram.CreateAnnotationDoer;

public class CreateProgressReportDoer extends CreateAnnotationDoer
{
	
	//FIXME urgent - this method needs to be done differently, it is fragile.
	@Override
	public BaseObject getSelectedParentFactor()
	{
		if (getPicker() == null)
			return null;
		
		ORefList selectionRefs = getPicker().getSelectedHierarchies()[0];
		return getProgressReportParent(getProject(), selectionRefs);
	}

	public static BaseObject getProgressReportParent(Project projectToUse, ORefList selectionRefs)
	{
		ORef progressReportRef = selectionRefs.getRefForType(ProgressReport.getObjectType());
		selectionRefs.remove(progressReportRef);
		if (selectionRefs.isEmpty())
			return null;
		
		ORef parentOfProgessReportRef = selectionRefs.getFirstElement();
		if (parentOfProgessReportRef.isInvalid())
			return null;
		
		return BaseObject.find(projectToUse, parentOfProgessReportRef);
	}
	
	@Override
	public String getAnnotationListTag()
	{
		return BaseObject.TAG_PROGRESS_REPORT_REFS;
	}

	@Override
	public int getAnnotationType()
	{
		return ProgressReport.getObjectType();
	}
}