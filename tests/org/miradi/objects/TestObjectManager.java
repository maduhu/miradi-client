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
package org.miradi.objects;

import java.io.File;
import java.io.IOException;

import org.martus.util.DirectoryUtils;
import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.ids.KeyEcologicalAttributeId;
import org.miradi.main.EAM;
import org.miradi.main.MiradiTestCase;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ORefSet;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.project.ObjectManager;
import org.miradi.project.Project;
import org.miradi.project.ProjectForTesting;
import org.miradi.project.ProjectLoader;
import org.miradi.project.ProjectSaver;
import org.miradi.questions.HabitatAssociationQuestion;
import org.miradi.questions.KeyEcologicalAttributeTypeQuestion;
import org.miradi.questions.ViabilityModeQuestion;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.KeyEcologicalAttributeSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.utils.CodeList;
import org.miradi.utils.NullProgressMeter;


public class TestObjectManager extends MiradiTestCase
{
	public TestObjectManager(String name)
	{
		super(name);
	}

	@Override
	public void setUp() throws Exception
	{
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		manager = project.getObjectManager();		
	}
	
	@Override
	public void tearDown() throws Exception
	{
		project.close();
		project = null;
	}

	public void testCachingInvalidRefs() throws Exception
	{
		assertEquals("Already have invalid refs?", 0, manager.getReferringObjects(ORef.INVALID).size());
		ORefSet containsInvalid = new ORefSet(ORef.INVALID);
		manager.updateReferrerCache(project.getMetadata().getRef(), new ORefSet(), containsInvalid);
		assertEquals("Now cached an invalid refs?", 0, manager.getReferringObjects(ORef.INVALID).size());
	}

	public void testObjectLifecycles() throws Exception
	{
		int[] representativeSampleOfTypes = new int[] {
			ObjectType.RATING_CRITERION, 
			ObjectType.VALUE_OPTION,  
			ObjectType.VIEW_DATA, 
			ObjectType.PROJECT_RESOURCE,
			ObjectType.INDICATOR,
			ObjectType.OBJECTIVE,
			ObjectType.GOAL,
			ObjectType.PROJECT_METADATA,
			ObjectType.CAUSE,
		};
		
		for(int i = 0; i < representativeSampleOfTypes.length; ++i)
		{
			verifyObjectLifecycle(representativeSampleOfTypes[i]);
		}
	}
	
	public void testPseudoQuestionField() throws Exception
	{
		Target target = getProject().createTarget();
		getProject().fillObjectUsingCommand(target, Target.TAG_HABITAT_ASSOCIATION, new CodeList(new String[]{HabitatAssociationQuestion.SAVANNA_CODE}).toString());
		final String pseudoValue = target.getSchema().getFieldSchema(Target.PSEUDO_TAG_HABITAT_ASSOCIATION_VALUE).createField(target).get();
		assertEquals("Habitat code not retrieved from pseudo field?", EAM.text("Savanna"), pseudoValue);
	}
	
	public void testPseudoReflistField() throws Exception
	{
		DiagramFactor diagramFactor = getProject().createAndAddFactorToDiagram(StrategySchema.getObjectType());
		Strategy strategy = Strategy.find(getProject(), diagramFactor.getWrappedORef());
		getProject().turnOnDraft(strategy);
		
		DiagramObject diagramObject = getProject().getTestingDiagramObject();
		final String pseudoValue = diagramObject.getSchema().getFieldSchema(ConceptualModelDiagram.PSEUDO_DRAFT_STRATEGY_REFS).createField(diagramObject).get();
		assertEquals("Incorrect pseudo reflist of draft strategy refs?", new ORefList(strategy).toString(), pseudoValue);		
	}

	public void testPseudoTagTargetViability() throws Exception
	{
		String NOT_SPECIFIED = "";
		String FAIR = "2";
		String sampleStatusCode = FAIR;

		ORef targetRef = project.createObject(ObjectType.TARGET);
		Target target = Target.find(project, targetRef);
		target.setData(Target.TAG_TARGET_STATUS, sampleStatusCode);
		
		String simple = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Didn't return simple viability?", sampleStatusCode, simple);
		
		target.setData(Target.TAG_VIABILITY_MODE, ViabilityModeQuestion.TNC_STYLE_CODE);
		String notRated = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Didn't return detailed viability?", NOT_SPECIFIED, notRated);
		
		Indicator condition1Indicator = createIndicator(FAIR);
		KeyEcologicalAttribute conditionKea = createKEA(new Indicator[] {condition1Indicator});

		IdList keas = new IdList(KeyEcologicalAttributeSchema.getObjectType());
		keas.add(conditionKea.id);
		target.setData(Target.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS, keas.toString());

		String keaWithoutCategory = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Included uncategorized KEA?", NOT_SPECIFIED, keaWithoutCategory);

		conditionKea.setData(KeyEcologicalAttribute.TAG_KEY_ECOLOGICAL_ATTRIBUTE_TYPE, KeyEcologicalAttributeTypeQuestion.CONDITION);
		String fair = project.getObjectData(target.getRef(), Target.PSEUDO_TAG_TARGET_VIABILITY);
		assertEquals("Didn't compute for one kea one indicator?", FAIR, fair);
		
	}

