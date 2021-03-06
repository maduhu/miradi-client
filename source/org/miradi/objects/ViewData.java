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

import java.text.ParseException;

import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.ids.BaseId;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.questions.DiagramModeQuestion;
import org.miradi.schemas.ViewDataSchema;
import org.miradi.utils.CodeList;

public class ViewData extends BaseObject
{
	public ViewData(ObjectManager objectManager, BaseId idToUse)
	{
		super(objectManager, idToUse, createSchema(objectManager));
	}

	public static ViewDataSchema createSchema(Project projectToUse)
	{
		return createSchema(projectToUse.getObjectManager());
	}

	public static ViewDataSchema createSchema(ObjectManager objectManager)
	{
		return (ViewDataSchema) objectManager.getSchemas().get(ObjectType.VIEW_DATA);
	}

	public Command[] buildCommandsToAddNode(ORef oRefToAdd) throws ParseException
	{
		if(getCurrentMode().equals(DiagramModeQuestion.MODE_DEFAULT))
			return new Command[0];
		
		CommandSetObjectData cmd = CommandSetObjectData.createAppendORefCommand(this, TAG_CHAIN_MODE_FACTOR_REFS, oRefToAdd);
		return new Command[] {cmd};
	}

	public Command[] buildCommandsToRemoveNode(ORef oRefToRemove) throws ParseException
	{
		if(getCurrentMode().equals(DiagramModeQuestion.MODE_DEFAULT))
			return new Command[0];
		
		ORefList currentORefs = new ORefList(getData(TAG_CHAIN_MODE_FACTOR_REFS));
		if(!currentORefs.contains(oRefToRemove))
			return new Command[0];
		
		CommandSetObjectData cmd = CommandSetObjectData.createRemoveORefCommand(this, TAG_CHAIN_MODE_FACTOR_REFS, oRefToRemove);
		return new Command[] {cmd};
	}

	public CommandSetObjectData createTabChangeCommand(int newTab) throws Exception
	{
		return new CommandSetObjectData(ObjectType.VIEW_DATA, getId(), ViewData.TAG_CURRENT_TAB, Integer.toString(newTab));
	}

	public String getTreeConfigurationChoice()
	{
		return getData(TAG_TREE_CONFIGURATION_REF);
	}
	
	public CodeList getBudgetRollupReportLevelTypes()
	{
		return getCodeListData(TAG_BUDGET_ROLLUP_REPORT_TYPES);
	}
	
	public ORef getCurrentConceptualModelRef()
	{
		return getRefData(TAG_CURRENT_CONCEPTUAL_MODEL_REF);
	}
	
	public ORef getCurrentResultsChainRef()
	{
		return getRefData(TAG_CURRENT_RESULTS_CHAIN_REF);
	}
	
	public ORef getTreeConfigurationRef()
	{
		return getRefData(TAG_TREE_CONFIGURATION_REF);
	}
	
	private PlanningTreeRowColumnProvider getTreeConfiguration()
	{
		return ObjectTreeTableConfiguration.find(getProject(), getTreeConfigurationRef());
	}
	
	public boolean shouldIncludeResultsChain() throws Exception
	{
		return getTreeConfiguration().shouldIncludeResultsChain();
	}

	public boolean shouldIncludeConceptualModelPage() throws Exception
	{
		return getTreeConfiguration().shouldIncludeConceptualModelPage();
	}
	
	public int getCurrentTab()
	{
		return getIntegerData(TAG_CURRENT_TAB);
	}

	public String getCurrentMode()
	{
		return getData(TAG_CURRENT_MODE);
	}

	@Override
	public int[] getTypesThatCanOwnUs()
	{
		return NO_OWNERS;
	}
	
	public static ViewData find(ObjectManager objectManager, ORef viewDataRef)
	{
		return (ViewData) objectManager.findObject(viewDataRef);
	}
	
	public static ViewData find(Project project, ORef viewDataRef)
	{
		return find(project.getObjectManager(), viewDataRef);
	}
	
	public static boolean is(ORef ref)
	{
		return is(ref.getObjectType());
	}

	public static boolean is(final int objectType)
	{
		return objectType == ViewDataSchema.getObjectType();
	}

	public static final String TAG_CURRENT_CONCEPTUAL_MODEL_REF = "CurrentConceptualModelRef";
	public static final String TAG_CURRENT_RESULTS_CHAIN_REF = "CurrentResultsChainRef";
	public static final String TAG_CURRENT_MODE = "CurrentMode";
	public static final String TAG_CHAIN_MODE_FACTOR_REFS = "ChainModeFactorRefs";
	public static final String TAG_CURRENT_TAB = "CurrentTab";
	public static final String TAG_DIAGRAM_HIDDEN_TYPES = "DiagramHiddenTypes";
	public static final String TAG_BUDGET_ROLLUP_REPORT_TYPES = "BudgetRollupReportTypes";
	
	public static final String TAG_PLANNING_SINGLE_LEVEL_CHOICE = "SingleLevelChoice";
	public static final String TAG_TREE_CONFIGURATION_REF = "CustomPlanRef";
	public static final String TAG_CURRENT_WIZARD_STEP = "CurrentWizardStep";
	public static final String TAG_ACTION_TREE_CONFIGURATION_CHOICE = "ActionTreeConfigurationChoice";
	public static final String TAG_MONITORING_TREE_CONFIGURATION_CHOICE = "MonitoringTreeConfigurationChoice";
}
