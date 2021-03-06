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

package org.miradi.migrations.forward;

import java.util.HashMap;

import org.miradi.main.EAM;
import org.miradi.migrations.NewlyAddedFieldsMigration;
import org.miradi.migrations.RawProject;
import org.miradi.schemas.TncProjectDataSchema;

public class MigrationTo13 extends NewlyAddedFieldsMigration
{
	public MigrationTo13(RawProject rawProjectToUse)
	{
		super(rawProjectToUse, TncProjectDataSchema.getObjectType());
	}
	
	@Override
	protected HashMap<String, String> createFieldsToLabelMapToModify()
	{
		HashMap<String, String> fieldsToRemove = new HashMap<String, String>();
		fieldsToRemove.put(TAG_FUNDRAISING_PLAN, EAM.text("Fundraising Plan"));
		
		return fieldsToRemove;
	}

	@Override
	protected int getToVersion()
	{
		return VERSION_TO;
	}
	
	@Override
	protected int getFromVersion() 
	{
		return VERSION_FROM;
	}
	
	@Override
	protected String getDescription()
	{
		return EAM.text("This migration adds 1 new TNC field.");
	}
	
	public static final int VERSION_FROM = 12;
	public static final int VERSION_TO = 13;
	
	public final static String TAG_FUNDRAISING_PLAN = "FundraisingPlan";
}
