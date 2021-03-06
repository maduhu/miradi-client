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

import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.martus.util.DirectoryUtils;
import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.main.MiradiTestCase;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.RatingCriterion;
import org.miradi.objects.Target;
import org.miradi.objects.ValueOption;
import org.miradi.project.threatrating.SimpleThreatRatingFramework;
import org.miradi.project.threatrating.SimpleThreatFrameworkJson;
import org.miradi.project.threatrating.ThreatRatingBundle;
import org.miradi.utils.ColorManager;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.NullProgressMeter;
import org.miradi.views.diagram.LinkCreator;
import org.miradi.views.diagram.LinkDeletor;

public class TestSimpleThreatRatingFramework extends MiradiTestCase
{
	public TestSimpleThreatRatingFramework(String name)
	{
		super(name);
	}
	
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		framework = project.getSimpleThreatRatingFramework();
	}
	
	@Override
	public void tearDown() throws Exception
	{
		project.close();
		project = null;
		super.tearDown();
	}
	
	public void testJson()
	{
		EnhancedJsonObject json = framework.toJson();
		JSONArray bundleKeys = json.getJsonArray(SimpleThreatFrameworkJson.TAG_BUNDLE_KEYS);
		assertEquals("didn't jsonize bundle keys?", framework.getBundleCount(), bundleKeys.length());
	}
	
	public void testWriteAndRead() throws Exception
	{
		File tempDir = createTempDirectory();
		File projectFile = new File(tempDir, getName());
		try
		{
			Project realProject = new Project();
			realProject.createWithDefaultObjectsAndDiagramHelp(projectFile, new NullProgressMeter());
			BaseId createdId = realProject.createObjectAndReturnId(ObjectType.RATING_CRITERION);
			
			FactorId threatId = new FactorId(283);
			FactorId targetId = new FactorId(983);
			SimpleThreatRatingFramework realFramework = realProject.getSimpleThreatRatingFramework();
			ThreatRatingBundle bundle = realFramework.getBundle(threatId, targetId);
			bundle.setValueId(createdId, new BaseId(838));
			IdList realOptionIds = realFramework.getValueOptionIds();
			realFramework.saveBundle(bundle);
			ProjectSaver.saveProject(realProject, projectFile);
			realProject.close();

			Project loadedProject = new Project();
			ProjectLoader.loadProject(projectFile, loadedProject);
			IdList loadedOptionIds = loadedProject.getSimpleThreatRatingFramework().getValueOptionIds();
			SimpleThreatRatingFramework loadedFramework = loadedProject.getSimpleThreatRatingFramework();
			assertEquals("didn't reload framework?", createdId, loadedFramework.getCriterion(createdId).getId());
			ThreatRatingBundle gotBundle = loadedProject.getSimpleThreatRatingFramework().getBundle(threatId, targetId);
			assertEquals("didn't load bundles?", bundle.getValueId(createdId), gotBundle.getValueId(createdId));
			assertEquals("didn't load options?", realOptionIds, loadedOptionIds);
			loadedProject.close();
		}
		finally
		{
			DirectoryUtils.deleteEntireDirectoryTree(tempDir);
		}
	}
	
	public void testGetBundleValue()
	{
		BaseId noneId = framework.findValueOptionByNumericValue(0).getId();
		ThreatRatingBundle bundle = new ThreatRatingBundle(new FactorId(1), new FactorId(2), noneId);
		ValueOption result = framework.getBundleValue(bundle);
		assertEquals("didn't default correctly? ", 0, result.getNumericValue());
	}

	public void testRatingValueOptions() throws Exception
	{
		ValueOption[] options = framework.getValueOptions();
		assertEquals("wrong number of default options?", 5, options.length);
		// NOTE: options are: [0]:4/VH, [1]:3/H, [2]:2/M, [3]:1/L, [4]:0/NONE 
		ValueOption veryHigh = findValueOptionByNumeric(options, 4);
		ValueOption high = findValueOptionByNumeric(options, 3);
		ValueOption medium = findValueOptionByNumeric(options, 2);
		assertEquals("wrong order or label?", "Very High", veryHigh.getLabel());
		assertEquals("wrong numeric value? ", 3, high.getNumericValue());
		assertEquals("bad color?", ColorManager.LIGHT_GREEN, medium.getColor());
	}
	
	private ValueOption findValueOptionByNumeric(ValueOption[] options, int lookFor)
	{
		for(ValueOption valueOption : options)
		{
			if(valueOption.getNumericValue() == lookFor)
				return valueOption;
		}
		return null;
	}

	public void testFindValueOptionByNumericValue()
	{
		ValueOption optionNone = framework.findValueOptionByNumericValue(0);
		assertEquals(Color.WHITE, optionNone.getColor());
	}

	public void testDefaultValue() throws Exception
	{
		BaseId id = framework.getDefaultValueId();
		ValueOption option = framework.getValueOption(id);
		assertEquals("Didn't default to zero?", 0, option.getNumericValue());
	}
	
	public void testThreatRatingCriteria() throws Exception
	{
		RatingCriterion[] criteria = framework.getCriteria();
		assertEquals("wrong number of default criteria?", 3, criteria.length);
		List expectedLabels = Arrays.asList(new String[] {"Scope", "Severity", "Irreversibility", });
		for(int i = 0; i < criteria.length; ++i)
		{
			String actual = criteria[i].getLabel();
			assertContains("Missing a criterion?", actual, expectedLabels);
		}
	}
	
	public void testIdAssignment() throws Exception
	{
		assertNotEquals("reused ids?", framework.getCriteria()[0].getId(), framework.getValueOptions()[0].getId());
	}
	
	public void testBundlesForDeletedNodes() throws Exception
	{
		ThreatRatingBundle bundle1 = createThreatTargetAndBundle(project, framework);
		BaseObject object1 = project.findObject(new ORef(ObjectType.CAUSE, bundle1.getThreatId()));
		project.deleteObject(object1);
		assertFalse("deleted threatId case failed?", framework.isBundleForLinkedThreatAndTarget(bundle1));
		
		ThreatRatingBundle bundle2 = createThreatTargetAndBundle(project, framework);
		BaseObject object2 = project.findObject(new ORef(ObjectType.TARGET, bundle2.getTargetId()));
		project.deleteObject(object2);
		assertFalse("deleted targetId case failed?", framework.isBundleForLinkedThreatAndTarget(bundle2));
		
	}

	public static ThreatRatingBundle createThreatTargetAndBundle(ProjectForTesting projectToUse, SimpleThreatRatingFramework frameworkToUse) throws Exception
	{
		FactorId threatId = createThreat(projectToUse).getWrappedId();
		FactorId targetId = createTarget(projectToUse).getWrappedId();
		populateBundle(frameworkToUse, threatId, targetId, frameworkToUse.getValueOptions()[0]);
		ThreatRatingBundle bundle = frameworkToUse.getBundle(threatId, targetId);
		assertFalse("normal case failed?", frameworkToUse.isBundleForLinkedThreatAndTarget(bundle));
		return bundle;
	}
	
	public void testBundles() throws Exception
	{
		FactorId threatId = new FactorId(77);
		FactorId targetId = new FactorId(292);
		BaseId criterionId = new BaseId(22);
		BaseId valueId = new BaseId(639);
		
		ThreatRatingBundle bundle = framework.getBundle(threatId, targetId);
		
		bundle.setValueId(criterionId, valueId);
		ThreatRatingBundle reGot = framework.getBundle(threatId, targetId);
		assertEquals("did't get same bundle?", bundle.getValueId(criterionId), reGot.getValueId(criterionId));
	}
	
	public void testGetThreatRatingSummary() throws Exception
	{
		DiagramFactor threat1 = createThreat(project);
		DiagramFactor threat2 = createThreat(project);
		DiagramFactor target1 = createTarget(project);
		DiagramFactor target2 = createTarget(project);

		ValueOption none = framework.findValueOptionByNumericValue(0);
		ValueOption high = framework.findValueOptionByNumericValue(3);
		ValueOption veryHigh = framework.findValueOptionByNumericValue(4);
		
		assertEquals("threat1 not none?", none, framework.getThreatThreatRatingValue(threat1.getWrappedId()));
		assertEquals("threat2 not none?", none, framework.getThreatThreatRatingValue(threat2.getWrappedId()));
		assertEquals("target1 not none?", none, framework.getTargetThreatRatingValue(target1.getWrappedId()));
		assertEquals("target2 not none?", none, framework.getTargetThreatRatingValue(target2.getWrappedId()));

		createLinkageAndBundle(project, threat1, target1, veryHigh);
		createLinkageAndBundle(project, threat1, target2, veryHigh);
		assertEquals("target1 not high?", high, framework.getTargetThreatRatingValue(target1.getWrappedId()));
		createLinkageAndBundle(project, threat2, target1, veryHigh);
		assertEquals("threat2 not high?", high, framework.getThreatThreatRatingValue(threat2.getWrappedId()));
		createLinkageAndBundle(project, threat2, target2, veryHigh);
		
		assertEquals("threat1 not very high?", veryHigh, framework.getThreatThreatRatingValue(threat1.getWrappedId()));
		assertEquals("threat2 not very high?", veryHigh, framework.getThreatThreatRatingValue(threat2.getWrappedId()));
		assertEquals("target1 not very high?", veryHigh, framework.getTargetThreatRatingValue(target1.getWrappedId()));
		assertEquals("target2 not very high?", veryHigh, framework.getTargetThreatRatingValue(target2.getWrappedId()));
	}
	
	public static void createLinkageAndBundle(ProjectForTesting projectToUse, DiagramFactor threat, DiagramFactor target, ValueOption value) throws Exception
	{
		createDiagramLink(projectToUse, threat, target);
		
		populateBundle(projectToUse.getSimpleThreatRatingFramework(), threat.getWrappedId(), target.getWrappedId(), value);
	}

	private static DiagramLink createDiagramLink(ProjectForTesting projectToUse, DiagramFactor threat, DiagramFactor target) throws Exception
	{
		LinkCreator creator = new LinkCreator(projectToUse);
		return creator.createFactorLinkAndAddToDiagramUsingCommands(projectToUse.getTestingDiagramObject(), threat, target);
	}
	
	public void testGetThreatRatingSummaryUnlinked() throws Exception
	{
		DiagramFactor threat = createThreat(project);
		DiagramFactor target = createTarget(project);
		FactorId threatId = threat.getWrappedId();
		FactorId targetId = target.getWrappedId();
		ValueOption none = framework.findValueOptionByNumericValue(0);
		ValueOption high = framework.findValueOptionByNumericValue(3);
		ValueOption veryHigh = framework.findValueOptionByNumericValue(4);

		populateBundle(framework, threatId, targetId, veryHigh);
		assertEquals("included unlinked bundle in threat value?", none, framework.getThreatThreatRatingValue(threatId));
		assertEquals("included unlinked bundle in target value?", none, framework.getTargetThreatRatingValue(targetId));
		DiagramLink createdDiagramLink = createDiagramLink(project, threat, target);
		ORef factorLinkRef = createdDiagramLink.getWrappedRef();
		
		assertEquals("linking didn't include value for threat?", high, framework.getThreatThreatRatingValue(threatId));
		assertEquals("linking didn't include value for target?", high, framework.getTargetThreatRatingValue(targetId));

		DiagramLink diagramLink = project.getTestingDiagramObject().getDiagramLinkByWrappedRef(factorLinkRef);
		LinkDeletor deletor = new LinkDeletor(project);
		deletor.deleteDiagramLinkAndOrphandFactorLink(diagramLink);
		
		assertEquals("threat value included contributing factor?", none, framework.getThreatThreatRatingValue(threatId));
		assertEquals("target value included contributing factor?", none, framework.getTargetThreatRatingValue(targetId));
	}
	
	public void testGetHighestValueForTarget() throws Exception
	{
		int[][] bundleValues = { {3,}, {4,}, };
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			FactorId targetId = trf.getProject().getTargetPool().getSortedTargets()[0].getFactorId();
			assertEquals(4, trf.getHighestValueForTarget(targetId));
		}
		finally
		{
			trf.getProject().close();
		}
	}
	
	public void testGetHighestValueForTargetThatHas357() throws Exception
	{
		int[][] bundleValues = { {3,}, {3,}, {3,}, {2,}, {2,}, {2,}, {2,}, };
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			FactorId targetId = trf.getProject().getTargetPool().getSortedTargets()[0].getFactorId();
			assertEquals(4, trf.getHighestValueForTarget(targetId));
		}
		finally
		{
			trf.getProject().close();
		}
	}
	
	public void testGetPureMajorityProjectRating() throws Exception
	{
		int[][] bundlesEmpty = { {-1} };
		verifyPureMajority("Empty", 0, bundlesEmpty);
		
		int[][] bundlesPlurality = { {2, 3, 4, 2, 3, 4, 2},	};
		verifyPureMajority("Plurality", 3, bundlesPlurality);
		
		int[][] bundlesMajority = { {2, 2, 1},	};
		verifyPureMajority("Majority", 2, bundlesMajority);

		int[][] bundlesTwoRows = {
			{1, 4, 1, },
			{4, 1, 4, },
		};
		verifyPureMajority("TwoRows", 4, bundlesTwoRows);
		
		int[][] bundles = { {3, 0},	};
		verifyPureMajority("wrong target majority", 3, bundles);
	}
	
	public void testGetProjectRating() throws Exception
	{
		int[][] bundlesEmpty = { {-1} };
		verifyOverallProjectRating("Empty", 0, bundlesEmpty);
		
		int[][] bundlesPlurality = { {4, 4, 1, 1},	};
		verifyOverallProjectRating("Plurality", 3, bundlesPlurality);
		
		int[][] bundlesMajority = { {3, 3, 1},	};
		verifyOverallProjectRating("Majority", 3, bundlesMajority);
		
	}
	
	private void verifyPureMajority(String message, int expected, int[][] bundleValues) throws Exception
	{
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			assertEquals(message, expected, trf.getProjectMajorityRating().getNumericValue());
		}
		finally
		{
			trf.getProject().close();
		}
	}

	private void verifyOverallProjectRating(String message, int expected, int[][] bundleValues) throws Exception
	{
		SimpleThreatRatingFramework trf = createFramework(bundleValues);
		try
		{
			assertEquals(message, expected, trf.getOverallProjectRating().getNumericValue());
		}
		finally
		{
			trf.getProject().close();
		}
	}

	private static DiagramFactor createTarget(ProjectForTesting projectToUse) throws Exception
	{
		DiagramFactor targetDiagramFactor = projectToUse.createDiagramFactorAndAddToDiagram(ObjectType.TARGET);
		projectToUse.setObjectData(targetDiagramFactor.getWrappedORef(), Target.TAG_LABEL, targetDiagramFactor.getWrappedId().toString());
		
		return targetDiagramFactor;
	}

	private static DiagramFactor createThreat(ProjectForTesting projectToUse) throws Exception
	{
		DiagramFactor threatDiagramFactor = projectToUse.createDiagramFactorAndAddToDiagram(ObjectType.CAUSE);
		projectToUse.enableAsThreat(threatDiagramFactor.getWrappedORef());
		
		return threatDiagramFactor;
	}
	
	public static void populateBundle(SimpleThreatRatingFramework frameworkToUse, FactorId threatId, FactorId targetId, ValueOption value) throws Exception
	{
		ThreatRatingBundle bundle = frameworkToUse.getBundle(threatId, targetId);
		RatingCriterion criteria[] = frameworkToUse.getCriteria();
		for(int i = 0; i < criteria.length; ++i)
			bundle.setValueId(criteria[i].getId(), value.getId());
	}
	
	private SimpleThreatRatingFramework createFramework(int[][] bundleValues) throws Exception
	{
		ProjectForTesting tempProject = ProjectForTesting.createProjectWithDefaultObjects(getName());
		
		return fillFrameWork(tempProject, bundleValues);
	}

	public static SimpleThreatRatingFramework fillFrameWork(ProjectForTesting projectToUse, int[][] bundleValues) throws Exception
	{
		SimpleThreatRatingFramework trf = projectToUse.getSimpleThreatRatingFramework();
		int threatCount = bundleValues.length;
		DiagramFactor[] threats = new DiagramFactor[threatCount];
		for(int i = 0; i < threatCount; ++i)
			threats[i] = createThreat(projectToUse);
		
		int targetCount = bundleValues[0].length;
		DiagramFactor[] targets = new DiagramFactor[targetCount];
		for(int i = 0; i < targetCount; ++i)
			targets[i] = createTarget(projectToUse);

		for(int threatIndex = 0; threatIndex < threatCount; ++threatIndex)
		{
			int[] valuesForThreat = bundleValues[threatIndex];
			for(int targetIndex = 0; targetIndex < targetCount; ++targetIndex)
			{
				int numericValue = valuesForThreat[targetIndex];
				if(numericValue < 0)
					continue;
				ValueOption valueOption = trf.findValueOptionByNumericValue(numericValue);
				createLinkageAndBundle(projectToUse, threats[threatIndex], targets[targetIndex], valueOption);				
			}
		}
		return trf;
	}
	
	private SimpleThreatRatingFramework framework;
	private ProjectForTesting project;
}
