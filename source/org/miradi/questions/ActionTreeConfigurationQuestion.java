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

import org.miradi.icons.IconManager;
import org.miradi.icons.ObjectiveIcon;
import org.miradi.main.EAM;

public class ActionTreeConfigurationQuestion extends StaticChoiceQuestion
{
	public ActionTreeConfigurationQuestion()
	{
		super(getStaticChoices());
	}

	private static ChoiceItem[] getStaticChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem(OBJECTIVES_CONTAIN_STRATEGIES_CODE, EAM.text("Objectives contain Strategies"), new ObjectiveIcon()),
			new ChoiceItem(STRATEGIES_CONTAIN_OBJECTIVES_CODE, EAM.text("Strategies contain Objectives"), IconManager.getStrategyIcon()),
		};
	}
	
	public static final String OBJECTIVES_CONTAIN_STRATEGIES_CODE = "";
	public static final String STRATEGIES_CONTAIN_OBJECTIVES_CODE = "StrategiesContainObjectives";
}
