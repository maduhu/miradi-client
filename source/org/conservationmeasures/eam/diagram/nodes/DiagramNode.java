/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.nodes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import org.conservationmeasures.eam.diagram.nodes.types.NodeType;
import org.conservationmeasures.eam.diagram.nodes.types.NodeTypeDirectThreat;
import org.conservationmeasures.eam.diagram.nodes.types.NodeTypeIndirectFactor;
import org.conservationmeasures.eam.diagram.nodes.types.NodeTypeIntervention;
import org.conservationmeasures.eam.diagram.nodes.types.NodeTypeStress;
import org.conservationmeasures.eam.diagram.nodes.types.NodeTypeTarget;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public class DiagramNode extends EAMGraphCell
{
	public DiagramNode(NodeType nodeType)
	{
		underlyingObject = new ConceptualModelObject(nodeType);
		
		port = new DefaultPort();
		add(port);
		setColors(getType());
		setFont();
		setLocation(new Point(0, 0));
		Dimension defaultNodeSize = new Dimension(120, 60);
		setSize(defaultNodeSize);
		setPreviousSize(defaultNodeSize);
		setText("");
	}
	
	public boolean isNode()
	{
		return true;
	}
	
	public NodeType getType()
	{
		return underlyingObject.getType();
	}

	public ThreatPriority getThreatPriority()
	{
		return underlyingObject.getThreatPriority();
	}
	
	public void setNodePriority(ThreatPriority priorityToUse)
	{
		underlyingObject.setNodePriority(priorityToUse);
	}
	
	public boolean canHavePriority()
	{
		return underlyingObject.canHavePriority();
	}
	
	public Indicator getIndicator()
	{
		return underlyingObject.getIndicator();
	}
	
	public void setIndicator(Indicator indicatorToUse)
	{
		underlyingObject.setIndicator(indicatorToUse);
	}

	public boolean canHaveObjectives()
	{
		return underlyingObject.canHaveObjectives();
	}

	public boolean canHaveGoal()
	{
		return getType().canHaveGoal();
	}

	public Goals getGoals()
	{
		return underlyingObject.getGoals();
	}

	public void setGoals(Goals goalsToUse)
	{
		underlyingObject.setGoals(goalsToUse);
	}

	public Objectives getObjectives()
	{
		return underlyingObject.getObjectives();
	}

	public void setObjectives(Objectives objectivesToUse)
	{
		underlyingObject.setObjectives(objectivesToUse);
	}
	
	public int getAnnotationRows()
	{
		int numberOfAnnotations = 0;
		numberOfAnnotations = getAnnotationCount(getObjectives()) + getAnnotationCount(getGoals());

		if(getIndicator() != null && getIndicator().hasIndicator() && numberOfAnnotations == 0)
			numberOfAnnotations = 1;

		return numberOfAnnotations;
	}

	private int getAnnotationCount(NodeAnnotations annotation)
	{
		int numberOfAnnotations = 0;
		if(annotation != null)
		{
			for(int i = 0; i < annotation.size(); ++i)
			{
				if(annotation.getAnnotation(i).hasAnnotation())
					++numberOfAnnotations;
			}
		}
		return numberOfAnnotations;
	}
	

	public boolean isTarget()
	{
		return(getType().isTarget());
	}
	
	public boolean isIndirectFactor()
	{
		return(getType().isIndirectFactor());
	}
	
	public boolean isIntervention()
	{
		return(getType().isIntervention());
	}
	
	public boolean isDirectThreat()
	{
		return(getType().isDirectThreat());
	}
	
	public boolean isStress()
	{
		return(getType().isStress());
	}
	
	public DefaultPort getPort()
	{
		return port;
	}
	
	private void setFont()
	{
//		Font font = originalFont.deriveFont(Font.BOLD);
//		GraphConstants.setFont(map, font);
	}

	private void setColors(NodeType cellType)
	{
		Color color = cellType.getColor();
		GraphConstants.setBorderColor(getAttributes(), Color.black);
		GraphConstants.setBackground(getAttributes(), color);
		GraphConstants.setForeground(getAttributes(), Color.black);
		GraphConstants.setOpaque(getAttributes(), true);
	}
	
	public Dimension getSize()
	{
		return getBounds().getBounds().getSize();
	}
	
	public void setSize(Dimension size)
	{
		Point location = getLocation();
		Rectangle bounds = new Rectangle(location, size);
		GraphConstants.setBounds(getAttributes(), bounds);
	}
	
	public Dimension getPreviousSize()
	{
		return previousSize;
	}
	
	public void setPreviousSize(Dimension size)
	{
		previousSize = size;
	}
	
	public Point getPreviousLocation()
	{
		return previousLocation;
	}

	public void setPreviousLocation(Point location)
	{
		previousLocation = location;
	}
	
	public Rectangle2D getBounds()
	{
		return GraphConstants.getBounds(getAttributes());
	}
	
	public boolean hasMoved()
	{
		return !getLocation().equals(getPreviousLocation());
	}
	
	public boolean sizeHasChanged()
	{
		return !getSize().equals(getPreviousSize());
	}
	
	public NodeDataMap createNodeDataMap()
	{
		NodeDataMap dataMap = super.createNodeDataMap();
		dataMap.putNodeType(getType());
		
		ThreatPriority priority = getThreatPriority();
		int priorityValue = ThreatPriority.PRIORITY_NOT_USED;
		if(priority != null)
			priorityValue = priority.getValue();
		dataMap.putInt(TAG_PRIORITY, priorityValue);
		return dataMap;
	}
	
	public static final int INVALID_ID = -1;
	
	public static final NodeType TYPE_INVALID = null;
	public static final NodeType TYPE_TARGET = new NodeTypeTarget();
	public static final NodeType TYPE_INDIRECT_FACTOR = new NodeTypeIndirectFactor();
	public static final NodeType TYPE_INTERVENTION = new NodeTypeIntervention();
	public static final NodeType TYPE_DIRECT_THREAT = new NodeTypeDirectThreat();
	public static final NodeType TYPE_STRESS = new NodeTypeStress();

	public static final int INT_TYPE_INVALID = -1;
	public static final int INT_TYPE_TARGET = 1;
	public static final int INT_TYPE_INDIRECT_FACTOR = 2;
	public static final int INT_TYPE_INTERVENTION = 3;
	public static final int INT_TYPE_DIRECT_THREAT = 4;
	public static final int INT_TYPE_STRESS = 5;

	public static final String TAG_NODE_TYPE = "NodeType";
	public static final String TAG_PRIORITY = "Priority";

	DefaultPort port;
	Dimension previousSize;
	Point previousLocation;
	
	ConceptualModelObject underlyingObject;
}

