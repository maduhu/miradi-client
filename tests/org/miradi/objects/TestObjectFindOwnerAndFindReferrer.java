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

import org.miradi.ids.BaseId;
import org.miradi.ids.IdList;
import org.miradi.main.MiradiTestCase;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.project.ProjectForTesting;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.FactorLinkSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.MeasurementSchema;
import org.miradi.schemas.ResourceAssignmentSchema;
import org.miradi.schemas.StrategySchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.TaskSchema;

public class TestObjectFindOwnerAndFindReferrer extends MiradiTestCase
{
	public TestObjectFindOwnerAndFindReferrer(String name)
	{
		super(name);
	}

	@Override
	public void setUp() throws Exception
	{
		project = ProjectForTesting.createProjectWithDefaultObjects(getName());
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception
	{
		super.tearDown();
		project.close();
		project = null;
	}
	
	public void testStrongRefererers() throws Exception
	{
		Strategy strategy = getProject().createStrategy();
		verifyNoStrongReferrers(strategy);
		TaggedObjectSet taggedObjectSet = getProject().createTaggedObjectSet();
		getProject().fillObjectUsingCommand(taggedObjectSet, TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, new ORefList(strategy.getRef()).toString());
		verifyNoStrongReferrers(strategy);
	}

	private void verifyNoStrongReferrers(Strategy strategy)
	{
		assertEquals("Strategy should not have any referrers?", 0, strategy.findStrongObjectsThatReferToUs().size());
	}

	public void testIntermediateOwn() throws Exception
	{
		ORef intermediateResultRef = project.createObject(ObjectType.INTERMEDIATE_RESULT);
		BaseId indicatorId = project.addItemToFactorList(intermediateResultRef, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId objectiveId = project.addItemToFactorList(intermediateResultRef, ObjectType.OBJECTIVE, Factor.TAG_OBJECTIVE_IDS);
		
		//----------- start test -----------
		
		verifyOwnershipFunctions(1,intermediateResultRef, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwnershipFunctions(1,intermediateResultRef, new ORef(ObjectType.OBJECTIVE, objectiveId));
	}

	public void testThreatReductionResultOwn() throws Exception
	{
		ORef threatReductionResultRef = project.createObject(ObjectType.THREAT_REDUCTION_RESULT);
		BaseId indicatorId = project.addItemToFactorList(threatReductionResultRef, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId objectiveId = project.addItemToFactorList(threatReductionResultRef, ObjectType.OBJECTIVE, Factor.TAG_OBJECTIVE_IDS);
		
		//----------- start test -----------
		
		verifyOwnershipFunctions(1, threatReductionResultRef, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwnershipFunctions(1, threatReductionResultRef, new ORef(ObjectType.OBJECTIVE, objectiveId));
	}

	public void testCauseOwn() throws Exception
	{
		ORef causeRef = project.createObject(ObjectType.CAUSE);
		BaseId indicatorId = project.addItemToFactorList(causeRef, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId objectiveId = project.addItemToFactorList(causeRef, ObjectType.OBJECTIVE, Factor.TAG_OBJECTIVE_IDS);
		
		//----------- start test -----------
		
		verifyOwnershipFunctions(1, causeRef, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwnershipFunctions(1, causeRef, new ORef(ObjectType.OBJECTIVE, objectiveId));
	}
	
	public void testStrategyOwn() throws Exception
	{
		ORef strategyRef = project.createObject(ObjectType.STRATEGY);
		BaseId indicatorId = project.addItemToFactorList(strategyRef, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId objectiveId = project.addItemToFactorList(strategyRef, ObjectType.OBJECTIVE, Factor.TAG_OBJECTIVE_IDS);
		
		BaseId taskId = project.createTask(Strategy.find(project, strategyRef)).getId();
		IdList taskList = new IdList(TaskSchema.getObjectType(), new BaseId[] {taskId});
		project.setObjectData(strategyRef, Strategy.TAG_ACTIVITY_IDS, taskList.toString());
		
		//----------- start test -----------
		
		verifyOwnershipFunctions(1, strategyRef, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwnershipFunctions(1, strategyRef, new ORef(ObjectType.OBJECTIVE, objectiveId));
		verifyRefer(strategyRef, new ORef(ObjectType.TASK, taskId));
	}

	public void testTargetOwn() throws Exception
	{
		ORef targetRef = project.createObject(ObjectType.TARGET);
		BaseId indicatorId = project.addItemToFactorList(targetRef, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		BaseId goalId = project.addItemToFactorList(targetRef, ObjectType.GOAL, AbstractTarget.TAG_GOAL_IDS);
		BaseId keaId = project.addItemToFactorList(targetRef, ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, AbstractTarget.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS);
		
		//----------- start test -----------
		
		verifyOwnershipFunctions(1, targetRef, new ORef(ObjectType.INDICATOR, indicatorId));
		verifyOwnershipFunctions(1, targetRef, new ORef(ObjectType.GOAL, goalId));
		verifyOwnershipFunctions(1, targetRef, new ORef(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keaId));
	}
	
	public void testTargetInKeaModeWithSimpleIndicatorOwn() throws Exception
	{
		Target target = project.createTarget();
		project.turnOnTncMode(target);
		ORef targetRef = target.getRef();
		BaseId indicatorId = project.addItemToFactorList(targetRef, ObjectType.INDICATOR, Factor.TAG_INDICATOR_IDS);
		
		verifyOwnershipFunctions(1, targetRef, new ORef(ObjectType.INDICATOR, indicatorId));
	}
	
	public void testTaskOwn() throws Exception
	{
		Strategy strategy = project.createStrategy();
		Task activity = project.createTask(strategy);
		Task task = project.createTask(activity);
		ORef assignmentRef = project.createResourceAssignment().getRef();
		
		IdList assignmentList = new IdList(ResourceAssignmentSchema.getObjectType(), new BaseId[] {assignmentRef.getObjectId()});
		project.setObjectData(ObjectType.TASK, task.getId(), Task.TAG_RESOURCE_ASSIGNMENT_IDS, assignmentList.toString());

		//----------- start test -----------
		
		verifyOwnershipFunctions(1,activity.getRef(), task.getRef());
		verifyOwnershipFunctions(1,task.getRef(), assignmentRef);
	}
	
	
	public void testAssignmentRefer() throws Exception
	{
		ORef assignmentRef = project.createResourceAssignment().getRef();
		
		BaseId projectResourceId = project.createObjectAndReturnId(ObjectType.PROJECT_RESOURCE);
		project.setObjectData(assignmentRef, ResourceAssignment.TAG_RESOURCE_ID, projectResourceId.toString());
		
		BaseId accountingCodeId = project.createObjectAndReturnId(ObjectType.ACCOUNTING_CODE);
		project.setObjectData(assignmentRef, ResourceAssignment.TAG_ACCOUNTING_CODE_ID, accountingCodeId.toString());
		
		BaseId fundingSourceId = project.createObjectAndReturnId(ObjectType.FUNDING_SOURCE);
		project.setObjectData(assignmentRef, ResourceAssignment.TAG_FUNDING_SOURCE_ID, fundingSourceId.toString());
		
		//----------- start test -----------
		
		verifyReferenceFunctions(1, assignmentRef, new ORef(ObjectType.PROJECT_RESOURCE, projectResourceId));
		verifyReferenceFunctions(1, assignmentRef, new ORef(ObjectType.ACCOUNTING_CODE, accountingCodeId));
		verifyReferenceFunctions(1, assignmentRef, new ORef(ObjectType.FUNDING_SOURCE, fundingSourceId));
	}


	public void testDiagramFactorRefer() throws Exception
	{
		verifyDiagramReference(ObjectType.CAUSE);
		verifyDiagramReference(ObjectType.STRATEGY);
		verifyDiagramReference(ObjectType.TARGET);
		verifyDiagramReference(ObjectType.INTERMEDIATE_RESULT);
		verifyDiagramReference(ObjectType.THREAT_REDUCTION_RESULT);
		verifyDiagramReference(ObjectType.TEXT_BOX);
		verifyDiagramReference(ObjectType.GROUP_BOX);
	}

	private void verifyDiagramReference(int type) throws Exception
	{
		DiagramFactor diagramFactor = project.createDiagramFactorAndAddToDiagram(type);
		ORef orefFactor = diagramFactor.getReferencedObjects(type).get(0);
		
		//----------- start test -----------
		
		verifyReferenceFunctions(1, diagramFactor.getRef(), orefFactor);
	}
	
	public void testDiagramFactorLinkAndLinkFactorRefer() throws Exception
	{
		verifyDiagramFactorLinkAndLinkFactorRefer(ObjectType.STRATEGY, ObjectType.CAUSE);
		verifyDiagramFactorLinkAndLinkFactorRefer(ObjectType.CAUSE, ObjectType.TARGET);
		verifyDiagramFactorLinkAndLinkFactorRefer(ObjectType.INTERMEDIATE_RESULT, ObjectType.THREAT_REDUCTION_RESULT);
	}
	
	public void verifyDiagramFactorLinkAndLinkFactorRefer(int fromType, int toType) throws Exception
	{
		DiagramFactor from = project.createDiagramFactorAndAddToDiagram(fromType);
		DiagramFactor to = project.createDiagramFactorAndAddToDiagram(toType);

		ORef diagramLinkRef = project.createDiagramLink(from, to);
		DiagramLink diagramLink = (DiagramLink) project.findObject(diagramLinkRef);
		
		//----------- start test -----------

		verifyReferenceFunctions(1, diagramLink.getWrappedRef(), from.getWrappedORef());
		verifyReferenceFunctions(1, diagramLink.getWrappedRef(), to.getWrappedORef());
    	
		verifyReferenceFunctions(2, diagramLinkRef, to.getRef());
		verifyReferenceFunctions(2, diagramLinkRef, from.getRef());
		verifyReferenceFunctions(1, diagramLinkRef, diagramLink.getWrappedRef());
	}
	
	public void testIndicatorOwn() throws Exception
	{
		ORef indicatorRef = project.createObject(IndicatorSchema.getObjectType());
	
		ORef taskRef = project.createObject(TaskSchema.getObjectType());
		IdList taskList = new IdList(TaskSchema.getObjectType(), new BaseId[] {taskRef.getObjectId()});
		project.setObjectData(indicatorRef, Indicator.TAG_METHOD_IDS, taskList.toString());
		
		ORef measurementRef = project.createObject(MeasurementSchema.getObjectType());
		ORefList measurementList = new ORefList(new ORef[] {measurementRef});
		project.setObjectData(indicatorRef, Indicator.TAG_MEASUREMENT_REFS, measurementList.toString());
		
		//----------- start test -----------
		
		verifyRefer(indicatorRef, taskRef);
		verifyRefer(indicatorRef, measurementRef);
	}
	
	
	public void testKeyEcologicalAttributeOwn() throws Exception
	{
		BaseId keaId = project.createObjectAndReturnId(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE);
		BaseId indicatorId = project.addItemToKeyEcologicalAttributeList(keaId, ObjectType.INDICATOR, KeyEcologicalAttribute.TAG_INDICATOR_IDS);
		
		//----------- start test -----------
		
		ORef owner = new ORef(ObjectType.KEY_ECOLOGICAL_ATTRIBUTE, keaId);
		verifyOwnershipFunctions(1,owner, new ORef(ObjectType.INDICATOR, indicatorId));
	}
	
	
	public void testVeiwDataRefer() throws Exception
	{
		BaseId viewDataId = project.createObjectAndReturnId(ObjectType.VIEW_DATA);
		BaseId factorId = project.createFactorAndReturnId(ObjectType.TARGET);
		ORefList oRefList = new ORefList(new ORef[] {new ORef(ObjectType.TARGET, factorId)});
		project.setObjectData(ObjectType.VIEW_DATA, viewDataId, ViewData.TAG_CHAIN_MODE_FACTOR_REFS, oRefList.toString());
		
		//----------- start test -----------
		
		ORef owner = new ORef(ObjectType.VIEW_DATA, viewDataId);
		verifyReferenceFunctions(1,owner, new ORef(ObjectType.TARGET, factorId));
	}
	
	public void testResultsChainRefer() throws Exception
	{
		BaseId resultsChainId = project.createObjectAndReturnId(ObjectType.RESULTS_CHAIN_DIAGRAM);
		ORef diagramRef = new ORef(ObjectType.RESULTS_CHAIN_DIAGRAM, resultsChainId);

		DiagramFactor strategy = createFactorAndDiagramFactor(StrategySchema.getObjectType());
		DiagramFactor cause = createFactorAndDiagramFactor(CauseSchema.getObjectType());
		DiagramFactor target = createFactorAndDiagramFactor(TargetSchema.getObjectType());
		IdList factorList = new IdList(DiagramFactorSchema.getObjectType(), new BaseId[] {strategy.getId(), cause.getId(), target.getId()});
		
		DiagramLink link = createDiagramFactorLink(strategy, cause);
		IdList linkList = new IdList(DiagramLinkSchema.getObjectType(), new BaseId[] {link.getId()});
		project.setObjectData(diagramRef, ResultsChainDiagram.TAG_DIAGRAM_FACTOR_IDS, factorList.toString());
		project.setObjectData(diagramRef, ResultsChainDiagram.TAG_DIAGRAM_FACTOR_LINK_IDS, linkList.toString());
		
		//----------- start test -----------
		
		verifyOwnershipFunctions(3,diagramRef, strategy.getRef());
		verifyOwnershipFunctions(3,diagramRef, cause.getRef());
		verifyOwnershipFunctions(3,diagramRef, target.getRef());
		verifyOwnershipFunctions(1,diagramRef, link.getRef());
	}
	
	private DiagramFactor createFactorAndDiagramFactor(int type) throws Exception
	{
		ORef factorRef = project.createObject(type);
		ORef diagramFactorRef = project.createObject(DiagramFactorSchema.getObjectType());
		project.setObjectData(diagramFactorRef, DiagramFactor.TAG_WRAPPED_REF, factorRef.toString());
		
		return DiagramFactor.find(project, diagramFactorRef);
	}
	
	private DiagramLink createDiagramFactorLink(DiagramFactor from, DiagramFactor to) throws Exception
	{
		BaseId linkId = project.createObjectAndReturnId(FactorLinkSchema.getObjectType());
		
		ORef factorLinkRef = new ORef(FactorLinkSchema.getObjectType(), linkId);
		project.setObjectData(factorLinkRef, FactorLink.TAG_FROM_REF, from.getWrappedORef().toString());
		project.setObjectData(factorLinkRef, FactorLink.TAG_TO_REF, to.getWrappedORef().toString());
		
		ORef diagramLinkRef = project.createObject(DiagramLinkSchema.getObjectType());
		project.setObjectData(diagramLinkRef, DiagramLink.TAG_WRAPPED_ID, linkId.toString());
    	project.setObjectData(diagramLinkRef, DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, from.getId().toString());
    	project.setObjectData(diagramLinkRef, DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, to.getId().toString());
		
		return (DiagramLink)project.findObject(diagramLinkRef);
	}
	

	private void verifyRefer(ORef referrer, ORef referred)
	{
		ORefList foundReferrers1 = BaseObject.findObjectsThatReferToUs(project.getObjectManager(), referrer.getObjectType(), referred);
		assertEquals(1,foundReferrers1.size());
		assertEquals(referrer.getObjectId(), foundReferrers1.get(0).getObjectId());

		BaseObject referredObject =  project.getObjectManager().findObject(referred);
		ORefList foundReferrers2 = referredObject.findAllObjectsThatReferToUs();
		assertContains(referrer, foundReferrers2.toArray());
	}	
	
	private void verifyReferenced(int size, ORef referrer, ORef referred)
	{
		BaseObject refererObject = project.findObject(referrer);
		ORefList ownedObjects = refererObject.getReferencedObjects(referred.getObjectType());
		assertEquals(size, ownedObjects.size());
		assertEquals(true, ownedObjects.contains(referred));
	}
	
	
	private void verifyReferenceFunctions(int size, ORef owner, ORef ref)
	{
		verifyReferenced(size, owner, ref);
		verifyRefer(owner, ref);
	}

	

	private void verifyOwner(ORef owner, ORef ref)
	{
		BaseObject object = BaseObject.find(project.getObjectManager(), ref);
		assertEquals(owner, object.getOwnerRef());
	}
	
	private void verifyOwned(int size, ORef owner, ORef ref)
	{
		BaseObject ownerObject = project.findObject(owner);
		ORefList ownedObjects = ownerObject.getOwnedObjectRefs().getFilteredBy(ref.getObjectType());
		assertEquals(size, ownedObjects.size());
		assertEquals(true, ownedObjects.contains(ref));
	}
	
	private void verifyOwnershipFunctions(int size, ORef owner, ORef ref)
	{
		verifyOwned(size, owner, ref);
		verifyOwner(owner, ref);
	}

	private ProjectForTesting getProject()
	{
		return project;
	}
	
	ProjectForTesting project;
}
