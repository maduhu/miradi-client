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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.FundingSource;
import org.miradi.project.ProjectForTesting;

public class TestFundingSource extends ObjectTestCase
{
	public TestFundingSource(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
	}

	@Override
	public void tearDown() throws Exception
	{
		project.close();
		project = null;
		super.tearDown();
	}
	
	public void testFields() throws Exception
	{
		verifyFields(ObjectType.FUNDING_SOURCE);
		verifyTagBehavior(FundingSource.TAG_LABEL);
		verifyTagBehavior(FundingSource.TAG_LABEL);
		verifyTagBehavior(FundingSource.TAG_COMMENTS);
	}

	private void verifyTagBehavior(String tag) throws Exception
	{
		String value = "ifislliefj";
		FundingSource fundingSource = new FundingSource(getObjectManager(), new BaseId(22));
		assertEquals(tag + " didn't default properly?", "", fundingSource.getData(tag));
		fundingSource.setData(tag, value);
	}
	
	ProjectForTesting project;
}
