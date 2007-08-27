/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.planning;

import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ORefList;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Goal;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.TreeTableNode;

public class PlanningTreeGoalNode extends TreeTableNode
{
	public PlanningTreeGoalNode(Project projectToUse, Goal goalToUse)
	{
		project = projectToUse; 
		goal = goalToUse;
		rebuild();
	}

	public TreeTableNode getChild(int index)
	{
		return objectiveNodes[index];
	}

	public int getChildCount()
	{
		return objectiveNodes.length;
	}

	public BaseObject getObject()
	{
		return goal;
	}

	public ORef getObjectReference()
	{
		return goal.getRef();
	}

	public int getType()
	{
		return Goal.getObjectType();
	}

	public Object getValueAt(int column)
	{
		return null;
	}

	public void rebuild()
	{
		ORefList objectiveRefs = goal.getObjectivesUpstreamOfGoal();
		objectiveNodes = new TreeTableNode[objectiveRefs.size()];
		for (int i = 0; i < objectiveRefs.size(); ++i)
		{
			Objective objective = (Objective) project.findObject(objectiveRefs.get(i));
			objectiveNodes[i] = new PlanningTreeObjectiveNode(project, objective);
		}	
	}

	public String toString()
	{
		return goal.getLabel();
	}
	
	Project project;
	Goal goal;
	TreeTableNode[] objectiveNodes;
}
