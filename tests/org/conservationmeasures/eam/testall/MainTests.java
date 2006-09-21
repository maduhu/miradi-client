/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.testall;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.conservationmeasures.eam.commands.TestCommandCreateObject;
import org.conservationmeasures.eam.commands.TestCommandSetObjectData;
import org.conservationmeasures.eam.commands.TestCommands;
import org.conservationmeasures.eam.database.TestDataUpgrader;
import org.conservationmeasures.eam.database.TestProjectServer;
import org.conservationmeasures.eam.diagram.TestDelete;
import org.conservationmeasures.eam.diagram.TestDiagramModel;
import org.conservationmeasures.eam.diagram.TestDiagramView;
import org.conservationmeasures.eam.diagram.TestEamGraphCell;
import org.conservationmeasures.eam.diagram.TestInsertNode;
import org.conservationmeasures.eam.diagram.TestLinkNodes;
import org.conservationmeasures.eam.diagram.TestProjectScopeBox;
import org.conservationmeasures.eam.diagram.TestSetNodeSize;
import org.conservationmeasures.eam.diagram.TestTypeIntervention;
import org.conservationmeasures.eam.diagram.TestUndoAndRedo;
import org.conservationmeasures.eam.diagram.nodes.TestDiagramCluster;
import org.conservationmeasures.eam.diagram.nodes.TestDiagramLinkage;
import org.conservationmeasures.eam.diagram.nodes.TestDiagramNode;
import org.conservationmeasures.eam.diagram.nodes.TestDiagramNodeData;
import org.conservationmeasures.eam.diagram.nodes.TestNodeDataMap;
import org.conservationmeasures.eam.diagram.nodetypes.TestNodeTypeDirectThreat;
import org.conservationmeasures.eam.diagram.nodetypes.TestNodeTypeIndirectFactor;
import org.conservationmeasures.eam.diagram.nodetypes.TestNodeTypeTarget;
import org.conservationmeasures.eam.ids.TestIdList;
import org.conservationmeasures.eam.main.TestCommandExecutedEvents;
import org.conservationmeasures.eam.main.TestMainMenu;
import org.conservationmeasures.eam.main.TestTransferableEamList;
import org.conservationmeasures.eam.objects.TestActivityInsertionPoint;
import org.conservationmeasures.eam.objects.TestConceptualModelCluster;
import org.conservationmeasures.eam.objects.TestConceptualModelIntervention;
import org.conservationmeasures.eam.objects.TestConceptualModelLinkage;
import org.conservationmeasures.eam.objects.TestConceptualModelNode;
import org.conservationmeasures.eam.objects.TestConceptualModelThreatRatings;
import org.conservationmeasures.eam.objects.TestIndicator;
import org.conservationmeasures.eam.objects.TestObjective;
import org.conservationmeasures.eam.objects.TestProjectResource;
import org.conservationmeasures.eam.objects.TestTask;
import org.conservationmeasures.eam.objects.TestThreatRatingBundle;
import org.conservationmeasures.eam.objects.TestThreatRatingCriterion;
import org.conservationmeasures.eam.objects.TestThreatRatingValueOption;
import org.conservationmeasures.eam.objects.TestViewData;
import org.conservationmeasures.eam.project.TestIdAssigner;
import org.conservationmeasures.eam.project.TestLinkagePool;
import org.conservationmeasures.eam.project.TestNodePool;
import org.conservationmeasures.eam.project.TestProject;
import org.conservationmeasures.eam.project.TestProjectInfo;
import org.conservationmeasures.eam.project.TestProjectRepairer;
import org.conservationmeasures.eam.project.TestProjectUnzipper;
import org.conservationmeasures.eam.project.TestProjectZipper;
import org.conservationmeasures.eam.project.TestRealProject;
import org.conservationmeasures.eam.project.TestTNCThreatFormula;
import org.conservationmeasures.eam.project.TestThreatRatingFramework;
import org.conservationmeasures.eam.utils.TestEnhancedJsonObject;
import org.conservationmeasures.eam.utils.TestLogging;
import org.conservationmeasures.eam.utils.TestTranslations;
import org.conservationmeasures.eam.views.threatmatrix.TestThreatMatrixModel;
import org.conservationmeasures.eam.views.umbrella.TestUndoRedo;
import org.martus.util.xml.TestSimpleXmlParser;

public class MainTests extends TestCase
{
	public static void main(String[] args)
	{
		runTests();
	}

	public static void runTests ()
	{
		junit.textui.TestRunner.run (suite());
	}
	
