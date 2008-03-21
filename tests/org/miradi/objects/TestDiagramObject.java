/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.objects;

import org.miradi.ids.BaseId;
import org.miradi.objects.Cause;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Target;
import org.miradi.project.ProjectForTesting;

public class TestDiagramObject extends ObjectTestCase
{

	public TestDiagramObject(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
	}

	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}

	public void testAreDiagramFactorsLinked() throws Exception
	{
		DiagramFactor cause = project.createDiagramFactorAndAddToDiagram(Cause.getObjectType());
		DiagramFactor target = project.createDiagramFactorAndAddToDiagram(Target.getObjectType());
		DiagramObject diagramObject = project.getDiagramObject();
		assertFalse("link does exist?", diagramObject.areDiagramFactorsLinked(cause.getDiagramFactorId(), target.getDiagramFactorId()));
		
		BaseId diagramLinkId = project.createDiagramFactorLink(cause, target);
		project.addDiagramLinkToModel(diagramLinkId);
		assertTrue("link does not exist?", diagramObject.areDiagramFactorsLinked(cause.getDiagramFactorId(), target.getDiagramFactorId()));
		assertTrue("link does not exist?", diagramObject.areDiagramFactorsLinked(target.getDiagramFactorId(), cause.getDiagramFactorId()));
		
		DiagramFactor nonLinkedCause = project.createDiagramFactorAndAddToDiagram(Cause.getObjectType());
		assertFalse("link does exist?", diagramObject.areDiagramFactorsLinked(nonLinkedCause.getDiagramFactorId(), target.getDiagramFactorId()));
		assertFalse("link does exist?", diagramObject.areDiagramFactorsLinked(target.getDiagramFactorId(), nonLinkedCause.getDiagramFactorId()));
	}
	
	ProjectForTesting project;
}
