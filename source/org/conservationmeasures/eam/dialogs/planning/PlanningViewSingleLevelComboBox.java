/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning;

import org.conservationmeasures.eam.project.Project;

public class PlanningViewSingleLevelComboBox extends PlanningViewComboBox
{
	public PlanningViewSingleLevelComboBox(Project projectToUse)
	{
		super(projectToUse);
	}

	public String[] getColumnList() throws Exception
	{
		return null;
	}

	public String getPropertyName()
	{
		return null;
	}

	public String[] getRowList() throws Exception
	{
		return null;
	}
}
