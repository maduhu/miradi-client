/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.diagram.cells;

import java.awt.Point;

import org.conservationmeasures.eam.ids.DiagramFactorId;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objects.DiagramFactor;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.project.ProjectForTesting;

public class TestFactorDataMap extends EAMTestCase
{
	public TestFactorDataMap(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void testFactorDataMap() throws Exception
	{
		FactorCell target = project.createFactorCell(Factor.TYPE_TARGET);
		DiagramFactor diagramFactor = target.getDiagramFactor();
		DiagramFactorId diagramFactorId = target.getDiagramFactorId();
		FactorId wrappedId = target.getWrappedId();
		
		String nodeAText = "Node A";
		diagramFactor.setLabel(nodeAText);
		Point location = new Point(5,22);
		diagramFactor.setLocation(location);
		String factorType = Factor.TYPE_TARGET.toString();
		String label = "Different from Node A";
		FactorDataMap nodeAData = diagramFactor.createFactorDataMap(factorType, label);
		
		assertEquals("location incorrect", location, nodeAData.getPoint(DiagramFactor.TAG_LOCATION));
		assertEquals("id incorrect", diagramFactorId, nodeAData.getId(DiagramFactor.TAG_ID));
		assertEquals("wrapped id incorrect", wrappedId, nodeAData.getId(DiagramFactor.TAG_WRAPPED_ID));
		assertEquals("node type incorrect", factorType, nodeAData.getFactorType());
		assertEquals("label incorrect", label, nodeAData.getLabel());
	}
	
	ProjectForTesting project;
}
