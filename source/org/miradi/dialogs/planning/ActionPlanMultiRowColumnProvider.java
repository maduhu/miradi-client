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

package org.miradi.dialogs.planning;

import java.util.HashMap;

import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.questions.ActionTreeConfigurationQuestion;

public class ActionPlanMultiRowColumnProvider extends AbstractPlanningMultiRowColumnProvider
{
	public ActionPlanMultiRowColumnProvider(Project projectToUse)
	{
		super(projectToUse);
	}
	
	@Override
	public boolean doObjectivesContainStrategies() throws Exception
	{
		return getSubViewProvider().doObjectivesContainStrategies();
	}
	
	@Override
	protected String getConfigurationTag()
	{
		return ViewData.TAG_ACTION_TREE_CONFIGURATION_CHOICE;
	}
	
	@Override
	protected HashMap<String, AbstractPlanningTreeRowColumnProvider> createCodeToProviderMap()
	{
		HashMap<String, AbstractPlanningTreeRowColumnProvider> map = new HashMap<String, AbstractPlanningTreeRowColumnProvider>();
		map.put(ActionTreeConfigurationQuestion.OBJECTIVES_CONTAIN_STRATEGIES_CODE, new ActionPlanSubViewObjectiveBasedRowColumnProvider(getProject()));
		map.put(ActionTreeConfigurationQuestion.STRATEGIES_CONTAIN_OBJECTIVES_CODE, new ActionPlanSubViewStrategyBasedRowColumnProvider(getProject()));
		
		return map;
	}
}