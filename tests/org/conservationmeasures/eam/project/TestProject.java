/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.project;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.Vector;

import org.conservationmeasures.eam.annotations.GoalIds;
import org.conservationmeasures.eam.annotations.IndicatorId;
import org.conservationmeasures.eam.annotations.ObjectiveIds;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandDiagramMove;
import org.conservationmeasures.eam.commands.CommandInsertNode;
import org.conservationmeasures.eam.commands.CommandLinkNodes;
import org.conservationmeasures.eam.commands.CommandRedo;
import org.conservationmeasures.eam.commands.CommandSetFactorType;
import org.conservationmeasures.eam.commands.CommandSetIndicator;
import org.conservationmeasures.eam.commands.CommandSetNodeObjectives;
import org.conservationmeasures.eam.commands.CommandSetNodePriority;
import org.conservationmeasures.eam.commands.CommandSetNodeSize;
import org.conservationmeasures.eam.commands.CommandSetNodeText;
import org.conservationmeasures.eam.commands.CommandSetTargetGoal;
import org.conservationmeasures.eam.commands.CommandSwitchView;
import org.conservationmeasures.eam.commands.CommandUndo;
import org.conservationmeasures.eam.database.ProjectServer;
import org.conservationmeasures.eam.diagram.DiagramModel;
import org.conservationmeasures.eam.diagram.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.DiagramLinkage;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodetypes.NodeType;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeDirectThreat;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeIndirectFactor;
import org.conservationmeasures.eam.diagram.nodetypes.NodeTypeTarget;
import org.conservationmeasures.eam.exceptions.AlreadyInThatViewException;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.TransferableEamList;
import org.conservationmeasures.eam.main.ViewChangeListener;
import org.conservationmeasures.eam.objects.ConceptualModelLinkage;
import org.conservationmeasures.eam.objects.ThreatPriority;
import org.conservationmeasures.eam.testall.EAMTestCase;
import org.conservationmeasures.eam.views.NoProjectView;
import org.conservationmeasures.eam.views.diagram.DiagramView;
import org.conservationmeasures.eam.views.interview.InterviewView;
import org.martus.util.DirectoryUtils;

public class TestProject extends EAMTestCase
{
	public TestProject(String name)
	{
		super(name);
	}
	