	private Indicator createIndicator(String status) throws Exception
	{
		ORef indicatorRef = project.createObject(IndicatorSchema.getObjectType());
		ORef measurementRef = project.createObject(MeasurementSchema.getObjectType());
		project.setObjectData(measurementRef, Measurement.TAG_STATUS, status);
		ORefList measurementRefs = new ORefList();
		measurementRefs.add(measurementRef);
		
		project.setObjectData(indicatorRef, Indicator.TAG_MEASUREMENT_REFS, measurementRefs.toString());
		return (Indicator)project.findObject(indicatorRef);
	}

	private KeyEcologicalAttribute createKEA(Indicator[] indicators) throws Exception
	{
		KeyEcologicalAttributeId keaId1 = (KeyEcologicalAttributeId)project.createObjectAndReturnId(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE);
		KeyEcologicalAttribute kea = (KeyEcologicalAttribute)project.findObject(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keaId1);

		IdList indicatorIds = new IdList(IndicatorSchema.getObjectType());
		for(int i = 0; i < indicators.length; ++i)
			indicatorIds.add(indicators[i].getId());
		kea.setData(KeyEcologicalAttribute.TAG_INDICATOR_IDS, indicatorIds.toString());
		
		return kea;
	}
	
	public void testComputeTNCViabilityOfKEA() throws Exception
	{
		String FAIR = "2";
		String GOOD = "3";
		String VERY_GOOD = "4";

		Indicator fair = createIndicator(FAIR);
		Indicator veryGood = createIndicator(VERY_GOOD);
		KeyEcologicalAttribute kea = createKEA(new Indicator[] {fair, veryGood});
		assertEquals(GOOD, kea.getData(KeyEcologicalAttribute.PSEUDO_TAG_VIABILITY_STATUS));
	}
	
	private void verifyObjectLifecycle(int type) throws Exception
	{
		BaseId createdId = manager.createObject(type, BaseId.INVALID);
		verifyBasicObjectLifecycle(type, createdId);
		verifyObjectWriteAndRead(type);
		verifyGetPool(type, createdId);
	}
	
	public void verifyBasicObjectLifecycle(int type, BaseId createdId) throws Exception
	{
		assertNotEquals(type + " Created with invalid id", BaseId.INVALID, createdId);
		assertNotNull(manager.findObject(type, createdId));
		
		String tag = BaseObject.TAG_LABEL;
		manager.setObjectData(new ORef(type, createdId), tag, "data");
		BaseObject withData = manager.findObject(type, createdId);
		assertEquals(type + " didn't write/read data for " + tag + "?", "data", withData.getData(tag));
		assertEquals(type + " can't get data from project?", "data", manager.getObjectData(type, createdId, tag));
		
		manager.deleteObject(withData.getRef());
		try
		{
			manager.getObjectData(type, createdId, tag);
			fail(type + " Should have thrown getting data from deleted object");
		}
		catch(Exception ignoreExpected)
		{
		}
		
		assertNull(manager.findObject(type, createdId));
		
		BaseId desiredId = new BaseId(2323);
		assertEquals(type + " didn't use requested id?", desiredId, manager.createObject(type, desiredId));
	}

	private void verifyObjectWriteAndRead(int type) throws IOException, Exception
	{
		File tempDirectory = createTempDirectory();
		File projectFile = new File(tempDirectory, getName());
		try
		{
			Project projectToWrite = new Project();
			projectToWrite.createWithDefaultObjectsAndDiagramHelp(projectFile, new NullProgressMeter());
			BaseId idToReload = projectToWrite.createObjectAndReturnId(type, BaseId.INVALID);
			ProjectSaver.saveProject(projectToWrite, projectFile);
			projectToWrite.close();
			
			Project projectToRead = new Project();
			ProjectLoader.loadProject(projectFile, projectToRead);
			try
			{
				projectToRead.getObjectData(type, idToReload, BaseObject.TAG_LABEL);
			}
			catch (NullPointerException e)
			{
				fail("Didn't reload object from disk, type: " + type + " (did the pool get loaded?)");
			}
			projectToRead.close();
		}
		finally
		{
			DirectoryUtils.deleteEntireDirectoryTree(tempDirectory);
		}
	}
	
	private void verifyGetPool(int type, BaseId createdId) throws Exception
	{
		EAMObjectPool pool = manager.getPool(type);
		assertNotNull("Missing pool type " + type, pool);
		BaseObject created = (BaseObject)pool.getRawObject(createdId);
		assertNotNull("Pool doesn't have object type " + created);
	}
	
	private ProjectForTesting getProject()
	{
		return project;
	}
	
	ProjectForTesting project;
	ObjectManager manager;
}
