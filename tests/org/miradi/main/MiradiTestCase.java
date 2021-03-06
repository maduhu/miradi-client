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
package org.miradi.main;

import org.martus.util.TestCaseEnhanced;

public class MiradiTestCase extends TestCaseEnhanced
{
	public MiradiTestCase(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		verifySetUpAndTearDownCalledEqually(setUpLastCalledBy);
		++setUpTearDownCount;
		setUpLastCalledBy = getCurrentTestName();
	}

	@Override
	public void tearDown() throws Exception
	{
		--setUpTearDownCount;
		EAM.setLogToConsole();
		verifySetUpAndTearDownCalledEqually(getCurrentTestName());
		Miradi.restoreDefaultLocalization();
	}
	
	private String getCurrentTestName()
	{
		return getClass().toString() + "." + getName();
	}

	public static void verifySetUpAndTearDownCalledEqually(String currentTestName) 
	{
		int countWas = setUpTearDownCount;
		setUpTearDownCount = 0;
		if(countWas < 0)
			fail("EAMTestCase.setUp not called by subclass " + currentTestName);
		
		if(countWas > 0)
			fail("EAMTestCase.tearDown not called by subclass " + setUpLastCalledBy);
	}
	
	public static int setUpTearDownCount;
	public static String setUpLastCalledBy;
}
