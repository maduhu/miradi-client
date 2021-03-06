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
package org.miradi.project;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.util.HashSet;

import org.martus.util.DirectoryUtils;
import org.martus.util.UnicodeStringReader;
import org.martus.util.UnicodeStringWriter;
import org.miradi.commands.CommandBeginTransaction;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandEndTransaction;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.ChainWalker;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.diagram.DiagramPanel;
import org.miradi.exceptions.NothingToUndoException;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdAssigner;
import org.miradi.ids.IdList;
import org.miradi.ids.IndicatorId;
import org.miradi.main.AbstractTransferableMiradiList;
import org.miradi.main.MiradiTestCase;
import org.miradi.main.TransferableMiradiListVersion4;
import org.miradi.objectdata.BooleanData;
import org.miradi.objecthelpers.DirectThreatSet;
import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.FactorLinkPool;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.Factor;
import org.miradi.objects.FactorLink;
import org.miradi.objects.ProjectMetadata;
import org.miradi.objects.Strategy;
import org.miradi.objects.ViewData;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.ConceptualModelDiagramSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.ObjectiveSchema;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.MpfFileFilterWithDirectories;
import org.miradi.utils.NullProgressMeter;
import org.miradi.views.diagram.DiagramCopyPaster;
import org.miradi.views.diagram.DiagramPaster;
import org.miradi.views.diagram.DiagramView;
import org.miradi.views.diagram.LinkCreator;

public class TestProject extends MiradiTestCase
{
	public TestProject(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		idAssigner = project.getNormalIdAssigner();
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}
	
	public void testLastModified() throws Exception
	{
		Project newProject = new Project();
		long originalLastModifiedMillis = newProject.getLastModifiedTime();
		assertNotEquals("New project last modified time didn't get set?", 0, originalLastModifiedMillis);

		newProject.finishOpeningAfterLoad(getName());
		sleepToEnsureDifferentLastSavedTime();
		
		CommandSetObjectData command = new CommandSetObjectData(newProject.getMetadata(), ProjectMetadata.TAG_PROJECT_NAME, "Whatever");
		newProject.executeCommand(command);
		assertNotEquals("Last modified not updated?", originalLastModifiedMillis, newProject.getLastModifiedTime());
	}

	private void sleepToEnsureDifferentLastSavedTime() throws Exception
	{
		Thread.sleep(10);
	}
	
	public void testForOnlyOneAnnotationIdAssigner() throws Exception
	{
		IdAssigner original = project.getNormalIdAssigner();
		project.getProjectInfo().fillFrom(project.getProjectInfo().toJson());
		assertTrue("Constructed new annotation id assigner?", original == project.getNormalIdAssigner());
	}
	
	public void testEmptyTransaction() throws Exception
	{
		project.executeCommand(new CommandBeginTransaction());
		project.executeCommand(new CommandEndTransaction());
		try
		{
			project.undo();
			fail("Empty transaction should not be undoable");
		}
		catch(NothingToUndoException ignoreExpected)
		{
		}
	}
	
	public void testUndoRedoSaveInfoAndDiagram() throws Exception
	{
		project.createAndAddFactorToDiagram(ObjectType.CAUSE);
		
		//undo add diagramFactor
		project.undo();
		
		//undo create diagramFactor
		project.undo();
		
		assertEquals("node not removed?", 0, project.getTestingDiagramObject().getAllDiagramFactorIds().size());

		project.redo();
		
		project.redo();
		assertEquals("node not re-added?", 1, project.getTestingDiagramObject().getAllDiagramFactorIds().size());
	}
	