class ConceptualModelObject
{
	public ConceptualModelObject(NodeType nodeType)
	{
		type = nodeType;
		
		indicator = new Indicator();

		if(canHavePriority())
			setNodePriority(ThreatPriority.createPriorityNone());

		if(canHaveObjectives())
			objectives = new Objectives();

		if(canHaveGoal())
			goals = new Goals();
		
	}
	
	public NodeType getType()
	{
		return type;
	}

	public Indicator getIndicator()
	{
		return indicator;
	}
	
	public void setIndicator(Indicator indicatorToUse)
	{
		indicator = indicatorToUse;
	}

	public boolean canHavePriority()
	{
		return getType().canHavePriority();
	}
	
	public ThreatPriority getThreatPriority()
	{
		return threatPriority;
	}
	
	public void setNodePriority(ThreatPriority priorityToUse)
	{
		threatPriority = priorityToUse;
	}
	
	public boolean canHaveObjectives()
	{
		return getType().canHaveObjective();
	}

	public Objectives getObjectives()
	{
		return objectives;
	}

	public void setObjectives(Objectives objectivesToUse)
	{
		objectives = objectivesToUse;
	}
	
	public boolean canHaveGoal()
	{
		return getType().canHaveGoal();
	}

	public Goals getGoals()
	{
		return goals;
	}

	public void setGoals(Goals goalsToUse)
	{
		goals = goalsToUse;
	}

	private NodeType type;
	private Indicator indicator;
	private ThreatPriority threatPriority;
	private Objectives objectives;
	private Goals goals;
}
