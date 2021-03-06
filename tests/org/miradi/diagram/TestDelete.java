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
package org.miradi.diagram;

import org.miradi.commands.CommandSetObjectData;
import org.miradi.main.MiradiTestCase;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.project.ProjectForTesting;
import org.miradi.views.diagram.LinkCreator;

public class TestDelete extends MiradiTestCase
{
	public TestDelete(String name)
	{
		super(name);
	}
	
	public void testBasics() throws Exception
	{
		ProjectForTesting project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		DiagramModel model = project.getTestingDiagramModel();
		
		DiagramFactor intervention = project.createAndAddFactorToDiagram(ObjectType.STRATEGY);
		
		DiagramFactor cause = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		LinkCreator linkCreator = new LinkCreator(project);
		DiagramLink diagramLink = linkCreator.createFactorLinkAndAddToDiagramUsingCommands(project.getTestingDiagramModel().getDiagramObject(), intervention, cause);
		
		assertTrue("link not found?", model.areDiagramFactorsLinked(intervention.getRef(), cause.getRef()));

		DiagramObject diagramObject1 = project.getTestingDiagramObject();
		CommandSetObjectData removeLink = CommandSetObjectData.createRemoveIdCommand(diagramObject1, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLink.getDiagramLinkId());
		project.executeCommand(removeLink);
		
	
		DiagramObject diagramObject2 = project.getTestingDiagramObject();
		CommandSetObjectData removeFactor = CommandSetObjectData.createRemoveIdCommand(diagramObject2, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, cause.getDiagramFactorId());
		project.executeCommand(removeFactor);
		assertFalse("node not deleted?", model.doesDiagramFactorExist(cause.getRef()));
		
		project.close();
	}
}
