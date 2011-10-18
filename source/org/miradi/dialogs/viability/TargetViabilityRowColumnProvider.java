/* 
Copyright 2005-2011, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.viability;

import org.miradi.dialogs.planning.AbstractPlanningTreeRowColumnProvider;
import org.miradi.objects.Indicator;
import org.miradi.objects.KeyEcologicalAttribute;
import org.miradi.objects.Measurement;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.Target;
import org.miradi.project.Project;
import org.miradi.utils.CodeList;

//FIXME urgent - Make new target viability tree table work
public class TargetViabilityRowColumnProvider extends AbstractPlanningTreeRowColumnProvider
{
	public TargetViabilityRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}

	public CodeList getRowCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				ProjectMetadata.OBJECT_NAME, 
				Target.OBJECT_NAME,
				KeyEcologicalAttribute.OBJECT_NAME,
				Indicator.OBJECT_NAME,
				Measurement.OBJECT_NAME,
		});
	}

	public CodeList getColumnCodesToShow() throws Exception
	{
		return new CodeList(new String[] {
				Indicator.TAG_LABEL,
		});
	}

	public boolean shouldIncludeResultsChain() throws Exception
	{
		return true;
	}

	public boolean shouldIncludeConceptualModelPage() throws Exception
	{
		return true;
	}

	@Override
	public boolean shouldPutTargetsAtTopLevelOfTree() throws Exception
	{
		return true;
	}
}
