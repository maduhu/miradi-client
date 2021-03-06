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
package org.miradi.questions;

import java.util.Vector;

import org.miradi.objects.DiagramObject;
import org.miradi.schemas.GroupBoxSchema;
import org.miradi.schemas.ScopeBoxSchema;
import org.miradi.schemas.StressSchema;
import org.miradi.schemas.TaskSchema;
import org.miradi.schemas.TextBoxSchema;

public class FilteredDiagramFactorsQuestion extends ObjectQuestion
{
	public FilteredDiagramFactorsQuestion(DiagramObject diagramObject)
	{
		super(diagramObject.getFactorsExcludingTypes(getTypesWithSpecialProperties()));
	}
	
	private static Vector<Integer> getTypesWithSpecialProperties()
	{
		Vector<Integer> typesToExclude = new Vector<Integer>();
		typesToExclude.add(TextBoxSchema.getObjectType());
		typesToExclude.add(GroupBoxSchema.getObjectType());
		typesToExclude.add(StressSchema.getObjectType());
		typesToExclude.add(TaskSchema.getObjectType());
		typesToExclude.add(ScopeBoxSchema.getObjectType());
		
		return typesToExclude;
	}
}
