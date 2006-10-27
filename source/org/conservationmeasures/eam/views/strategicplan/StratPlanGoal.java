/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.strategicplan;

import java.util.Iterator;
import java.util.Vector;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.ids.ObjectiveIds;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ConceptualModelNodeSet;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.ConceptualModelNode;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.project.ChainManager;
import org.conservationmeasures.eam.project.Project;

public class StratPlanGoal extends StratPlanObject
{
	public StratPlanGoal(Project projectToUse, Goal goalToUse)
	{
		project = projectToUse;
		if(goalToUse == null)
			EAM.logError("Attempted to create tree node for null goal");
		goal = goalToUse;
		rebuild();
		
	}
	
	public boolean canInsertActivityHere()
	{
		return false;
	}

	public Object getChild(int index)
	{
		return objectiveVector.get(index);
	}

	public int getChildCount()
	{
		return objectiveVector.size();
	}

	public BaseId getId()
	{
		return goal.getId();
	}

	public int getType()
	{
		return goal.getType();
	}

	public Object getValueAt(int column)
	{
		if(column == StrategicPlanTreeTableModel.labelColumn)
			return toString();
		return "";
	}

	public String toString()
	{
		return goal.getLabel();
	}

	public void rebuild()
	{
		objectiveVector = new Vector();
		ConceptualModelNodeSet relatedNodes = new ChainManager(project).findAllNodesRelatedToThisGoal(goal.getId());
		Iterator iter = relatedNodes.iterator();
		while(iter.hasNext())
		{
			ConceptualModelNode node = (ConceptualModelNode)iter.next();
			ObjectiveIds objectiveIds = node.getObjectives();
			for(int i = 0; i < objectiveIds.size(); ++i)
			{
				if(objectiveIds.get(i).isInvalid())
					continue;
				Objective objective = (Objective)project.findObject(ObjectType.OBJECTIVE, objectiveIds.get(i));
				objectiveVector.add(new StratPlanObjective(project, objective));
			}
		}
	}

	Project project;
	Goal goal;
	Vector objectiveVector;
}