	public void setUp() throws Exception
	{
		project = new ProjectForTesting(getName());
		idAssigner = project.getIdAssigner();
		super.setUp();
	}
	
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
	}
	
	public void testApplySnapToOldUnsnappedCommands() throws Exception
	{
		int snap = Project.DEFAULT_GRID_SIZE;
		
		Vector commands = new Vector();
		commands.add(new CommandDiagramMove(snap - 3, 2 * snap + 3, new int[] {0}));
		commands.add(new CommandDiagramMove(snap, 2 * snap, new int[] {0}));
		commands.add(new CommandInsertNode(DiagramNode.TYPE_DIRECT_THREAT));

		project.applySnapToOldUnsnappedCommands(commands);
		assertEquals("didn't snap first command?", commands.get(1), commands.get(0));
	}
	
	public void testGetSnapped() throws Exception
	{
		Point zeroZero = new Point(0, 0);
		assertEquals("moved zero zero?", zeroZero, project.getSnapped(zeroZero));
		
		Point defaultPlus = new Point(Project.DEFAULT_GRID_SIZE, Project.DEFAULT_GRID_SIZE);
		assertEquals("moved default plus?", defaultPlus, project.getSnapped(defaultPlus));
		
		Point defaultMinus = new Point(-Project.DEFAULT_GRID_SIZE, -Project.DEFAULT_GRID_SIZE);
		assertEquals("moved default minus?", defaultMinus, project.getSnapped(defaultMinus));

		Point oneThirdPlus = new Point(Project.DEFAULT_GRID_SIZE/3, Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap one third?", zeroZero, project.getSnapped(oneThirdPlus));
		
		Point oneThirdMinus = new Point(-Project.DEFAULT_GRID_SIZE/3, -Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap minus one third?", zeroZero, project.getSnapped(oneThirdMinus));
		
		Point twoThirdsPlus = new Point(2*Project.DEFAULT_GRID_SIZE/3, 2*Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap two thirds?", defaultPlus, project.getSnapped(twoThirdsPlus));
		
		Point twoThirdsMinus = new Point(-2*Project.DEFAULT_GRID_SIZE/3, -2*Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap minus two thirds?", defaultMinus, project.getSnapped(twoThirdsMinus));
		
		
	}
	
	public void testIsValidName() throws Exception
	{
		assertTrue("AlphaNumericDotDashSpace", Project.isValidProjectName("AZaz09.- "));
		assertFalse("Other Punct", Project.isValidProjectName("$"));
		final char ACCENT_A_LOWER = 0xE1;
		assertTrue("Foreign", Project.isValidProjectName(new String(new char[] {ACCENT_A_LOWER})));
	}
	
	public void testData() throws Exception
	{
		assertEquals("bad fieldname has data?", "", project.getDataValue("lisjefijef"));
		
		String fieldName = "sample field name";
		String fieldData = "sample field data";
		project.setDataValue(fieldName, fieldData);
		assertEquals("Didn't set data?", fieldData, project.getDataValue(fieldName));
		
		project.close();
		assertEquals("Didn't clear data?", "", project.getDataValue(fieldName));
		
	}
	
	public void testViewChanges() throws Exception
	{
		class SampleViewChangeListener implements ViewChangeListener
		{
			public void switchToView(String viewName)
			{
				if(viewName.equals(InterviewView.getViewName()))
					++interviewViewCount;
				else if(!viewName.equals(""))
					throw new RuntimeException("Unknown view: " + viewName);
			}

			int interviewViewCount;
		}
		
		SampleViewChangeListener listener = new SampleViewChangeListener();
		project.addViewChangeListener(listener);
		Command toInterview = new CommandSwitchView(InterviewView.getViewName());
		project.executeCommand(toInterview);
		assertEquals("didn't notify listener of view switch?", 1, listener.interviewViewCount);
		try
		{
			project.executeCommand(toInterview);
			fail("Can't switch to current view");
		}
		catch(AlreadyInThatViewException ignoreExpected)
		{
		}
	}

	public void testGetAllSelectedCellsWithLinkages() throws Exception
	{
		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 =  createNode(DiagramNode.TYPE_INTERVENTION);
		DiagramNode node3 =  createNode(DiagramNode.TYPE_INDIRECT_FACTOR);
		
		DiagramLinkage linkage1 = createLinkage(idAssigner.takeNextId(), node1.getId(), node2.getId());
		DiagramLinkage linkage2 = createLinkage(idAssigner.takeNextId(), node1.getId(), node3.getId());
		
		EAMGraphCell[] selectedCells = {linkage1};
		Vector selectedItems = project.getAllSelectedCellsWithLinkages(selectedCells);
		assertEquals(1, selectedItems.size());
		assertContains(linkage1, selectedItems);
		
		selectedCells[0] = node2;
		selectedItems = project.getAllSelectedCellsWithLinkages(selectedCells);
		assertEquals(2, selectedItems.size());
		assertContains(node2, selectedItems);
		assertContains(linkage1, selectedItems);
		
		selectedCells[0] = node1;
		selectedItems = project.getAllSelectedCellsWithLinkages(selectedCells);
		assertEquals(3, selectedItems.size());
		assertContains(node1, selectedItems);
		assertContains(linkage1, selectedItems);
		assertContains(linkage2, selectedItems);
	}

	public void testGetAllSelectedNodes() throws Exception
	{
		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 =  createNode(DiagramNode.TYPE_INTERVENTION);
		
		DiagramLinkage linkage1 = createLinkage(idAssigner.takeNextId(), node1.getId(), node2.getId());
		
		EAMGraphCell[] selectedCells = {linkage1};
		EAMGraphCell[] selectedItems = project.getOnlySelectedNodes(selectedCells);
		assertEquals(0, selectedItems.length);
		
		selectedCells[0] = node2;
		selectedItems = project.getOnlySelectedNodes(selectedCells);
		assertEquals(1, selectedItems.length);
		assertEquals(node2, selectedItems[0]);
		
		EAMGraphCell[] selectedCellsTwo = {node2,linkage1,node1};
		selectedItems = project.getOnlySelectedNodes(selectedCellsTwo);
		assertEquals(2, selectedItems.length);
	}
	
	public void TestPasteNodesAndLinksIntoProject() throws Exception
	{
		DiagramModel model = project.getDiagramModel();

		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 =  createNode(DiagramNode.TYPE_INTERVENTION);
		DiagramNode node3 =  createNode(DiagramNode.TYPE_INDIRECT_FACTOR);
		
		createLinkage(idAssigner.takeNextId(), node1.getId(), node2.getId());
		createLinkage(idAssigner.takeNextId(), node1.getId(), node3.getId());
		
		Vector cellVector = project.getAllSelectedCellsWithLinkages(new EAMGraphCell[]{node1});
		Object[] selectedCells = cellVector.toArray(new EAMGraphCell[0]);
		TransferableEamList transferableList = new TransferableEamList(selectedCells);
		assertEquals(3, model.getAllNodes().size());
		assertEquals(2, model.getLinkages(node1).size());
		assertEquals(1, model.getLinkages(node2).size());
		assertEquals(1, model.getLinkages(node3).size());
		
		project.pasteNodesAndLinksIntoProject(transferableList, new Point(5,5));
		Vector nodes = model.getAllNodes();
		assertEquals(4, nodes.size());
		assertEquals(4, model.getAllLinkages().size());
		for(int i = 0; i < nodes.size(); ++i)
		{
			assertEquals(2, model.getLinkages((DiagramNode)nodes.get(i)).size());
		}
		
		//Test when a pasted item has linkages to a previously deleted node
		model.deleteNode(node2);
		project.pasteNodesAndLinksIntoProject(transferableList, new Point(5,5));
		assertEquals(2, model.getLinkages(node1).size());
		assertEquals(3, model.getLinkages(node3).size());
	}

	public void testDiagramMoveOnly() throws Exception
	{
		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		node1.setPreviousLocation(new Point(0,0));
		node1.setLocation(new Point(0,0));
		node1.setPreviousSize(node1.getSize());

		int[] noNodesMoved = new int[1];
		noNodesMoved[0] = node1.getId();
	
		project.nodesWereMovedOrResized(0, 0, noNodesMoved);
		try
		{
			project.getLastCommand();
			fail("Should have thrown a null pointer since command should not have been recorded for a node which wasn't moved");
		}
		catch(Exception expected)
		{
		}
		
		
		int deltaX = 55;
		int deltaY = 88;
		node1.setLocation(new Point(deltaX,deltaY));
		
		DiagramNode node2 =  createNode(DiagramNode.TYPE_DIRECT_THREAT);
		node2.setPreviousLocation(new Point(10,10));
		node2.setLocation(new Point(20,30));
		
		int[] ids = new int[2];
		ids[0] = node1.getId();
		ids[1] = node2.getId();
		
		
		project.nodesWereMovedOrResized(deltaX, deltaY, ids);
		project.getLastCommand(); //End Transaction
		CommandDiagramMove commandDiagramMoveRecorded = (CommandDiagramMove)project.getLastCommand();
		assertEquals(deltaX, commandDiagramMoveRecorded.getDeltaX());
		assertEquals(deltaY, commandDiagramMoveRecorded.getDeltaY());
		assertEquals(node1.getId(), commandDiagramMoveRecorded.getIds()[0]);
		assertEquals(node2.getId(), commandDiagramMoveRecorded.getIds()[1]);
		project.getLastCommand(); //begin Transaction
		
	}
	
	public void testResizeNodesOnly() throws Exception
	{
		DiagramNode node1 =  createNode(DiagramNode.TYPE_INTERVENTION);
		node1.setSize(new Dimension(5,10));
		node1.setPreviousSize((new Dimension(55, 80)));
		node1.setPreviousLocation(new Point(0,0));
		node1.setLocation(new Point(0,0));

		DiagramNode node2 =  createNode(DiagramNode.TYPE_DIRECT_THREAT);
		node2.setSize(new Dimension(15,15));
		node2.setPreviousSize((new Dimension(52, 33)));
		node2.setPreviousLocation(new Point(0,0));
		node2.setLocation(new Point(0,0));
		
		int[] ids = new int[2];
		ids[0] = node1.getId();
		ids[1] = node2.getId();
		
		project.nodesWereMovedOrResized(0, 0, ids);
		project.getLastCommand(); //End Transaction
		CommandSetNodeSize commandSetNodeSize2Recorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(node2.getSize(), commandSetNodeSize2Recorded.getCurrentSize());
		assertEquals(node2.getPreviousSize(), commandSetNodeSize2Recorded.getPreviousSize());
		CommandSetNodeSize commandSetNodeSize1Recorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(node1.getSize(), commandSetNodeSize1Recorded.getCurrentSize());
		assertEquals(node1.getPreviousSize(), commandSetNodeSize1Recorded.getPreviousSize());
		project.getLastCommand(); //begin Transaction
	}

	public void testResizeAndMoveNodes() throws Exception
	{
		int x = 5;
		int y = 10;
		int deltaX = 20;
		int deltaY = 30;
		Dimension position1 = new Dimension(45, 65);
		Dimension position2 = new Dimension(95, 88);
		
		
		DiagramNode nodeResizedAndMoved =  createNode(DiagramNode.TYPE_INTERVENTION);
		nodeResizedAndMoved.setSize(position1);
		nodeResizedAndMoved.setPreviousSize(position2);
		nodeResizedAndMoved.setPreviousLocation(new Point(x,y));
		nodeResizedAndMoved.setLocation(new Point(x+deltaX, y+deltaY));

		DiagramNode nodeMovedOnly =  createNode(DiagramNode.TYPE_DIRECT_THREAT);
		nodeMovedOnly.setSize(position1);
		nodeMovedOnly.setPreviousSize(position1);
		nodeMovedOnly.setPreviousLocation(new Point(x,y));
		nodeMovedOnly.setLocation(new Point(x+deltaX, y+deltaY));
		
		DiagramNode nodeResizedOnly = createNode(DiagramNode.TYPE_DIRECT_THREAT);
		nodeResizedOnly.setSize(position1);
		nodeResizedOnly.setPreviousSize(position2);
		nodeResizedOnly.setPreviousLocation(new Point(x,y));
		nodeResizedOnly.setLocation(new Point(x,y));

		DiagramNode nodeNotMovedOrResized =  createNode(DiagramNode.TYPE_DIRECT_THREAT);
		nodeNotMovedOrResized.setSize(position1);
		nodeNotMovedOrResized.setPreviousSize(position1);
		nodeNotMovedOrResized.setPreviousLocation(new Point(x,y));
		nodeNotMovedOrResized.setLocation(new Point(x,y));

		
		int[] ids = new int[4];
		ids[0] = nodeResizedAndMoved.getId();
		ids[1] = nodeMovedOnly.getId();
		ids[2] = nodeResizedOnly.getId();
		ids[3] = nodeNotMovedOrResized.getId();

		project.nodesWereMovedOrResized(deltaX, deltaY, ids);
		
		project.getLastCommand(); //End Transaction
		
		CommandDiagramMove commandDiagramMoveRecorded = (CommandDiagramMove)project.getLastCommand();
		assertEquals(deltaX, commandDiagramMoveRecorded.getDeltaX());
		assertEquals(deltaY, commandDiagramMoveRecorded.getDeltaY());
		assertEquals(nodeResizedAndMoved.getId(), commandDiagramMoveRecorded.getIds()[0]);
		assertEquals(nodeMovedOnly.getId(), commandDiagramMoveRecorded.getIds()[1]);

		CommandSetNodeSize commandNodeResizedOnlyRecorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(nodeResizedOnly.getSize(), commandNodeResizedOnlyRecorded.getCurrentSize());
		assertEquals(nodeResizedOnly.getPreviousSize(), commandNodeResizedOnlyRecorded.getPreviousSize());
		
		CommandSetNodeSize commandNodeResizedAndMovedRecorded = (CommandSetNodeSize)project.getLastCommand();
		assertEquals(nodeResizedAndMoved.getSize(), commandNodeResizedAndMovedRecorded.getCurrentSize());
		assertEquals(nodeResizedAndMoved.getPreviousSize(), commandNodeResizedAndMovedRecorded.getPreviousSize());
		
		project.getLastCommand(); //begin Transaction
	}

	public void testPasteNodesOnlyIntoProject() throws Exception
	{
		DiagramModel model = project.getDiagramModel();

		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);
		DiagramNode node2 = createNode(DiagramNode.TYPE_INTERVENTION);
		DiagramNode node3 = createNode(DiagramNode.TYPE_INDIRECT_FACTOR);
		
		createLinkage(idAssigner.takeNextId(), node1.getId(), node2.getId());
		createLinkage(idAssigner.takeNextId(), node1.getId(), node3.getId());
		
		Vector cellVector = project.getAllSelectedCellsWithLinkages(new EAMGraphCell[]{node1});
		Object[] selectedCells = cellVector.toArray(new EAMGraphCell[0]);
		TransferableEamList transferableList = new TransferableEamList(selectedCells);
		assertEquals(3, model.getAllNodes().size());
		assertEquals(2, model.getLinkages(node1).size());
		assertEquals(1, model.getLinkages(node2).size());
		assertEquals(1, model.getLinkages(node3).size());
		
		project.pasteNodesOnlyIntoProject(transferableList, new Point(5,5));
		Vector nodes = model.getAllNodes();
		assertEquals(4, nodes.size());
		assertEquals(2, model.getAllLinkages().size());
	}
	
	public void testCutAndPaste() throws Exception
	{
		assertEquals("objects already in the pool?", 0, project.getNodePool().size());
		DiagramModel model = project.getDiagramModel();

		DiagramNode node1 = createNode(DiagramNode.TYPE_TARGET);

		Object[] selectedCells = new DiagramNode[] {node1};
		TransferableEamList transferableList = new TransferableEamList(selectedCells);
		
		project.deleteNode(node1.getId());
		assertEquals("objects still in the pool?", 0, project.getNodePool().size());

		Point pastePoint = new Point(5,5);
		project.pasteNodesAndLinksIntoProject(transferableList, pastePoint);
		Vector nodes = model.getAllNodes();
		assertEquals(1, nodes.size());
		DiagramNode pastedNode = (DiagramNode)nodes.get(0);
		assertEquals("didn't paste correct size?", node1.getSize(), pastedNode.getSize());
		assertNotEquals("didn't change id?", node1.getId(), pastedNode.getId());
		assertEquals("didn't snap?", project.getSnapped(pastePoint), pastedNode.getLocation());

		assertEquals("not just one object in the pool?", 1, project.getNodePool().size());
	}

	public void testCloseClearsCurrentView() throws Exception
	{
		assertEquals("not starting on diagram view?", DiagramView.getViewName(), project.getCurrentView());
		String sampleViewName = "blah blah";
		project.switchToView(sampleViewName);
		assertEquals("didn't switch?", sampleViewName, project.getCurrentView());
		project.close();
		assertEquals("didn't reset view?", NoProjectView.getViewName(), project.getCurrentView());
	}
	
	public void testExecuteCommandWritesDiagram() throws Exception
	{
		CommandInsertNode cmd = new CommandInsertNode(new NodeTypeTarget());
		project.executeCommand(cmd);
		DiagramModel copyOfModel = new DiagramModel(project.getNodePool());
		project.getDatabase().readDiagram(copyOfModel);
		assertEquals("didn't read back our one node?", 1, copyOfModel.getAllNodes().size());
	}
	
	public void testNodesGetWritten() throws Exception
	{
		ProjectServerForTesting database = project.getTestDatabase();
		assertEquals(0, database.callsToWriteNode);
		
		CommandInsertNode targetCommand = new CommandInsertNode(new NodeTypeTarget());
		project.executeCommand(targetCommand);
		assertEquals(1, database.callsToWriteNode);
		int targetId = targetCommand.getId();
		
		CommandInsertNode factorCommand = new CommandInsertNode(new NodeTypeIndirectFactor());
		project.executeCommand(factorCommand);
		assertEquals(2, database.callsToWriteNode);
		int factorId = factorCommand.getId();
		DiagramNode factor = project.getDiagramModel().getNodeById(factorId);
		
		project.executeCommand(new CommandDiagramMove(9, 9, new int[] {targetId, factorId} ));
		assertEquals(2, database.callsToWriteNode);
		
		project.executeCommand(new CommandSetFactorType(factorId, new NodeTypeDirectThreat()));
		assertEquals(3, database.callsToWriteNode);
		
		project.executeCommand(new CommandSetIndicator(factorId, new IndicatorId(7)));
		assertEquals(4, database.callsToWriteNode);
		
		ObjectiveIds objectives = new ObjectiveIds();
		objectives.addId(99);
		project.executeCommand(new CommandSetNodeObjectives(factorId, objectives));
		assertEquals(5, database.callsToWriteNode);
		
		project.executeCommand(new CommandSetNodePriority(factorId, ThreatPriority.createPriorityHigh()));
		assertEquals(6, database.callsToWriteNode);
		
		Dimension oldDimension = factor.getSize();
		project.executeCommand(new CommandSetNodeSize(factorId, new Dimension(50, 75), oldDimension));
		assertEquals(6, database.callsToWriteNode);
		
		project.executeCommand(new CommandSetNodeText(factorId, "hello"));
		assertEquals(6, database.callsToWriteNode);
		
		GoalIds goals = new GoalIds();
		goals.addId(55);
		project.executeCommand(new CommandSetTargetGoal(targetId, goals));
		assertEquals(7, database.callsToWriteNode);
		
		project.executeCommand(new CommandUndo());
		assertEquals(8, database.callsToWriteNode);
		
		project.executeCommand(new CommandRedo());
		assertEquals(9, database.callsToWriteNode);
	}
	
	public void testInsertDuplicateNodes() throws Exception
	{
		int id = 3023;
		int gotIdFirst = project.insertNodeAtId(DiagramNode.TYPE_DIRECT_THREAT, id);
		assertEquals("Didn't get our id?", id, gotIdFirst);
		try
		{
			project.insertNodeAtId(DiagramNode.TYPE_DIRECT_THREAT, id);
			fail("Should have thrown for inserting a duplicate id");
		}
		catch(RuntimeException ignoreExpected)
		{
		}
		
		CommandInsertNode cmd = new CommandInsertNode(DiagramNode.TYPE_DIRECT_THREAT);
		project.executeCommand(cmd);
		try
		{
			EAM.setLogToString();
			project.replayCommand(cmd);
			fail("Should have thrown for replaying an insert over an existing node");
		}
		catch(CommandFailedException ignoreExpected)
		{
		}
		finally
		{
			EAM.setLogToConsole();
		}
		
	}
	
	public void testLinkagePool() throws Exception
	{
		DiagramNode nodeA = createNode(new NodeTypeIndirectFactor());
		DiagramNode nodeB = createNode(new NodeTypeTarget());
		int linkageId = project.insertLinkageAtId(idAssigner.takeNextId(), nodeA.getId(), nodeB.getId());
		LinkagePool linkagePool = project.getLinkagePool();
		assertEquals("not in pool?", 1, linkagePool.size());
		ConceptualModelLinkage cmLinkage = linkagePool.find(linkageId);
		assertEquals("wrong from?", nodeA.getId(), cmLinkage.getFromNodeId());
		assertEquals("wrong to?", nodeB.getId(), cmLinkage.getToNodeId());
		
		project.deleteLinkage(linkageId);
		assertEquals("Didn't remove from pool?", 0, linkagePool.size());
	}
	
	public void testOpenProject() throws Exception
	{
		File tempDir = createTempDirectory();
		Project diskProject = new Project(new ProjectServer());
		diskProject.open(tempDir);
		try
		{
			
			CommandInsertNode cmdNode1 = new CommandInsertNode(new NodeTypeIndirectFactor());
			diskProject.executeCommand(cmdNode1);
			CommandInsertNode cmdNode2 = new CommandInsertNode(new NodeTypeTarget());
			diskProject.executeCommand(cmdNode2);
			CommandLinkNodes cmdLinkage = new CommandLinkNodes(cmdNode1.getId(), cmdNode2.getId());
			diskProject.executeCommand(cmdLinkage);
		}
		finally
		{
			diskProject.close();
		}
		
		Project loadedProject = new Project(new ProjectServer());
		loadedProject.open(tempDir);
		try
		{
			assertEquals("didn't read node pool?", 2, loadedProject.getNodePool().size());
			assertEquals("didn't read linkage pool?", 1, loadedProject.getLinkagePool().size());
			assertEquals("didn't populate diagram?", 2, loadedProject.getDiagramModel().getNodeCount());
		}
		finally
		{
			loadedProject.close();
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
	}
	
	private DiagramNode createNode(NodeType nodeType) throws Exception
	{
		int insertedId = project.insertNodeAtId(nodeType, IdAssigner.INVALID_ID);
		return project.getDiagramModel().getNodeById(insertedId);
	}
	
	private DiagramLinkage createLinkage(int id, int fromId, int toId) throws Exception
	{
		ConceptualModelLinkage cmLinkage = new ConceptualModelLinkage(id, fromId, toId);
		return project.getDiagramModel().createLinkage(cmLinkage);
	}



	ProjectForTesting project;
	IdAssigner idAssigner;
}
