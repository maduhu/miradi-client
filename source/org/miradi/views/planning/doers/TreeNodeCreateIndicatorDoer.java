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
package org.miradi.views.planning.doers;

import org.miradi.objects.BaseObject;
import org.miradi.objects.Factor;
import org.miradi.schemas.IndicatorSchema;

public class TreeNodeCreateIndicatorDoer extends AbstractTreeCreateAnnotationDoer
{
	@Override
	protected int getAnnotationType()
	{
		return IndicatorSchema.getObjectType();
	}
	
	@Override
	protected String getAnnotationTag()
	{
		return Factor.TAG_INDICATOR_IDS;
	}

	@Override
	protected boolean isCorrectOwner(BaseObject selectedObject)
	{
		if (!Factor.isFactor(selectedObject))
			return false;
		
		return ((Factor) selectedObject).canDirectlyOwnIndicators();
	}

	@Override
	protected String getObjectName()
	{
		return IndicatorSchema.OBJECT_NAME;
	}
}