	public void testGetViewData() throws Exception
	{
		String viewName1 = DiagramView.getViewName();
		ViewData diagramData = project.getViewData(viewName1);
		assertEquals("gave back wrong data?", viewName1, diagramData.getLabel());
		ViewData repeat = project.getViewData(viewName1);
		assertEquals("didn't return same data?", diagramData.getId(), repeat.getId());
		
		assertNotNull("didn't create arbitrary data?", project.getViewData("iweflijjfliej"));
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
	
	public void testUndoRedoNestedTransactions() throws Exception
	{
		project.executeBeginTransaction();
		ORef diagramFactorRef1 = project.createAndAddFactorToDiagram(ObjectType.CAUSE).getRef();
		project.executeBeginTransaction();
		ORef diagramFactorRef2 = project.createAndAddFactorToDiagram(ObjectType.CAUSE).getRef();
		project.executeEndTransaction();
		ORef diagramFactorRef3 = project.createAndAddFactorToDiagram(ObjectType.CAUSE).getRef();
		project.executeEndTransaction();

		project.undo();
		assertNull("Didn't undo start of outer transaction?", DiagramFactor.find(project, diagramFactorRef1));
		assertNull("Didn't undo nested transaction?", DiagramFactor.find(project, diagramFactorRef2));
		assertNull("Didn't undo end of outer transaction?", DiagramFactor.find(project, diagramFactorRef3));
		project.redo();
		assertNotNull("Didn't redo start of outer transaction?", DiagramFactor.find(project, diagramFactorRef1));
		assertNotNull("Didn't redo nested transaction?", DiagramFactor.find(project, diagramFactorRef2));
		assertNotNull("Didn't redo end of outer transaction?", DiagramFactor.find(project, diagramFactorRef3));
	}

	public void testGetSnappedSize() throws Exception
	{		
		Dimension zeroByZero = new Dimension(0, 0);
		assertEquals("moved zero zero?", zeroByZero, project.getSnapped(zeroByZero));
		
		Dimension defaultPlus = new Dimension(Project.DEFAULT_GRID_SIZE, Project.DEFAULT_GRID_SIZE);
		assertEquals("moved default plus?", defaultPlus, project.getSnapped(defaultPlus));
		
		Dimension defaultMinus = new Dimension(-Project.DEFAULT_GRID_SIZE, -Project.DEFAULT_GRID_SIZE);
		assertEquals("moved default minus?", defaultMinus, project.getSnapped(defaultMinus));

		Dimension oneThirdPlus = new Dimension(Project.DEFAULT_GRID_SIZE/3, Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap one third?", zeroByZero, project.getSnapped(oneThirdPlus));
		
		Dimension oneThirdMinus = new Dimension(-Project.DEFAULT_GRID_SIZE/3, -Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap minus one third?", zeroByZero, project.getSnapped(oneThirdMinus));
		
		Dimension twoThirdsPlus = new Dimension(2*Project.DEFAULT_GRID_SIZE/3, 2*Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap two thirds?", defaultPlus, project.getSnapped(twoThirdsPlus));
		
		Dimension twoThirdsMinus = new Dimension(-2*Project.DEFAULT_GRID_SIZE/3, -2*Project.DEFAULT_GRID_SIZE/3);
		assertEquals("didn't snap minus two thirds?", defaultMinus, project.getSnapped(twoThirdsMinus));		
	}
	
	public void testForceNonZeroEvenSnap()
	{
		verifyEvenSnap(30, 0);
		verifyEvenSnap(30, 15);
		verifyEvenSnap(30, 29);
		verifyEvenSnap(30, 40);
		verifyEvenSnap(60, 45);
	}
	
	private void verifyEvenSnap(int expectedValue, int valueToEvenSnap)
	{
		assertEquals("value was not snapped to even size?", expectedValue, getProject().forceNonZeroEvenSnap(valueToEvenSnap));
	}

	public void testIsValidMpfProjectFilename() throws Exception
	{
		assertTrue("AlphaNumericDotDashSpace", Project.isValidMpfProjectFilename("AZaz09_.Miradi"));
		assertFalse("allowed really long name?", Project.isValidMpfProjectFilename(createLargerThanAllowedProjectName() + MpfFileFilterWithDirectories.EXTENSION));
		assertFalse("Other Punct", Project.isValidMpfProjectFilename("$.Miradi"));
		final char ACCENT_A_LOWER = 0xE1;
		assertTrue("Foreign", Project.isValidMpfProjectFilename(new String(new char[] {ACCENT_A_LOWER}) + ".Miradi"));
		assertFalse("Empty", Project.isValidMpfProjectFilename(".Miradi"));
	}
	
	public void testMakeProjectFilenameLegal() throws Exception
	{
		assertEquals("didn't fix empty?", "-", Project.makeProjectNameLegal(""));
		String longest = createLargerThanAllowedProjectName();
		final String legalExpectedProjectName = longest.substring(0, getProject().getMaximumProjectNameLength());
		assertEquals("didn't fix long?", legalExpectedProjectName, Project.makeProjectNameLegal(longest));
		String allGood = "abc_123. -";
		assertEquals("Ruined a good name?", allGood, Project.makeProjectNameLegal(allGood));
		String bad = "`~!@#$%^&*()=+[]\\{}|;':\',<>/?. -";
		assertEquals("Didn't fix bad?", "-----------------------------. -", Project.makeProjectNameLegal(bad));
	}

	private String createLargerThanAllowedProjectName()
	{
		String projectName = "123456789012345678901234567890123";
		while (projectName.length() < Project.MAX_PROJECT_FILENAME_LENGTH)
		{
			projectName += projectName;
		}
		
		return projectName;
	}
	
	public void testGetAllSelectedCellsWithLinkages() throws Exception
	{
		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);
		FactorCell node2 =  project.createFactorCell(ObjectType.STRATEGY);
		FactorCell node3 =  project.createFactorCell(ObjectType.CAUSE);
		
		DiagramLink linkage1 = createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), node2.getWrappedFactorRef());
		DiagramLink linkage2 = createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), node3.getWrappedFactorRef());
		
		LinkCell cell1 = project.getTestingDiagramModel().findLinkCell(linkage1);
		LinkCell cell2 = project.getTestingDiagramModel().findLinkCell(linkage2);
		EAMGraphCell[] selectedCells = {cell1};
		DiagramModel model = project.getTestingDiagramModel();
		HashSet<EAMGraphCell> selectedItems = model.getAllSelectedCellsWithRelatedLinkages(selectedCells);
		assertEquals(1, selectedItems.size());
		assertContains(cell1, selectedItems);
		
		selectedCells[0] = node2;
		selectedItems = model.getAllSelectedCellsWithRelatedLinkages(selectedCells);
		assertEquals(2, selectedItems.size());
		assertContains(node2, selectedItems);
		assertContains(cell1, selectedItems);
		