	public static Test suite ( )
	{
		TestSuite suite= new MainTestSuite("All eAM Tests");

		// database package
		suite.addTest(new TestSuite(TestDataUpgrader.class));
		suite.addTest(new TestSuite(TestProjectServer.class));
		
		// main package
		suite.addTest(new TestSuite(TestCommandExecutedEvents.class));
		suite.addTest(new TestSuite(TestMainMenu.class));
		suite.addTest(new TestSuite(TestTransferableEamList.class));
		
		// project package
		suite.addTest(new TestSuite(TestLinkagePool.class));
		suite.addTest(new TestSuite(TestNodePool.class));
		suite.addTest(new TestSuite(TestProject.class));
		suite.addTest(new TestSuite(TestProjectRepairer.class));
		suite.addTest(new TestSuite(TestRealProject.class));
		suite.addTest(new TestSuite(TestIdAssigner.class));
		suite.addTest(new TestSuite(TestProjectInfo.class));
		suite.addTest(new TestSuite(TestThreatRatingFramework.class));
		suite.addTest(new TestSuite(TestTNCThreatFormula.class));
		suite.addTest(new TestSuite(TestProjectZipper.class));
		suite.addTest(new TestSuite(TestProjectUnzipper.class));
		
		// utils package
		suite.addTest(new TestSuite(TestEnhancedJsonObject.class));
		suite.addTest(new TestSuite(TestLogging.class));
		suite.addTest(new TestSuite(TestTranslations.class));
		
		// diagram package
		suite.addTest(new TestSuite(TestDiagramModel.class));
		suite.addTest(new TestSuite(TestDiagramView.class));
		suite.addTest(new TestSuite(TestProjectScopeBox.class));

		// nodes package
		suite.addTest(new TestSuite(TestLinkNodes.class));
		suite.addTest(new TestSuite(TestDelete.class));
		suite.addTest(new TestSuite(TestEamGraphCell.class));
		suite.addTest(new TestSuite(TestInsertNode.class));
		suite.addTest(new TestSuite(TestDiagramCluster.class));
		suite.addTest(new TestSuite(TestDiagramLinkage.class));
		suite.addTest(new TestSuite(TestDiagramNode.class));
		suite.addTest(new TestSuite(TestDiagramNodeData.class));
		suite.addTest(new TestSuite(TestNodeDataMap.class));
		suite.addTest(new TestSuite(TestNodeTypeIndirectFactor.class));
		suite.addTest(new TestSuite(TestNodeTypeTarget.class));
		suite.addTest(new TestSuite(TestTypeIntervention.class));
		suite.addTest(new TestSuite(TestNodeTypeDirectThreat.class));
		suite.addTest(new TestSuite(TestSetNodeSize.class));
		suite.addTest(new TestSuite(TestUndoAndRedo.class));
		
		// objects package
		suite.addTest(new TestSuite(TestActivityInsertionPoint.class));
		suite.addTest(new TestSuite(TestConceptualModelCluster.class));
		suite.addTest(new TestSuite(TestConceptualModelIntervention.class));
		suite.addTest(new TestSuite(TestConceptualModelLinkage.class));
		suite.addTest(new TestSuite(TestConceptualModelNode.class));
		suite.addTest(new TestSuite(TestConceptualModelThreatRatings.class));
		suite.addTest(new TestSuite(TestIdList.class));
		suite.addTest(new TestSuite(TestIndicator.class));
		suite.addTest(new TestSuite(TestObjective.class));
		suite.addTest(new TestSuite(TestProjectResource.class));
		suite.addTest(new TestSuite(TestTask.class));
		suite.addTest(new TestSuite(TestThreatRatingBundle.class));
		suite.addTest(new TestSuite(TestThreatRatingCriterion.class));
		suite.addTest(new TestSuite(TestThreatRatingValueOption.class));
		suite.addTest(new TestSuite(TestViewData.class));
		
		// commands package
		suite.addTest(new TestSuite(TestCommands.class));
		suite.addTest(new TestSuite(TestCommandCreateObject.class));
		suite.addTest(new TestSuite(TestCommandSetObjectData.class));
		
		// view.diagram package
		suite.addTest(new TestSuite(org.conservationmeasures.eam.views.diagram.TestInsertConnection.class));
		suite.addTest(new TestSuite(org.conservationmeasures.eam.views.diagram.TestInsertNode.class));
		suite.addTest(new TestSuite(org.conservationmeasures.eam.views.diagram.TestLayerManager.class));
		
		// view.interview package
		
		// view.threatmatrix package
		suite.addTest(new TestSuite(TestThreatMatrixModel.class));
		
		// view.umbrella package
		suite.addTest(new TestSuite(TestUndoRedo.class));

		
		// martus-utils
		suite.addTest(new TestSuite(TestSimpleXmlParser.class));

	    return suite;
	}
}

class MainTestSuite extends TestSuite
{
	public MainTestSuite(String name)
	{
		super(name);
	}

	public void run(TestResult result)
	{
		reportAnyTempFiles("Existing temp file: ");
		super.run(result);
		reportAnyTempFiles("Orphaned temp file: ");
	}
	
	public void reportAnyTempFiles(String message)
	{
		File systemTempDirectory = getSystemTempDirectory();
		
		String[] allTempFileNames = systemTempDirectory.list();
		for(int i = 0; i < allTempFileNames.length; ++i)
		{
			String fileName = allTempFileNames[i];
			if(fileName.startsWith("$$$"))
				System.out.println("WARNING: " + message + fileName);
		}
	}

	private File getSystemTempDirectory()
	{
		File merelyToFindTempDirectory = createTempFileToLocateTempDirectory();
		File systemTempDirectory = merelyToFindTempDirectory.getParentFile();
		merelyToFindTempDirectory.delete();
		return systemTempDirectory;
	}

	private File createTempFileToLocateTempDirectory()
	{
		try
		{
			return File.createTempFile("$$$MainTests", null);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unable to create temp file!");
		}
	}
	
}