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

import org.miradi.main.EAM;

public class ProjectScaleQuestion extends StaticChoiceQuestion
{
	public ProjectScaleQuestion()
	{
		super(getStaticChoices());
	}

	private static ChoiceItem[] getStaticChoices()
	{
		return new ChoiceItem[] {
			new ChoiceItem("", EAM.text("Not Specified")),
			new ChoiceItem(GLOBAL_CODE, EAM.text("Global")),	
			new ChoiceItem(REGIONAL_CODE, EAM.text("Regional")),
			new ChoiceItem(LOCAL_CODE, EAM.text("Local")),
		};
	}
	
	private static final String GLOBAL_CODE = "Global";
	public static final String REGIONAL_CODE = "Regional";
	private static final String LOCAL_CODE = "Local";

}