		selectedCells[0] = node1;
		selectedItems = model.getAllSelectedCellsWithRelatedLinkages(selectedCells);
		assertEquals(3, selectedItems.size());
		assertContains(node1, selectedItems);
		assertContains(cell1, selectedItems);
		assertContains(cell2, selectedItems);
	}

	public void testGetAllSelectedNodes() throws Exception
	{
		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);
		FactorCell node2 =  project.createFactorCell(ObjectType.STRATEGY);
		
		DiagramLink linkage1 = createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), node2.getWrappedFactorRef());
		
		LinkCell cell1 = project.getTestingDiagramModel().findLinkCell(linkage1);

		EAMGraphCell[] selectedCells = {cell1};
		EAMGraphCell[] selectedItems = DiagramPanel.getOnlySelectedFactorCells(selectedCells);
		assertEquals(0, selectedItems.length);
		
		selectedCells[0] = node2;
		selectedItems = DiagramPanel.getOnlySelectedFactorCells(selectedCells);
		assertEquals(1, selectedItems.length);
		assertEquals(node2, selectedItems[0]);
		
		EAMGraphCell[] selectedCellsTwo = {node2, cell1, node1};
		selectedItems = DiagramPanel.getOnlySelectedFactorCells(selectedCellsTwo);
		assertEquals(2, selectedItems.length);
	}
	
	public void testPasteNodesAndLinksIntoProject() throws Exception
	{
		DiagramModel model = project.getTestingDiagramModel();

		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);
		DiagramFactor diagramFactor1 = project.createDiagramFactorAndAddToDiagram(ObjectType.STRATEGY);
		FactorCell node3 =  project.createFactorCell(ObjectType.CAUSE);
		
		createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), diagramFactor1.getWrappedORef());
		createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), node3.getWrappedFactorRef());
		
		HashSet<EAMGraphCell> cellVector = model.getAllSelectedCellsWithRelatedLinkages(new EAMGraphCell[]{node1});
		EAMGraphCell[] selectedCells = cellVector.toArray(new EAMGraphCell[0]);
		ORef diagramObjectRef = project.getTestingDiagramObject().getRef();
		AbstractTransferableMiradiList transferableList = new TransferableMiradiListVersion4(project, diagramObjectRef);
		transferableList.storeData(selectedCells);
		assertEquals(3, project.getAllDiagramFactorIds().length);
		assertEquals(2, model.getFactorLinks(node1).size());
		assertEquals(1, model.getFactorLinksSize(diagramFactor1.getRef()));
		assertEquals(1, model.getFactorLinks(node3).size());
		
		new DiagramCopyPaster(null, project.getTestingDiagramModel(), transferableList).pasteFactorsAndLinks(new Point(5,5));
		DiagramFactorId[] diagramFactorIds = project.getAllDiagramFactorIds();
		assertEquals(4, diagramFactorIds.length);
		assertEquals(4, model.getAllDiagramFactorLinks().size());
		for(int i = 0; i < diagramFactorIds.length; ++i)
		{
			assertEquals(2, project.getToAndFromLinks(diagramFactorIds[i]).length);
		}
		
		//Test when a pasted item has linkages to a previously deleted node
		model.removeDiagramFactor(diagramFactor1.getRef());
		new DiagramCopyPaster(null, project.getTestingDiagramModel(), transferableList).pasteFactorsAndLinks(new Point(5,5));
		assertEquals(2, model.getFactorLinks(node1).size());
		assertEquals(3, model.getFactorLinks(node3).size());
	}

	public void testDiagramMoveOnly() throws Exception
	{
		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);
		
		// reset command stack to empty
		while(project.popLastCommand() != null)
			;
		
		node1.setPreviousLocation(new Point(0,0));
		node1.setLocation(new Point(0,0));
		node1.setPreviousSize(node1.getSize());

		ORefList noNodesMoved = new ORefList();
		noNodesMoved.add(node1.getDiagramFactorRef());
	
		new FactorMoveHandler(project, project.getTestingDiagramModel()).factorsWereMovedOrResized(noNodesMoved);
		
		assertNull("Move executed a real command?", project.popToLastNonTransactionCommand());
		
		Point deltaLocation = new Point(55, 88);
		node1.setLocation(deltaLocation);
		
		FactorCell node2 =  project.createFactorCell(ObjectType.CAUSE);
		node2.setPreviousLocation(new Point(10,10));
		Point node2Location = new Point(20,30);
		node2.setLocation(node2Location);
		
		ORefList diagramFactorRefs = new ORefList();
		diagramFactorRefs.add(node1.getDiagramFactorRef());
		diagramFactorRefs.add(node2.getDiagramFactorRef());
		
		project.executeCommand(new CommandBeginTransaction());
		new FactorMoveHandler(project, project.getTestingDiagramModel()).factorsWereMovedOrResized(diagramFactorRefs);
		project.executeCommand(new CommandEndTransaction());
		
		CommandSetObjectData commandDiagramMove1 = (CommandSetObjectData)project.popToLastNonTransactionCommand();
		assertEquals(node2.getDiagramFactorId(), commandDiagramMove1.getObjectId());
		Point point1 = EnhancedJsonObject.convertToPoint(commandDiagramMove1.getDataValue());
		assertEquals(point1, project.getSnapped(node2Location));
		
		CommandSetObjectData commandDiagramMove2 = (CommandSetObjectData)project.popToLastNonTransactionCommand();
		assertEquals(node1.getDiagramFactorId(), commandDiagramMove2.getObjectId());
		Point point2 = EnhancedJsonObject.convertToPoint(commandDiagramMove2.getDataValue());
		assertEquals(point2, project.getSnapped(deltaLocation));
		
		project.popLastCommand(); //begin Transaction
	}
	
	public void testResizeNodesOnly() throws Exception
	{
		FactorCell node1 =  project.createFactorCell(ObjectType.STRATEGY);
		DiagramFactor diagramFactor1 = node1.getDiagramFactor();
		node1.setSize(new Dimension(5,10));
		node1.setPreviousSize((new Dimension(55, 80)));
		node1.setPreviousLocation(new Point(0,0));
		node1.setLocation(new Point(0,0));
		diagramFactor1.setSize(node1.getPreviousSize());
		diagramFactor1.setLocation(node1.getPreviousLocation());

		FactorCell node2 =  project.createFactorCell(ObjectType.CAUSE);
		DiagramFactor diagramFactor2 = node2.getDiagramFactor();
		node2.setSize(new Dimension(15,15));
		node2.setPreviousSize((new Dimension(52, 33)));
		node2.setPreviousLocation(new Point(0,0));
		node2.setLocation(new Point(0,0));
		diagramFactor2.setSize(node2.getPreviousSize());
		diagramFactor2.setLocation(node2.getPreviousLocation());

		
		ORefList diagramFactorRefs = new ORefList();
		diagramFactorRefs.add(node1.getDiagramFactorRef());
		diagramFactorRefs.add(node2.getDiagramFactorRef());
		
		new FactorMoveHandler(project, project.getTestingDiagramModel()).factorsWereMovedOrResized(diagramFactorRefs);
		
		project.popLastCommand(); //End Transaction
		
		CommandSetObjectData commandSetNodeSize2 = (CommandSetObjectData)project.popLastCommand();
		DiagramFactor diagramFactor = (DiagramFactor) project.findObject(new ORef(ObjectType.DIAGRAM_FACTOR, node2.getDiagramFactorId()));
		String foundSize =  EnhancedJsonObject.convertFromDimension(diagramFactor.getSize());
		assertEquals(foundSize, commandSetNodeSize2.getDataValue());
		
		String node2PreviousSize = EnhancedJsonObject.convertFromDimension(node2.getPreviousSize());
		assertEquals(node2PreviousSize, commandSetNodeSize2.getPreviousDataValue());
		
		CommandSetObjectData commandSetNodeSize1 = (CommandSetObjectData)project.popToLastNonTransactionCommand();
		String size = EnhancedJsonObject.convertFromDimension(node1.getSize());
		assertEquals(size, commandSetNodeSize1.getDataValue());
		String previousSize = EnhancedJsonObject.convertFromDimension(node1.getPreviousSize());
		assertEquals(previousSize, commandSetNodeSize1.getPreviousDataValue());
		
		project.popLastCommand(); //begin Transaction
	}

	public void testResizeAndMoveNodes() throws Exception
	{
		int x = 5;
		int y = 10;
		int deltaX = 20;
		int deltaY = 30;
		//Note: the expceted sizes are expected to be in even incr of the DefaultGridSize.
		//TODO: test code shuold be converted to use the DefaultGridSize
		Dimension position1 = new Dimension(30, 60);
		Dimension position2 = new Dimension(60, 90);
		
		
		FactorCell nodeResizedAndMoved =  project.createFactorCell(ObjectType.STRATEGY);
		nodeResizedAndMoved.setSize(position1);
		nodeResizedAndMoved.setPreviousSize(position2);
		nodeResizedAndMoved.setPreviousLocation(new Point(x,y));
		nodeResizedAndMoved.setLocation(new Point(x+deltaX, y+deltaY));
		DiagramFactor diagramFactorResizedAndMoved = nodeResizedAndMoved.getDiagramFactor();
		diagramFactorResizedAndMoved.setLocation(nodeResizedAndMoved.getPreviousLocation());
		diagramFactorResizedAndMoved.setSize(nodeResizedAndMoved.getPreviousSize());

		FactorCell nodeMovedOnly =  project.createFactorCell(ObjectType.CAUSE);
		nodeMovedOnly.setSize(position1);
		nodeMovedOnly.setPreviousSize(position1);
		nodeMovedOnly.setPreviousLocation(new Point(x,y));
		nodeMovedOnly.setLocation(new Point(x+deltaX, y+deltaY));
		DiagramFactor diagramFactorMovedOnly = nodeMovedOnly.getDiagramFactor();
		diagramFactorMovedOnly.setLocation(nodeMovedOnly.getPreviousLocation());
		diagramFactorMovedOnly.setSize(nodeMovedOnly.getPreviousSize());
		
		FactorCell nodeResizedOnly = project.createFactorCell(ObjectType.CAUSE);
		nodeResizedOnly.setSize(position1);
		nodeResizedOnly.setPreviousSize(position2);
		nodeResizedOnly.setPreviousLocation(new Point(x,y));
		nodeResizedOnly.setLocation(new Point(x,y));
		DiagramFactor diagramFactorResizedOnly = nodeResizedOnly.getDiagramFactor();
		diagramFactorResizedOnly.setLocation(nodeResizedOnly.getPreviousLocation());
		diagramFactorResizedOnly.setSize(nodeResizedOnly.getPreviousSize());
		
		FactorCell nodeNotMovedOrResized =  project.createFactorCell(ObjectType.CAUSE);
		nodeNotMovedOrResized.setSize(position1);
		nodeNotMovedOrResized.setPreviousSize(position1);
		nodeNotMovedOrResized.setPreviousLocation(new Point(x,y));
		nodeNotMovedOrResized.setLocation(new Point(x,y));
		DiagramFactor diagramFactorNotMovedOrResized = nodeNotMovedOrResized.getDiagramFactor();
		diagramFactorNotMovedOrResized.setLocation(nodeNotMovedOrResized.getPreviousLocation());
		diagramFactorNotMovedOrResized.setSize(nodeNotMovedOrResized.getPreviousSize());
		
		ORefList diagramFactorRefs = new ORefList();
		diagramFactorRefs.add(nodeResizedAndMoved.getDiagramFactorRef());
		diagramFactorRefs.add(nodeMovedOnly.getDiagramFactorRef());
		diagramFactorRefs.add(nodeResizedOnly.getDiagramFactorRef());
		diagramFactorRefs.add(nodeNotMovedOrResized.getDiagramFactorRef());

		new FactorMoveHandler(project, project.getTestingDiagramModel()).factorsWereMovedOrResized(diagramFactorRefs);
		
		CommandSetObjectData commandNodeResizedOnly = (CommandSetObjectData)project.popToLastNonTransactionCommand();
		String nodeResizeOnlySize = EnhancedJsonObject.convertFromDimension(nodeResizedOnly.getSize());
		assertEquals(nodeResizeOnlySize, commandNodeResizedOnly.getDataValue());
		
		String nodeResizeOnlyPreviousSize = EnhancedJsonObject.convertFromDimension(nodeResizedOnly.getPreviousSize()); 
		assertEquals(nodeResizeOnlyPreviousSize, commandNodeResizedOnly.getPreviousDataValue());
		
		
		CommandSetObjectData commandDiagramMoveRecorded = (CommandSetObjectData)project.popToLastNonTransactionCommand();
		Point deltaPoint = EnhancedJsonObject.convertToPoint(commandDiagramMoveRecorded.getDataValue());
		assertEquals(diagramFactorMovedOnly.getLocation(), deltaPoint);
		assertEquals(diagramFactorMovedOnly.getDiagramFactorId(), commandDiagramMoveRecorded.getObjectId());
		
		CommandSetObjectData commandNodeResizedAndMovedRecorded = (CommandSetObjectData)project.popToLastNonTransactionCommand();
		String nodeResizedAndMovedSize = EnhancedJsonObject.convertFromDimension(nodeResizedAndMoved.getSize());
		assertEquals(nodeResizedAndMovedSize, commandNodeResizedAndMovedRecorded.getDataValue());
		
		String nodeResizedAndMovedPreviousSize = EnhancedJsonObject.convertFromDimension(nodeResizedAndMoved.getPreviousSize());
		assertEquals(nodeResizedAndMovedPreviousSize, commandNodeResizedAndMovedRecorded.getPreviousDataValue());
	}

	public void testPasteNodesOnlyIntoProject() throws Exception
	{
		DiagramModel model = project.getTestingDiagramModel();

		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);
		FactorCell node2 = project.createFactorCell(ObjectType.STRATEGY);
		FactorCell node3 = project.createFactorCell(ObjectType.CAUSE);
		
		createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), node2.getWrappedFactorRef());
		createLinkage(idAssigner.takeNextId(), node1.getWrappedFactorRef(), node3.getWrappedFactorRef());
		
		HashSet<EAMGraphCell> cellVector = model.getAllSelectedCellsWithRelatedLinkages(new EAMGraphCell[]{node1});
		EAMGraphCell[] selectedCells = cellVector.toArray(new EAMGraphCell[0]);
		ORef diagramObjectRef = project.getTestingDiagramObject().getRef();
		AbstractTransferableMiradiList transferableList = new TransferableMiradiListVersion4(project, diagramObjectRef);
		transferableList.storeData(selectedCells);
		assertEquals(3, project.getAllDiagramFactorIds().length);
		assertEquals(2, model.getFactorLinks(node1).size());
		assertEquals(1, model.getFactorLinks(node2).size());
		assertEquals(1, model.getFactorLinks(node3).size());
		
		new DiagramCopyPaster(null, project.getTestingDiagramModel(), transferableList).pasteFactors(new Point(5,5));
		DiagramFactorId[] diagramFactorIds = project.getAllDiagramFactorIds();
		assertEquals(4, diagramFactorIds.length);
		assertEquals(2, model.getAllDiagramFactorLinks().size());
	}
	
	public void testCutAndPaste() throws Exception
	{
		assertEquals("objects already in the cause pool?", 0, project.getCausePool().size());
		assertEquals("objects already in the strategy pool?", 0, project.getStrategyPool().size());
		assertEquals("objects already in the target pool?", 0, project.getTargetPool().size());
		assertEquals("nodes  already in the diagram?", 0, project.getAllDiagramFactorIds().length);

		FactorCell node1 = project.createFactorCell(ObjectType.TARGET);
		EAMGraphCell[] selectedCells = new FactorCell[] {node1};
		ORef diagramObjectRef = project.getTestingDiagramObject().getRef();
		AbstractTransferableMiradiList transferableList = new TransferableMiradiListVersion4(project, diagramObjectRef);
		transferableList.storeData(selectedCells);
		
		DiagramFactorId idToDelete = node1.getDiagramFactorId();
		DiagramObject diagramObject = project.getTestingDiagramObject();
		CommandSetObjectData removeFactor = CommandSetObjectData.createRemoveIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_IDS, idToDelete);
		project.executeCommand(removeFactor);

		BaseObject target = project.findObject(new ORef(ObjectType.TARGET, node1.getWrappedId()));
		project.deleteObject(target);
		project.deleteObject(node1.getDiagramFactor());
		
		assertEquals("objects still in the target pool?", 0, project.getTargetPool().size());
		assertEquals("nodes  still in the diagram?", 0, project.getAllDiagramFactorIds().length);

		Point pastePoint = new Point(5,5);
		DiagramPaster diagramPaster = new DiagramCopyPaster(null, project.getTestingDiagramModel(), transferableList);
		diagramPaster.pasteFactorsAndLinks(pastePoint);
		DiagramFactor diagramFactors[] = project.getAllDiagramFactors();
		assertEquals(1, diagramFactors.length);
		DiagramFactor pastedNode = diagramFactors[0];
		assertEquals("didn't paste correct size?", node1.getSize(), pastedNode.getSize());
		assertNotEquals("didn't change id?", node1.getDiagramFactorId(), pastedNode.getDiagramFactorId());
		assertEquals("didn't snap?", project.getSnapped(pastePoint), pastedNode.getLocation());

		assertEquals("not just one object in the target pool?", 1, project.getTargetPool().size());
	}

	public void testCloseClearsCurrentView() throws Exception
	{
		assertEquals("not starting on NoProject view?", Project.NO_PROJECT_VIEW_NAME, project.getCurrentView());
		project.createOrOpenWithDefaultObjects(new File(getName()), null);
		assertEquals("not starting on summary view?", Project.SUMMARY_VIEW_NAME, project.getCurrentView());
		String sampleViewName = Project.MAP_VIEW_NAME;
		project.switchToView(sampleViewName);
		assertEquals("didn't switch?", sampleViewName, project.getCurrentView());
		project.close();
		assertEquals("didn't reset view?", Project.NO_PROJECT_VIEW_NAME, project.getCurrentView());
	}
	
	public void testLinkagePool() throws Exception
	{
		FactorCell nodeA = project.createFactorCell(ObjectType.CAUSE);
		FactorCell nodeB = project.createFactorCell(ObjectType.TARGET);
		ORef refA = nodeA.getWrappedFactorRef();
		ORef refB = nodeB.getWrappedFactorRef();
		BaseId createdId = project.createObjectAndReturnId(ObjectType.FACTOR_LINK, idAssigner.takeNextId());
		final ORef factorLinkRef = new ORef(ObjectType.FACTOR_LINK, createdId);
		project.setObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, nodeA.getWrappedFactorRef().toString());
		project.setObjectData(factorLinkRef, FactorLink.TAG_TO_REF, nodeB.getWrappedFactorRef().toString());

		BaseId linkageId = createdId;
		FactorLinkPool linkagePool = project.getFactorLinkPool();
		assertEquals("not in pool?", 1, linkagePool.size());
		FactorLink cmLinkage = linkagePool.find(linkageId);
		assertEquals("wrong from?", refA, cmLinkage.getFromFactorRef());
		assertEquals("wrong to?", refB, cmLinkage.getToFactorRef());
		assertTrue("not linked?", project.areLinked(nodeA.getWrappedFactorRef(), nodeB.getWrappedFactorRef()));
		
		project.deleteObject(cmLinkage);
		assertEquals("Didn't remove from pool?", 0, linkagePool.size());
		assertFalse("still linked?", project.areLinked(nodeA.getWrappedFactorRef(), nodeB.getWrappedFactorRef()));
	}

	public void testFindAllNodesRelatedToThisObjective() throws Exception
	{
		FactorCell nodeContributingFactor = project.createFactorCell(ObjectType.CAUSE);
		FactorCell nodeDirectThreat = project.createFactorCell(ObjectType.CAUSE);
		
		BaseId objectiveId1 = project.createObjectAndReturnId(ObjectType.OBJECTIVE);
		
		IdList objectiveId = new IdList(ObjectiveSchema.getObjectType());
		objectiveId.add(objectiveId1);

		Factor cf = nodeContributingFactor.getWrappedFactor();
		CommandSetObjectData addObjective = new CommandSetObjectData(cf, Cause.TAG_OBJECTIVE_IDS, objectiveId.toString());
		getProject().executeCommand(addObjective);
		
		
		createLinkage(BaseId.INVALID, nodeContributingFactor.getWrappedFactorRef(), nodeDirectThreat.getWrappedFactorRef());
		ORef ref = new ORef(ObjectType.OBJECTIVE, objectiveId1);
		
		FactorSet foundNodes = findAllFactorsRelatedToThisObject(ref);
		
		assertEquals("didn't find anything?", 2, foundNodes.size());
		assertContains("missing direct threat?", nodeDirectThreat.getWrappedFactor(), foundNodes);
		assertContains("missing contributing factor?", cf, foundNodes);
	}
	
	public void testFindAllNodesRelatedToThisIndicator() throws Exception
	{
		FactorCell nodeContributingFactor = project.createFactorCell(ObjectType.CAUSE);
		FactorCell nodeDirectThreat = project.createFactorCell(ObjectType.CAUSE);
		
		IndicatorId indicatorId1 = (IndicatorId)project.createObjectAndReturnId(ObjectType.INDICATOR);
		IdList indicators1 = new IdList(IndicatorSchema.getObjectType());
		indicators1.add(indicatorId1);

		Factor cf = nodeContributingFactor.getWrappedFactor();
		CommandSetObjectData addIndicator = new CommandSetObjectData(cf, Cause.TAG_INDICATOR_IDS, indicators1.toString());
		getProject().executeCommand(addIndicator);
		
		createLinkage(BaseId.INVALID, nodeContributingFactor.getWrappedFactorRef(), nodeDirectThreat.getWrappedFactorRef());
		ORef ref = new ORef(ObjectType.INDICATOR, indicatorId1);
		
		FactorSet foundNodes = findAllFactorsRelatedToThisObject(ref);
		
		assertEquals("didn't find anything?", 2, foundNodes.size());
		assertContains("missing direct threat?", nodeDirectThreat.getWrappedFactor(), foundNodes);
		assertContains("missing contributing factor?", nodeContributingFactor.getWrappedFactor(), foundNodes);
	}
	
	private FactorSet findAllFactorsRelatedToThisObject(ORef ref) throws Exception
	{
		BaseObject owner = getProject().findObject(ref);
		Factor owningFactor = owner.getDirectOrIndirectOwningFactor();
		FactorSet relatedFactors = new FactorSet();
		
		if(owningFactor != null)
		{
			ChainWalker chainObject = owner.getNonDiagramChainWalker();
			FactorSet nodesInChain = chainObject.buildNormalChainAndGetFactors(owningFactor);
			
			relatedFactors.attemptToAddAll(nodesInChain);
		}
		
		return relatedFactors;
	}
	

	public void testDirectThreatSet() throws Exception
	{
		FactorCell nodeContributingFactor = project.createFactorCell(ObjectType.CAUSE);
		FactorCell nodeDirectThreatA = project.createFactorCell(ObjectType.CAUSE);
		getProject().enableAsThreat(nodeDirectThreatA.getWrappedFactorRef());
		
		FactorCell target = project.createFactorCell(ObjectType.TARGET);
		BaseId factorLinkId = project.createObjectAndReturnId(ObjectType.FACTOR_LINK, BaseId.INVALID);
		final ORef factorLinkRef = new ORef(ObjectType.FACTOR_LINK, factorLinkId);
		project.setObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, nodeDirectThreatA.getWrappedFactorRef().toString());
		project.setObjectData(factorLinkRef, FactorLink.TAG_TO_REF, target.getWrappedFactorRef().toString());
		
		FactorCell nodeDirectThreatB = project.createFactorCell(ObjectType.CAUSE);
		getProject().enableAsThreat(nodeDirectThreatB.getWrappedFactorRef());
		final BaseId factorLinkId2 = project.createObjectAndReturnId(ObjectType.FACTOR_LINK, BaseId.INVALID);
		final ORef factorLinkRef2 = new ORef(ObjectType.FACTOR_LINK, factorLinkId2);
		project.setObjectData(factorLinkRef2, FactorLink.TAG_FROM_REF, nodeDirectThreatB.getWrappedFactorRef().toString());
		project.setObjectData(factorLinkRef2, FactorLink.TAG_TO_REF, target.getWrappedFactorRef().toString());
		
		FactorSet allNodes = new FactorSet();
		allNodes.attemptToAdd(nodeContributingFactor.getWrappedFactor());
		allNodes.attemptToAdd(nodeDirectThreatA.getWrappedFactor());
		allNodes.attemptToAdd(nodeDirectThreatB.getWrappedFactor());	
		
		DirectThreatSet foundNodes = new DirectThreatSet(allNodes);
		
		assertEquals("didn't find both nodes?", 2, foundNodes.size());
		assertContains("missing nodeDirectThreatA? ", nodeDirectThreatA.getWrappedFactor(), foundNodes);
		assertContains("missing nodeDirectThreatB?", nodeDirectThreatB.getWrappedFactor(), foundNodes);
	}
	  
	public void testOpenProject() throws Exception
	{
		int memorizedHighestId = -1;
		
		ORef factorRef;
		File tempDir = createTempDirectory();
		File projectFile = new File(tempDir, getName());
		Project diskProject = new Project();
		String savedProject = "";

		diskProject.createWithDefaultObjectsAndDiagramHelp(projectFile, new NullProgressMeter());
		
		ORef conceptualModelRef = diskProject.getConceptualModelDiagramPool().getORefList().getRefForType(ConceptualModelDiagramSchema.getObjectType());
		ConceptualModelDiagram conceptualModel = ConceptualModelDiagram.find(diskProject, conceptualModelRef);
		try
		{
			DiagramFactor cause = createNodeAndAddToDiagram(diskProject, conceptualModel, ObjectType.CAUSE);
			factorRef = cause.getWrappedORef();
			DiagramFactor target = createNodeAndAddToDiagram(diskProject, conceptualModel, ObjectType.TARGET);
			
			LinkCreator linkCreator = new LinkCreator(diskProject);
			linkCreator.createFactorLinkAndAddToDiagramUsingCommands(conceptualModel, cause, target);
			diskProject.setObjectData(factorRef, Cause.TAG_IS_DIRECT_THREAT, BooleanData.BOOLEAN_TRUE);
			
			FactorId interventionId = (FactorId)diskProject.createObjectAndReturnId(ObjectType.STRATEGY);
			Factor object = (Factor) diskProject.findObject(new ORef(ObjectType.STRATEGY, interventionId));
			diskProject.deleteObject(object);
	
			DiagramFactor diagramFactor = createNodeAndAddToDiagram(diskProject, conceptualModel, ObjectType.CAUSE);
			deleteNodeAndRemoveFromDiagram(conceptualModel, diagramFactor);
			
			memorizedHighestId = diskProject.getNormalIdAssigner().getHighestAssignedId();

			UnicodeStringWriter writer = UnicodeStringWriter.create();
			ProjectSaver.saveProject(diskProject, writer);
			savedProject = writer.toString();
		}
		finally
		{
			diskProject.close();
		}
		
		UnicodeStringReader reader = new UnicodeStringReader(savedProject);
		Project loadedProject = new Project();
		ProjectLoader.loadProject(reader, loadedProject);
		try
		{
			assertEquals("didn't read cause pool?", 1, loadedProject.getCausePool().size());
			assertEquals("didn't read strategy pool?", 0, loadedProject.getStrategyPool().size());
			assertEquals("didn't read target pool?", 1, loadedProject.getTargetPool().size());
			assertEquals("didn't read text box pool?", 1, loadedProject.getTextBoxPool().size());
			
			assertEquals("didn't add diagram factors to pool?", 3, loadedProject.getDiagramFactorPool().size());
			assertEquals("too man conceptual models in pool?", 1, loadedProject.getConceptualModelDiagramPool().size());
			
			assertEquals("didn't read link pool?", 1, loadedProject.getFactorLinkPool().size());
			assertEquals("didn't populate diagram?", 3, conceptualModel.getAllDiagramFactorRefs().size());
			assertEquals("didn't preserve next node id?", memorizedHighestId, loadedProject.getNormalIdAssigner().getHighestAssignedId());
			assertEquals("didn't preserve next annotation id?", memorizedHighestId, loadedProject.getNormalIdAssigner().getHighestAssignedId());
			Cause factor = Cause.find(loadedProject, factorRef);
			assertTrue("didn't update factor target count?", factor.isDirectThreat());
		}
		finally
		{
			loadedProject.close();
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
		
		assertEquals("didn't clear node cause pool?", 0, diskProject.getCausePool().size());
		assertEquals("didn't clear node strategy pool?", 0, diskProject.getStrategyPool().size());
		assertEquals("didn't clear node target pool?", 0, diskProject.getTargetPool().size());
		assertEquals("didn't clear conceptual model diagram pool?", 0, diskProject.getConceptualModelDiagramPool().size());
		assertEquals("didn't clear link pool?", 0, diskProject.getFactorLinkPool().size());
		assertTrue("didn't clear next annotation id?", diskProject.getNormalIdAssigner().getHighestAssignedId() < memorizedHighestId);
	}
	
	private void deleteNodeAndRemoveFromDiagram(DiagramObject diagramObject, DiagramFactor diagramFactor) throws Exception
	{
		FactorDeleteHelper factorHelper = FactorDeleteHelper.createFactorDeleteHelperForNonSelectedFactors(diagramObject);
		factorHelper.deleteFactorAndDiagramFactor(diagramFactor);
	}
	
	public void testCreateNewProject() throws Exception
	{
		File tempDir = createTempDirectory();
		File projectFile = new File(tempDir, getName());
		Project newProject = new Project();
		newProject.createWithDefaultObjectsAndDiagramHelp(projectFile, new NullProgressMeter());
		
		try
		{
			assertEquals("default criterion not created?", 3, newProject.getSimpleThreatRatingFramework().getCriteria().length);
			assertEquals("default valueoptions not created?", 5, newProject.getSimpleThreatRatingFramework().getValueOptions().length);
			assertEquals("default currency not set?", "$", newProject.getMetadata().getData(ProjectMetadata.TAG_CURRENCY_SYMBOL));
		}
		finally
		{
			newProject.close();
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
	}
	
	private DiagramLink createLinkage(BaseId id, ORef fromFactorRef, ORef toFactorRef) throws Exception
	{
		DiagramFactor fromDiagramFactor = project.getTestingDiagramModel().getFactorCellByWrappedRef(fromFactorRef).getDiagramFactor();
		DiagramFactor toDiagramFactor = project.getTestingDiagramModel().getFactorCellByWrappedRef(toFactorRef).getDiagramFactor();
		BaseId createdId = project.createObjectAndReturnId(ObjectType.FACTOR_LINK, id);
		final ORef factorLinkRef = new ORef(ObjectType.FACTOR_LINK, createdId);
		getProject().setObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, fromDiagramFactor.getWrappedORef().toString());
		getProject().setObjectData(factorLinkRef, FactorLink.TAG_TO_REF, toDiagramFactor.getWrappedORef().toString());

		
		ORef diagramLinkRef = project.createObject(DiagramLinkSchema.getObjectType());
		getProject().setObjectData(diagramLinkRef, DiagramLink.TAG_WRAPPED_ID, createdId.toString());
    	getProject().setObjectData(diagramLinkRef, DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, fromDiagramFactor.getId().toString());
    	getProject().setObjectData(diagramLinkRef, DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, toDiagramFactor.getId().toString());
		
		DiagramObject diagramObject = project.getTestingDiagramObject();
		CommandSetObjectData addLink = CommandSetObjectData.createAppendIdCommand(diagramObject, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS, diagramLinkRef.getObjectId());
		project.executeCommand(addLink);

		return project.getTestingDiagramModel().getDiagramLinkByRef(diagramLinkRef);
	}

	public DiagramFactor createNodeAndAddToDiagram(Project projectToUse, DiagramObject diagramObject, int nodeType) throws Exception
	{
		FactorCommandHelper commandHelper = new FactorCommandHelper(projectToUse, diagramObject);
		CommandCreateObject createCommand = commandHelper.createFactorAndDiagramFactor(nodeType);
		DiagramFactorId diagramFactorId = (DiagramFactorId) createCommand.getCreatedId();
		DiagramFactor diagramFactor = (DiagramFactor) projectToUse.findObject(ObjectType.DIAGRAM_FACTOR, diagramFactorId);
		
		return diagramFactor;
	}
	
	public void testEnsureAllDiagramFactorsAreVisible() throws Exception
	{
		verifyOnScreenLocation(new Point(50, 50), new Point(50, 50));
		verifyOnScreenLocation(new Point(0, 0), new Point(0, 0));
		verifyOnScreenLocation(new Point(0, 0), new Point(-10, 0));
		verifyOnScreenLocation(new Point(0, 0), new Point(0, -10));
		verifyOnScreenLocation(new Point(10, 0), new Point(10, 0));
		verifyOnScreenLocation(new Point(0, 10), new Point(0, 10));
	}

	private void verifyOnScreenLocation(Point expectedPoint, Point initialPoint) throws Exception
	{
		DiagramFactor onScreenDiagramFactor = project.createDiagramFactorAndAddToDiagram(CauseSchema.getObjectType());
		CommandSetObjectData setOnScreenLocation = new CommandSetObjectData(onScreenDiagramFactor.getRef(), DiagramFactor.TAG_LOCATION, EnhancedJsonObject.convertFromPoint(initialPoint));
		getProject().executeCommand(setOnScreenLocation);
		
		project.ensureAllDiagramFactorsAreVisible();
		DiagramFactor alreadyOnScreenDiagramFactor = (DiagramFactor) project.findObject(onScreenDiagramFactor.getRef());
		assertEquals("did not move off screen diagram factor on screen?", expectedPoint, alreadyOnScreenDiagramFactor.getLocation());
	}
	
	public void testIsDoNothingCommandOptimizationEnabled() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		final String SOME_LABEL = "SomeLabel";
		CommandSetObjectData setLabel = new CommandSetObjectData(strategy, Strategy.TAG_LABEL, SOME_LABEL);
		getProject().executeCommand(setLabel);
		
		CommandSetObjectData setToSameLabel = new CommandSetObjectData(strategy, Strategy.TAG_LABEL, SOME_LABEL);
		assertTrue("Command should be isDoNothingCommand for setting label to what it already is?", setToSameLabel.isDoNothingCommand(getProject()));
		assertTrue("considering isDoNothingCommandOptimization should be enabled by default?", getProject().isDoNothingCommandEnabledOptimization());
		assertTrue("setting label to same value should not do anything?", getProject().isDoNothingCommand(setToSameLabel));
		
		getProject().disableIsDoNothingCommandOptimization();
		assertFalse("isDoNothingCommandOptimization is not disabled?", getProject().isDoNothingCommandEnabledOptimization());
	}

	public ProjectForTesting getProject()
	{
		return project;
	}
	
	private ProjectForTesting project;
	private IdAssigner idAssigner;
}
