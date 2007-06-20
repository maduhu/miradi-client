package org.conservationmeasures.eam.objects;

import org.conservationmeasures.eam.ids.DiagramFactorLinkId;
import org.conservationmeasures.eam.ids.FactorLinkId;
import org.conservationmeasures.eam.main.EAMTestCase;
import org.conservationmeasures.eam.objecthelpers.CreateFactorLinkParameter;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.project.ProjectForTesting;
import org.conservationmeasures.eam.utils.EnhancedJsonObject;

public class TestFactorLink extends EAMTestCase
{
	public TestFactorLink(String name) 
	{
		super(name);
	}

	public void testBasics() throws Exception
	{
		FactorLink linkageData = new FactorLink(id, nodeA.getWrappedId(), nodeB.getWrappedId());
		assertEquals("Id not the same?", id, linkageData.getId());
		assertEquals("From Node Ids don't match", nodeA.getWrappedId(), linkageData.getFromFactorId());
		assertEquals("To Node Ids don't match", nodeB.getWrappedId(), linkageData.getToFactorId());
	}

	public void setUp() throws Exception
	{
		super.setUp();
		project = new ProjectForTesting(getName());
		nodeA = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		nodeB = project.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
	}

	public void tearDown() throws Exception
	{
		project.close();
		super.tearDown();
	}

	public void testToJson() throws Exception
	{
		FactorLink original = new FactorLink(id, nodeA.getWrappedId(), nodeB.getWrappedId());
		original.setData(FactorLink.TAG_STRESS_LABEL, "What stress!");
		EnhancedJsonObject json = original.toJson();
		FactorLink gotBack = (FactorLink)BaseObject.createFromJson(project.getObjectManager(), original.getType(), json);
		assertEquals("wrong id?", original.getId(), gotBack.getId());
		assertEquals("wrong from?", original.getFromFactorId(), gotBack.getFromFactorId());
		assertEquals("wrong to?", original.getToFactorId(), gotBack.getToFactorId());
		assertEquals("wrong stress label?", original.getStressLabel(), gotBack.getStressLabel());
	}
	
	public void testStressLabel() throws Exception
	{
		String sampleStressLabel = "A Stress";
		FactorLink linkage = new FactorLink(id, nodeA.getWrappedId(), nodeB.getWrappedId());
		assertEquals("Didn't default to blank stress?", "", linkage.getStressLabel());
		linkage.setData(FactorLink.TAG_STRESS_LABEL, sampleStressLabel);
		assertEquals("Didn't set stress?", sampleStressLabel, linkage.getStressLabel());
		assertEquals("getData didn't work?", sampleStressLabel, linkage.getData(FactorLink.TAG_STRESS_LABEL));
		
	}
	
	public void testExtraInfo() throws Exception
	{
		DiagramFactorLinkId diagramLinkId = project.createDiagramFactorLink(nodeA, nodeB);
		DiagramLink diagramLink = (DiagramLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, diagramLinkId));
		FactorLink linkage = (FactorLink) project.findObject(new ORef(ObjectType.FACTOR_LINK, diagramLink.getWrappedId()));
		
		CreateFactorLinkParameter extraInfo = new CreateFactorLinkParameter(nodeA.getWrappedORef(), nodeB.getWrappedORef());
		CreateFactorLinkParameter gotExtraInfo = (CreateFactorLinkParameter)linkage.getCreationExtraInfo();
		assertEquals(extraInfo.getFromId(), gotExtraInfo.getFromId());
		assertEquals(extraInfo.getToId(), gotExtraInfo.getToId());
	}

	static final FactorLinkId id = new FactorLinkId(1);
	DiagramFactor nodeA;
	DiagramFactor nodeB;
	ProjectForTesting project;
}
