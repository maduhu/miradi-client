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

package org.miradi.views.targetviability.doers;

import org.miradi.actions.ActionExpandToIndicator;
import org.miradi.actions.ActionExpandToKeyEcologicalAttribute;
import org.miradi.actions.ActionExpandToMeasurement;
import org.miradi.actions.ActionExpandToTarget;
import org.miradi.views.umbrella.doers.AbstractPopDownMenuDoer;

public class ExpandToMenuDoer extends AbstractPopDownMenuDoer
{
	@Override
	protected Class[] getAllPossibleActionClasses()
	{
		return new Class[] {
				ActionExpandToTarget.class,
				ActionExpandToKeyEcologicalAttribute.class,
				ActionExpandToIndicator.class,
				ActionExpandToMeasurement.class,
			};
	}
}