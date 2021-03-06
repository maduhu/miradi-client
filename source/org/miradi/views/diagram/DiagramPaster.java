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
package org.miradi.views.diagram;

import java.awt.Point;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import org.miradi.commands.Command;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.cells.EAMGraphCell;
import org.miradi.diagram.cells.FactorCell;
import org.miradi.diagram.cells.LinkCell;
import org.miradi.dialogs.diagram.DiagramPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramFactorId;
import org.miradi.ids.IdList;
import org.miradi.main.AbstractTransferableMiradiList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.*;
import org.miradi.objects.*;
import org.miradi.project.Project;
import org.miradi.schemas.*;
import org.miradi.utils.CodeList;
import org.miradi.utils.CommandVector;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.utils.PointList;

abstract public class DiagramPaster
{
	public DiagramPaster(DiagramPanel diagramPanelToUse, DiagramModel modelToUse, AbstractTransferableMiradiList transferableListToUse)
	{
		diagramPanel = diagramPanelToUse;
		currentModel = modelToUse;
		project = currentModel.getProject();
		transferableList = transferableListToUse;
		oldToNewPastedObjectMap = new HashMap<ORef, ORef>();
        doesPasteDiscardInvalidTaxonomyClassificationList = false;

		factorDeepCopies = transferableList.getFactorDeepCopies();
		diagramFactorDeepCopies = transferableList.getDiagramFactorDeepCopies();
		threatStressRatings = transferableListToUse.getThreatStressRatingDeepCopies();
		factorLinkDeepCopies = transferableList.getFactorLinkDeepCopies();
		diagramLinkDeepCopies = transferableList.getDiagramLinkDeepCopies();
		pastedCellsToSelect = new Vector<EAMGraphCell>();
    }
	
	protected Vector<String> getFactorDeepCopies()
	{
		return factorDeepCopies;
	}
	
	protected DiagramModel getDiagramModel()
	{
		return currentModel;
	}
	
	protected void selectNewlyPastedItems()
	{
		//NOTE if-test only exists for tests
		if (diagramPanel == null)
			return;
		diagramPanel.clearSelection();
		EAMGraphCell[] cellsToSelect = pastedCellsToSelect.toArray(new EAMGraphCell[0]);  
		diagramPanel.selectCells(cellsToSelect);
	}

	private String getClipboardProjectFileName()
	{
		return transferableList.getProjectFileName();
	}

	private void fixupRefs(HashMap pastedObjectMap, BaseObject newObject) throws Exception
	{
		Command[] commandsToFixRefs = createCommandToFixupRefLists(pastedObjectMap, newObject);
		getProject().executeCommands(commandsToFixRefs);
	}
	
	public Command[] createCommandToFixupRefLists(HashMap pastedObjectMap, BaseObject newObject) throws Exception
	{
		CommandVector commands = new CommandVector();
		String[] fieldTags = newObject.getFieldTags();
		for (int i = 0; i < fieldTags.length; ++i)
		{
			String tag = fieldTags[i];
			commands.addAll(Arrays.asList(getCommandsToFixUpIdListRefs(pastedObjectMap, newObject, tag)));
			commands.addAll(Arrays.asList(getCommandsToFixUpORefList(pastedObjectMap, newObject, tag)));
			commands.addAll(Arrays.asList(getCommandToFixUpIdRefs(pastedObjectMap,newObject, tag)));
		}
		
		return commands.toArray(new Command[0]);
	}
	
	private Command[] getCommandsToFixUpIdListRefs(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		if (!newObject.isIdListTag(tag))
			return new Command[0];
		
		Command commandToFixRefs = fixUpIdList(pastedObjectMap, newObject, tag, newObject.getAnnotationType(tag));
		return new Command[] {commandToFixRefs};
	}
	
	private Command[] getCommandsToFixUpORefList(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		if (!newObject.isRefList(tag) || newObject.isPseudoField(tag))
			return new Command[0];
		
		Command commandToFixRefs = fixUpRefList(pastedObjectMap, newObject, tag);
		return new Command[] {commandToFixRefs};
	}
	
	private Command[] getCommandToFixUpIdRefs(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		if (ResourceAssignmentSchema.getObjectType() == newObject.getType())
		{
			if (ResourceAssignment.TAG_ACCOUNTING_CODE_ID.equals(tag))
				return getCommandToFixId(pastedObjectMap, newObject, AccountingCodeSchema.getObjectType(), tag);

			if (ResourceAssignment.TAG_FUNDING_SOURCE_ID.equals(tag))
				return getCommandToFixId(pastedObjectMap, newObject, FundingSourceSchema.getObjectType(), tag);

			if (ResourceAssignment.TAG_RESOURCE_ID.equals(tag))
				return getCommandToFixId(pastedObjectMap, newObject, ProjectResourceSchema.getObjectType(), tag);

			if (ResourceAssignment.TAG_CATEGORY_ONE_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);

			if (ResourceAssignment.TAG_CATEGORY_TWO_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}

		if (ExpenseAssignment.is(newObject))
		{
			if (ExpenseAssignment.TAG_ACCOUNTING_CODE_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
			
			if (ExpenseAssignment.TAG_FUNDING_SOURCE_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
			
			if (ExpenseAssignment.TAG_CATEGORY_ONE_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
			
			if (ExpenseAssignment.TAG_CATEGORY_TWO_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}
				
		if (ThreatStressRatingSchema.getObjectType() == newObject.getType())
		{
			if (ThreatStressRating.TAG_STRESS_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
				
			if (ThreatStressRating.TAG_THREAT_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}
		
		if (DiagramFactorSchema.getObjectType() == newObject.getType())
		{
			if (DiagramFactor.TAG_WRAPPED_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}
		
		if (ThreatReductionResult.is(newObject))
		{
			if (ThreatReductionResult.TAG_RELATED_DIRECT_THREAT_REF.equals(tag))
				return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}
		
		if (tag.equals(BaseObject.TAG_ASSIGNED_LEADER_RESOURCE))
		{
			return getCommandToFixRef(pastedObjectMap, newObject, tag);
		}

		return new Command[0];
	}

	private Command[] getCommandToFixId(HashMap pastedObjectMap, BaseObject newObject, int annotationType, String tag) throws Exception
	{
		BaseId baseId = new BaseId(newObject.getData(tag));
		ORef refToFix = new ORef(annotationType, baseId);
		ORef fixedRef = fixupSingleRef(pastedObjectMap, refToFix);
		
		return new Command[] {new CommandSetObjectData(newObject.getRef(), tag, fixedRef.getObjectId().toString())};
	}

	private Command[] getCommandToFixRef(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		ORef refToFix = ORef.createFromString(newObject.getData(tag));
		ORef fixedRef = fixupSingleRef(pastedObjectMap, refToFix);
		
		return new Command[] {new CommandSetObjectData(newObject.getRef(), tag, fixedRef.toString())};
	}
		
	private Command fixUpRelevancySet(HashMap pastedObjectMap, BaseObject newObject, String tag) throws Exception
	{
		RelevancyOverrideSet newOverrideSet = new RelevancyOverrideSet();
		RelevancyOverrideSet relevancyOverrideSet = new RelevancyOverrideSet(newObject.getData(tag));
		for(RelevancyOverride relevancyOverride : relevancyOverrideSet)
		{
			ORef oldRef = relevancyOverride.getRef();
			ORef newRef = fixupSingleRef(pastedObjectMap, oldRef);
			if (!newRef.isInvalid())
			{
				RelevancyOverride override = new RelevancyOverride(newRef, relevancyOverride.isOverride());
				newOverrideSet.add(override);
			}
		}
		
		return new CommandSetObjectData(newObject.getRef(), tag, newOverrideSet.toString());
	}

	private Command fixUpIdList(HashMap pastedObjectMap, BaseObject newObject, String annotationTag, int annotationType) throws Exception
	{
		//FIXME urgent: currently items ids found in list but not in map are not added to new list
		IdList oldList = new IdList(annotationType, newObject.getData(annotationTag));
		ORefList newRefList = getNewFixedUpRefList(pastedObjectMap, new ORefList(annotationType, oldList));		
		IdList newList = newRefList.convertToIdList(annotationType);
		
		return new CommandSetObjectData(newObject.getRef(), annotationTag, newList.toString());
	}
  
	private Command fixUpRefList(HashMap pastedObjectMap, BaseObject newObject, String annotationTag) throws Exception
	{
		//FIXME urgent: currently items ids found in list but not in map are not added to new list
		ORefList oldList = new ORefList(newObject.getData(annotationTag));
		ORefList newList = getNewFixedUpRefList(pastedObjectMap, oldList);
		
		ORef newObjectRef = newObject.getRef();
		if(DiagramFactor.is(newObjectRef) && annotationTag.equals(DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS))
		{
			ORefList groupBoxChildrenRefsToOmit = getExistingGroupBoxChildrenRefsToOmit(newList);
			newList.removeAll(groupBoxChildrenRefsToOmit);
		}
		
		if(DiagramLink.is(newObjectRef) && annotationTag.equals(DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS))
		{
			ORefList groupedLinkRefsToOmit = getExistingGroupedDiagramLinkRefsToOmit(newObjectRef, newList);
			newList.removeAll(groupedLinkRefsToOmit);
		}
		
		return new CommandSetObjectData(newObject.getRef(), annotationTag, newList.toString());
	}

	private ORefList getExistingGroupBoxChildrenRefsToOmit(ORefList groupBoxDiagramFactorChildrenRefs)
	{
		ORefList groupBoxChildrenRefsToOmit = new ORefList();
		for (int index = 0; index < groupBoxDiagramFactorChildrenRefs.size(); ++index)
		{
			ORef groupBoxChildFactorRef = groupBoxDiagramFactorChildrenRefs.get(index);
			DiagramFactor groupBoxChildDiagramFactor = DiagramFactor.find(getProject(), groupBoxChildFactorRef);
			if (groupBoxChildDiagramFactor == null)
			{
				EAM.logError("Pasted a missing gb child: " + groupBoxChildFactorRef);
				groupBoxChildrenRefsToOmit.add(groupBoxChildFactorRef);
				continue;
			}
			
			ORef wrappedFactorRef = groupBoxChildDiagramFactor.getWrappedORef();
			DiagramFactor wrappingDiagramFactorOnThisDiagram = getDiagramObject().getDiagramFactor(wrappedFactorRef);
			if (wrappingDiagramFactorOnThisDiagram == null)
				continue;
			
			if (wrappingDiagramFactorOnThisDiagram.isCoveredByGroupBox())
				groupBoxChildrenRefsToOmit.add(groupBoxChildFactorRef);
		}
		return groupBoxChildrenRefsToOmit;
	}

	private ORefList getExistingGroupedDiagramLinkRefsToOmit(ORef groupDiagramLinkRef, ORefList groupedDiagramLinkRefs)
	{
		ORefList groupedDiagramLinkRefsToOmit = new ORefList();
		
		DiagramLink groupDiagramLink = DiagramLink.find(getProject(), groupDiagramLinkRef);
		DiagramFactor fromDiagramFactor = groupDiagramLink.getFromDiagramFactor();
		boolean isFromGroup = fromDiagramFactor.isGroupBoxFactor();
		ORefList legalFromRefs = fromDiagramFactor.getGroupBoxChildrenRefs();
		DiagramFactor toDiagramFactor = groupDiagramLink.getToDiagramFactor();
		boolean isToGroup = toDiagramFactor.isGroupBoxFactor();
		ORefList legalToRefs = toDiagramFactor.getGroupBoxChildrenRefs();
		
		for(int index = 0; index < groupedDiagramLinkRefs.size(); ++ index)
		{
			ORef groupedDiagramLinkRef = groupedDiagramLinkRefs.get(index);
			DiagramLink diagramLink = DiagramLink.find(getProject(), groupedDiagramLinkRef);

			boolean shouldOmit = false;
			if(diagramLink == null)
				shouldOmit = true;
			else if(isFromGroup && !legalFromRefs.contains(diagramLink.getFromDiagramFactorRef()))
				shouldOmit = true;
			else if (isToGroup && !legalToRefs.contains(diagramLink.getToDiagramFactorRef()))
				shouldOmit = true;
			
			if(shouldOmit)
				groupedDiagramLinkRefsToOmit.add(groupedDiagramLinkRef);
		}
		return groupedDiagramLinkRefsToOmit;
	}

	private ORefList getNewFixedUpRefList(HashMap pastedObjectMap, ORefList oldList) throws Exception
	{
		ORefList newList = new ORefList();
		for (int i = 0; i < oldList.size(); ++i)
		{
			ORef oldRef = oldList.get(i);
			ORef refToAdd = fixupSingleRef(pastedObjectMap, oldRef);
			if (!refToAdd.isInvalid())
				newList.add(refToAdd);
		}
		return newList;
	}

	private ORef fixupSingleRef(HashMap pastedObjectMap, ORef oldRef) throws Exception
	{
		if (pastedObjectMap.containsKey(oldRef))
			return  (ORef) pastedObjectMap.get(oldRef);
		
		if (!isInBetweenProjectPaste())
			return oldRef;
		
		return ORef.INVALID;
	}

	protected ORef getFixedUpRef(HashMap pastedObjectMap, EnhancedJsonObject json, String tag) throws Exception
	{
		ORef oldRef = json.getRef(tag);
		return fixupSingleRef(pastedObjectMap, oldRef);
	}	
	
	private void loadNewObjectFromOldJson(BaseObject newObject, EnhancedJsonObject json) throws Exception, CommandFailedException
	{
		Command[] commandsToLoadFromJson = createCommandsToLoadFromJson(json, newObject);
		getProject().executeCommands(commandsToLoadFromJson);
	}
	
	private BaseObject createObject(int type) throws CommandFailedException
	{
		CommandCreateObject createObject = new CommandCreateObject(type);
		getProject().executeCommand(createObject);
		
		ORef newObjectRef = createObject.getObjectRef();
		BaseObject newObject = getProject().findObject(newObjectRef);
		
		return newObject;
	}
	
	private boolean diagramAlreadyContainsAlias(ORef wrappedRef)
	{
		DiagramObject diagramObject = getDiagramObject();
		return diagramObject.containsWrappedFactorRef(wrappedRef);
	}

	private String offsetLocation(EnhancedJsonObject json, DiagramFactorId diagramFactorId) throws Exception
	{
		Point originalLocation = json.getPoint(DiagramFactor.TAG_LOCATION);
		int offsetToAvoidOverlaying = dataHelper.getOffset(getProject());
		Point translatedPoint = dataHelper.getSnappedTranslatedPoint(getProject(), originalLocation, offsetToAvoidOverlaying);
		
		return EnhancedJsonObject.convertFromPoint(translatedPoint);
	}

	private String movePoints(PointList originalBendPoints, int offsetToAvoidOverlaying) throws Exception
	{
		PointList movedPoints = new PointList();
		for (int i = 0; i < originalBendPoints.size(); ++i)
		{
			Point originalPoint = originalBendPoints.get(i);
			Point translatedPoint = dataHelper.getSnappedTranslatedPoint(getProject(), originalPoint, offsetToAvoidOverlaying);			
			movedPoints.add(translatedPoint);
		}
		
		return movedPoints.toString();
	}

	private void addDiagramFactorToSelection(ORef diagramFactorRefToSelect) throws Exception
	{
		if (currentModel.containsDiagramFactor(diagramFactorRefToSelect))
		{
			FactorCell cell = currentModel.getFactorCellByRef(diagramFactorRefToSelect);
			pastedCellsToSelect.add(cell);
		}
	}
	
	private void addDiagramLinkToSelection(ORef diagramLinkRefToSelect) throws Exception
	{
		DiagramLink diagramLink = (DiagramLink) getProject().findObject(diagramLinkRefToSelect);
		LinkCell linkCell = currentModel.getLinkCell(diagramLink);
		pastedCellsToSelect.add(linkCell);	
	}

	private ORef createDiagramFactor() throws CommandFailedException
	{
		return createObject(DiagramFactorSchema.getObjectType()).getRef();
	}

	private void addToDiagramObject(DiagramObject diagramObjectToAddTo, ORef refToAppend, String tag) throws Exception
	{
		CommandSetObjectData appendId = CommandSetObjectData.createAppendIdCommand(diagramObjectToAddTo, tag, refToAppend.getObjectId());
		getProject().executeCommand(appendId);
	}
	
	private void addToCurrentDiagram(ORef refToAppend, String tag) throws Exception
	{
		addToDiagramObject(getDiagramObject(), refToAppend, tag);
	}

	protected void updateAutoCreatedThreatStressRatings() throws Exception
	{
		for (int index = 0; index < threatStressRatings.size(); ++index)
		{			
			String jsonAsString = threatStressRatings.get(index);
			EnhancedJsonObject tsrJson = new EnhancedJsonObject(jsonAsString);

			ThreatStressRating threatStressRating = findThreatStressRating(tsrJson);
			if(threatStressRating != null)
			{
				Command[] commands = createCommandsToLoadFromJson(tsrJson, threatStressRating);
				getProject().executeCommands(commands);
			}
		}
	}
	
	private ThreatStressRating findThreatStressRating(EnhancedJsonObject json) throws Exception
	{
		ORef oldStressRef = json.getRef(ThreatStressRating.TAG_STRESS_REF);
		ORef oldThreatRef = json.getRef(ThreatStressRating.TAG_THREAT_REF);
		
		ORef newStressRef = getCorrespondingNewRef(oldStressRef);
		ORef newThreatRef = getCorrespondingNewRef(oldThreatRef);
		
		ORef threatStressRatingRef = ThreatStressPair.findMatchingThreatStressRating(getProject(), newThreatRef, newStressRef);
		return ThreatStressRating.find(getProject(), threatStressRatingRef);
	}

	protected void createNewFactorsAndContents() throws Exception
	{
		for (int i = factorDeepCopies.size() - 1; i >= 0; --i)
		{			
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = getTypeFromJson(json);

			BaseId oldId = json.getId(BaseObject.TAG_ID);
			ORef oldObjectRef = new ORef(type, oldId);
			
			if (!shouldCreateObject(oldObjectRef, json))
				continue;
			
			int convertedType = convertType(oldObjectRef);
			BaseObject newObject = createObject(convertedType);
			loadNewObjectFromOldJson(newObject, json);
			
			getOldToNewObjectRefMap().put(oldObjectRef, newObject.getRef());
			fixupRefs(getOldToNewObjectRefMap(),newObject);
			
			CodeList tagNames = getTagNamesFromJson(json);
			fixTags(tagNames, newObject);
		}
		
		fixUpRelevancyOverrideSet();
	}
	
	public void fixTags(CodeList tagNames, BaseObject newObject) throws Exception
	{
		ORefList allTags = getProject().getTaggedObjectSetPool().getRefList();
		CodeList existingTagNames = new CodeList();
		for (int index = 0; index < allTags.size(); ++index)
		{
			TaggedObjectSet taggedObjectSet = TaggedObjectSet.find(getProject(), allTags.get(index));
			String tagName = taggedObjectSet.getLabel();
			if (tagNames.contains(tagName))
			{
				addObjectToTaggedObjectSet(taggedObjectSet, newObject.getRef());
				existingTagNames.add(tagName);
			}
		}
		
		tagNames.subtract(existingTagNames);
		CodeList needToCreateTagNames = new CodeList(tagNames);
		
		createMissingTags(needToCreateTagNames, newObject.getRef());
	}

	private void createMissingTags(CodeList needToCreateTagNames, ORef refToBeRemoved) throws Exception
	{
		for (int index = 0; index < needToCreateTagNames.size(); ++index)
		{
			CommandCreateObject createTaggedObjectSet = new CommandCreateObject(TaggedObjectSetSchema.getObjectType());
			getProject().executeCommand(createTaggedObjectSet);
			
			ORef newTaggedObjectSetRef = createTaggedObjectSet.getObjectRef();
			CommandSetObjectData setLabel = new CommandSetObjectData(newTaggedObjectSetRef, TaggedObjectSet.TAG_LABEL, needToCreateTagNames.get(index));
			getProject().executeCommand(setLabel);
			
			TaggedObjectSet taggedObjectSet = TaggedObjectSet.find(getProject(), newTaggedObjectSetRef);
			addObjectToTaggedObjectSet(taggedObjectSet, refToBeRemoved); 
		}
	}

	private void addObjectToTaggedObjectSet(TaggedObjectSet taggedObjectSet, ORef refToBeRemoved) throws Exception
	{
		CommandSetObjectData tagObjectCommand = CommandSetObjectData.createAppendORefCommand(taggedObjectSet, TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, refToBeRemoved);
		getProject().executeCommand(tagObjectCommand);
	}

	private void fixUpRelevancyOverrideSet() throws Exception
	{
		Collection<ORef> newPastedRefs = getOldToNewObjectRefMap().values();
		for(ORef ref : newPastedRefs)
		{
			BaseObject newObject = getProject().findObject(ref);
			fixUpRelevancyOverrideSet(newObject);
		}
	}

	private void fixUpRelevancyOverrideSet(BaseObject newObject) throws Exception
	{
		String[] fieldTags = newObject.getFieldTags();
		for (int i = 0; i < fieldTags.length; ++i)
		{
			String tag = fieldTags[i];
			if (newObject.isRelevancyOverrideSet(tag))
			{
				Command updateRelevancyCommand = fixUpRelevancySet(getOldToNewObjectRefMap(), newObject, tag);
				getProject().executeCommand(updateRelevancyCommand);
			}
		}
	}

	protected void createNewDiagramFactors() throws Exception
	{
		for (int i = diagramFactorDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = diagramFactorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			createNewDiagramFactor(json);
		}
	}
	
	private void createNewDiagramFactor(EnhancedJsonObject json) throws Exception
	{
		ORef oldWrappedRef = json.getRef(DiagramFactor.TAG_WRAPPED_REF);
		boolean isGroupBox = GroupBox.is(oldWrappedRef);

		DiagramFactorId diagramFactorId = new DiagramFactorId(json.getId(DiagramFactor.TAG_ID).asInt());
		ORef diagramFactorRef = new ORef(DiagramFactorSchema.getObjectType(), diagramFactorId);

		// TODO: GB logic should probably be handled in DiagramAliasPaster, not here
		if (!isGroupBox && oldToNewPastedObjectMap.containsKey(diagramFactorRef))
			return;
		
		ORef newWrappedRef = getCorrespondingNewRef(oldWrappedRef);
	
		// TODO: GB logic should probably be handled in DiagramAliasPaster, not here
		if (!isGroupBox && diagramAlreadyContainsAlias(newWrappedRef))
		{
			DiagramFactor existingDiagramFactor = getDiagramObject().getDiagramFactor(newWrappedRef);
			getOldToNewObjectRefMap().put(diagramFactorRef, existingDiagramFactor.getRef());
			return;
		}
		
		if (ignorePastingDiagramFactorForFactor(newWrappedRef))
			return;
		
		String newLocationAsJsonString = offsetLocation(json, diagramFactorId);
		json.put(DiagramFactor.TAG_LOCATION, newLocationAsJsonString);
		
		ORef newDiagramFactorRef = createDiagramFactor();
		DiagramFactor newDiagramFactor = DiagramFactor.find(getProject(), newDiagramFactorRef);
		loadNewObjectFromOldJson(newDiagramFactor, json);

		BaseId oldDiagramFactorId = json.getId(DiagramFactor.TAG_ID);
		int type = getTypeFromJson(json);
		getOldToNewObjectRefMap().put(new ORef(type, oldDiagramFactorId), newDiagramFactorRef);
		fixupRefs(getOldToNewObjectRefMap(), newDiagramFactor);
		addToCurrentDiagram(newDiagramFactorRef, DiagramObject.TAG_DIAGRAM_FACTOR_IDS);
		addDiagramFactorToSelection(newDiagramFactorRef);
	}

	protected void createNewFactorLinks() throws Exception
	{
		for (int i = factorLinkDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = factorLinkDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			BaseObject newObject = null;	
			int type = getTypeFromJson(json);
			if (type == FactorLinkSchema.getObjectType())
				newObject = createFactorLink(json);
			
			if (newObject != null)
				fixObjectRefs(getOldToNewObjectRefMap(), newObject, json);
		}
	}
	
	protected void createNewDiagramLinks() throws Exception
	{	
		int offsetToAvoidOverlaying = dataHelper.getOffset(getProject());
		for (int i = diagramLinkDeepCopies.size() - 1; i >= 0; --i)
		{
			String jsonAsString = diagramLinkDeepCopies.get(i);
			EnhancedJsonObject diagramLinkJson = new EnhancedJsonObject(jsonAsString);
			BaseId diagramLinkId = diagramLinkJson.getId(DiagramLink.TAG_ID);
			ORef diagramLinkRef = new ORef(DiagramLinkSchema.getObjectType(), diagramLinkId);
			if (oldToNewPastedObjectMap.containsKey(diagramLinkRef))
				continue;
	
			PointList originalBendPoints = new PointList(diagramLinkJson.getString(DiagramLink.TAG_BEND_POINTS));
			String movedBendPointsAsString = movePoints(originalBendPoints, offsetToAvoidOverlaying);
			diagramLinkJson.put(DiagramLink.TAG_BEND_POINTS, movedBendPointsAsString);
			
			DiagramFactor fromDiagramFactor = getNewDiagramFactor(diagramLinkJson, DiagramLink.FROM);
			if(fromDiagramFactor == null)
				continue;
			DiagramFactor toDiagramFactor = getNewDiagramFactor(diagramLinkJson, DiagramLink.TO);
			if(toDiagramFactor == null)
				continue;

			LinkCreator linkCreator = new LinkCreator(getProject());
			if (linkCreator.linkToBePastedWasRejected(currentModel, fromDiagramFactor.getRef(), toDiagramFactor.getRef()))
				continue;
			
			ORef fromFactorRef = fromDiagramFactor.getWrappedORef();
			ORef toFactorRef = toDiagramFactor.getWrappedORef();
			ORef newFactorLinkRef = null;
			FactorLink factorLink = findFactorLink(fromFactorRef, toFactorRef);
			if(factorLink != null)
				newFactorLinkRef = factorLink.getRef();
			
			int type = getTypeFromJson(diagramLinkJson);
			DiagramLink newDiagramLink = (DiagramLink) createObject(type);
			//NOTE: we are not always setting diagramLink's wrapped ref due to it sometimes being a Group box link
			if (newFactorLinkRef != null)
			{
				CommandSetObjectData setWrappedId = new CommandSetObjectData(newDiagramLink.getRef(), DiagramLink.TAG_WRAPPED_ID, newFactorLinkRef.getObjectId().toString());
				getProject().executeCommand(setWrappedId);
			}
	    	
			// TODO: Switch this to match the DF pattern of:
			// create, load (everything), fixup (everything)
			CommandSetObjectData setWrappedFromId = new CommandSetObjectData(newDiagramLink.getRef(), DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID, fromDiagramFactor.getId().toString());
			getProject().executeCommand(setWrappedFromId);
			
			CommandSetObjectData setWrappedToId = new CommandSetObjectData(newDiagramLink.getRef(), DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID, toDiagramFactor.getId().toString());
			getProject().executeCommand(setWrappedToId);

			Command[]  commandsToLoadFromJson = createCommandsToLoadFromJson(diagramLinkJson, newDiagramLink);
			getProject().executeCommands(commandsToLoadFromJson);
	
			if(newDiagramLink.getWrappedFactorLink() == null && !newDiagramLink.isGroupBoxLink())
				throw new RuntimeException("Created non-group DiagramLink with no wrapped FactorLink: " + newDiagramLink.getRef());
			ORef newDiagramLinkRef = newDiagramLink.getRef();
			getOldToNewObjectRefMap().put(diagramLinkRef, newDiagramLinkRef);
			fixupRefs(getOldToNewObjectRefMap(), newDiagramLink);
			addToCurrentDiagram(newDiagramLinkRef, DiagramObject.TAG_DIAGRAM_FACTOR_LINK_IDS);
			addDiagramLinkToSelection(newDiagramLinkRef);
		}
	}

	private DiagramFactor getNewDiagramFactor(EnhancedJsonObject diagramLinkJson, int direction)
	{
		ORef newDiagramFactorRef = getNewDiagramFactorRef(diagramLinkJson, direction);
		if(newDiagramFactorRef.isInvalid())
			return null;
		
		return DiagramFactor.find(getProject(), newDiagramFactorRef);
	}

	private ORef getNewDiagramFactorRef(EnhancedJsonObject diagramLinkJson, int direction)
	{
		if(isGroupLink(diagramLinkJson))
			return getGroupLinkDiagramFactorEnd(diagramLinkJson, direction);

		return getWrappedLinkFactorEnd(diagramLinkJson, direction);
	}

	private boolean isGroupLink(EnhancedJsonObject diagramLinkJson)
	{
		BaseId wrappedLinkId = diagramLinkJson.optId(DiagramLink.TAG_WRAPPED_ID);
		if(wrappedLinkId.isInvalid())
			return true;
		
		return false;
	}

	private ORef getGroupLinkDiagramFactorEnd(EnhancedJsonObject json, int direction)
	{
		String diagramFactorEndTag = DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID;
		if (direction == DiagramLink.TO)
			diagramFactorEndTag = DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID;
		
		ORef diagramFactorRef = getDiagramFactorId(json, diagramFactorEndTag);
		BaseId factorLinkId = json.getId(DiagramLink.TAG_WRAPPED_ID);
		ORef oldFactorLinkRef = new ORef(FactorLinkSchema.getObjectType(), factorLinkId);
		if (oldFactorLinkRef.isInvalid())
			return diagramFactorRef;
		
		return ORef.INVALID;
	}
	
	private ORef getWrappedLinkFactorEnd(EnhancedJsonObject json, int direction)
	{
		BaseId factorLinkId = json.getId(DiagramLink.TAG_WRAPPED_ID);
		ORef oldFactorLinkRef = new ORef(FactorLinkSchema.getObjectType(), factorLinkId);
		ORef newFactorLinkRef = getRefFromMap(oldFactorLinkRef);
		FactorLink factorLink = FactorLink.find(getProject(), newFactorLinkRef);
		if (factorLink == null)
			return ORef.INVALID;
		
		ORef factorRef = factorLink.getFactorRef(direction);
		DiagramFactor newDiagramFactor = getDiagramObject().getDiagramFactor(factorRef);
		if(newDiagramFactor == null)
			return ORef.INVALID;
		
		return newDiagramFactor.getRef();
	}

	private boolean isPastingIntoResultsChain()
	{
		return ResultsChainDiagram.is(getDiagramObject().getType());
	}
	
	private boolean isPastingIntoConceptualModel()
	{
		return ConceptualModelDiagram.is(getDiagramObject().getType());
	}

	private void fixObjectRefs(HashMap pastedObjectMap, BaseObject newObject, EnhancedJsonObject json) throws Exception, CommandFailedException
	{
		loadNewObjectFromOldJson(newObject, json);
		fixupRefs(pastedObjectMap, newObject);
	}
	
	protected FactorLink findFactorLink(ORef fromFactorRef, ORef toFactorRef)
	{
		ORefList allFactorLinks = getProject().getPool(FactorLinkSchema.getObjectType()).getRefList();
		for(int i = 0; i < allFactorLinks.size(); ++i)
		{
			FactorLink link = FactorLink.find(getProject(), allFactorLinks.get(i));
			if(link.getFromFactorRef().equals(fromFactorRef) && link.getToFactorRef().equals(toFactorRef))
				return link;
		}
		
		return null;
	}

	protected FactorLink createFactorLink(EnhancedJsonObject json) throws Exception
	{
		if (cannotCreateNewFactorLinkFromAnotherProject(json))
			return null;
		
		// TODO: Switch this to match the DF pattern of:
		// create, load (everything), fixup (everything)
		ORef newFromRef = getFixedUpRef(getOldToNewObjectRefMap(), json, FactorLink.TAG_FROM_REF);
		ORef newToRef = getFixedUpRef(getOldToNewObjectRefMap(), json, FactorLink.TAG_TO_REF);
		
		LinkCreator linkCreator = new LinkCreator(getProject());
		if (linkCreator.linkWasRejected(currentModel, newFromRef, newToRef))
			return null;
					
		ORef factorLinkRef = linkCreator.createFactorLink(newFromRef, newToRef);
		FactorLink newFactorLink = (FactorLink) getProject().findObject(factorLinkRef);
		
		Command[]  commandsToLoadFromJson = createCommandsToLoadFromJson(json, newFactorLink);
		getProject().executeCommands(commandsToLoadFromJson);

		BaseId oldFactorLinkId = json.getId(FactorLink.TAG_ID);
		getOldToNewObjectRefMap().put(new ORef(FactorLinkSchema.getObjectType(), oldFactorLinkId), newFactorLink.getRef());
		
		return newFactorLink;
	}

	private boolean isInBetweenProjectPaste()
	{
		return ! getProject().getFilename().equals(getClipboardProjectFileName());
	}

    private boolean isValidTaxonomyClassificationList(Project project, BaseObject baseObject, String taxonomyClassificationList) throws Exception
    {
        if (taxonomyClassificationList.equals(""))
            return true;

        TaxonomyClassificationMap taxonomyClassificationLMap = new TaxonomyClassificationMap(taxonomyClassificationList);
        return TaxonomyHelper.isTaxonomyClassificationMapValid(project, baseObject, taxonomyClassificationLMap);
    }

	private boolean cannotCreateNewFactorLinkFromAnotherProject(EnhancedJsonObject json)
	{
		ORef oldFromRef = json.getRef(FactorLink.TAG_FROM_REF);
		ORef oldToRef = json.getRef(FactorLink.TAG_TO_REF);
		boolean haveBothFactorsBeenCopied = haveBothFactorsBeenCopied(oldFromRef, oldToRef);
		boolean isInBetweenProjectPaste = isInBetweenProjectPaste();
		
		return (haveBothFactorsBeenCopied && isInBetweenProjectPaste);
	}

	private boolean haveBothFactorsBeenCopied(ORef oldFromRef, ORef oldToRef)
	{
		return (getOldToNewObjectRefMap().get(oldFromRef) == null || getOldToNewObjectRefMap().get(oldToRef) == null);
	}
	
	public boolean wasAnyDataLost() throws Exception
	{
		if (!isInBetweenProjectPaste())
			return false;

        if (doesPasteDiscardInvalidTaxonomyClassificationList)
            return true;

		for (int i = 0; i < factorDeepCopies.size(); ++i)
		{
			String jsonAsString = factorDeepCopies.get(i);
			EnhancedJsonObject json = new EnhancedJsonObject(jsonAsString);
			int type = getTypeFromJson(json);
			final ORef assignedLeaderResourceRef = json.optRef(BaseObject.TAG_ASSIGNED_LEADER_RESOURCE);
			if (assignedLeaderResourceRef.isValid())
				return true;
			if (TimeframeSchema.getObjectType() == type)
				return true;
			if (ResourceAssignmentSchema.getObjectType() == type)
				return true;
			if (ExpenseAssignmentSchema.getObjectType() == type)
				return true;
		}
		
		return false;
	}

	private ORef getDiagramFactorId(EnhancedJsonObject json, String tag)
	{
		BaseId oldId = json.getId(tag);
		ORef oldRef = new ORef(ObjectType.DIAGRAM_FACTOR, oldId);
		return getRefFromMap(oldRef);
	}

	private ORef getRefFromMap(ORef oldRef)
	{
		ORef newRef = getOldToNewObjectRefMap().get(oldRef);
		if (newRef == null)
			return oldRef; 
			 
		return newRef;
	}

	private int convertType(ORef oldObjectRef)
	{
		if (isPastingInSameDiagramType())
			return oldObjectRef.getObjectType();

		if (IntermediateResult.is(oldObjectRef))
			return CauseSchema.getObjectType();
		
		if (ThreatReductionResult.is(oldObjectRef))
			return CauseSchema.getObjectType();
		
		if (Cause.is(oldObjectRef))
		{
			Factor factor = Factor.findFactor(getProject(), oldObjectRef);
			if(factor != null && factor.isDirectThreat())
				return ThreatReductionResultSchema.getObjectType();

			return IntermediateResultSchema.getObjectType();
		}

		if (BiophysicalFactor.is(oldObjectRef))
			return BiophysicalResultSchema.getObjectType();

		if (BiophysicalResult.is(oldObjectRef))
			return BiophysicalFactorSchema.getObjectType();

		return oldObjectRef.getObjectType();
	}

	public boolean isPastingInSameDiagramType()
	{
		int fromType = transferableList.getDiagramObjectRefCopiedFrom().getObjectType();
		int toType = getDiagramObject().getType();
		
		return (fromType == toType);
	}
	
	public Project getProject()
	{
		return project;
	}

    public HashMap<ORef, ORef> getOldToNewObjectRefMap()
	{
		return oldToNewPastedObjectMap;
	}
	
	protected DiagramObject getDiagramObject()
	{
		return currentModel.getDiagramObject();
	}
	
	protected static int getTypeFromJson(EnhancedJsonObject json)
	{
		return json.getInt(FAKE_TAG_TYPE);
	}
	
	private CodeList getTagNamesFromJson(EnhancedJsonObject json) throws Exception
	{
		return new CodeList(json.getString(FAKE_TAG_TAG_NAMES));
	}

	protected boolean containsType(int[] types, int type)
	{
		for (int i = 0; i < types.length; ++i)
		{
			if (types[i] == type)
				return true;
		}
		
		return false;
	}
	
	private boolean ignorePastingDiagramFactorForFactor(ORef factorRef)
	{
		if (Task.is(factorRef))
		{
			if (!doesObjectExist(factorRef))
				return true;
			
			if (isPastingIntoConceptualModel())
				return true;
			
			return !hasReferrersInDiagram(factorRef, StrategySchema.getObjectType());
		}
		
		if (Stress.is(factorRef))
		{
			if (!doesObjectExist(factorRef))
				return true;
			
			if (isPastingIntoResultsChain())
				return true;
			
			return !hasReferrersInDiagram(factorRef, TargetSchema.getObjectType());
		}
		
		return false;
	}

	private boolean doesObjectExist(ORef factorRef)
	{
		BaseObject found = getProject().findObject(factorRef);
		if (found == null)
			return false;
		
		return true;
	}
	
	private boolean hasReferrersInDiagram(ORef referredObjectRef, int referrerType)
	{
		ORefList referrerRefs = getDiagramObject().findReferrersOnSameDiagram(referredObjectRef, referrerType);
		return referrerRefs.size() > 0;
	}
	
	private Command[] createCommandsToLoadFromJson(EnhancedJsonObject json, BaseObject baseObject) throws Exception
	{
		Vector<CommandSetObjectData> commands = new Vector<CommandSetObjectData>();
		Vector<String> tags = getTagsToLoadFromJson(json, baseObject);
		for (String tag : tags)
		{
            String value = json.optString(tag);
            if (transferableList.isLegacyTransferableMiradiList())
                value = baseObject.getHtmlEncodedValue(json, tag);

            if (tag.equals(BaseObject.TAG_TAXONOMY_CLASSIFICATION_CONTAINER) && !isValidTaxonomyClassificationList(project, baseObject, value))
            {
                doesPasteDiscardInvalidTaxonomyClassificationList = true;
                continue;
            }

            CommandSetObjectData setDataCommand = new CommandSetObjectData(baseObject.getRef(), tag, value);
            commands.add(setDataCommand);
		}
		
		return commands.toArray(new Command[0]);
	}

	public static Vector<String> getTagsToLoadFromJson(EnhancedJsonObject json, BaseObject baseObject)
	{
		final Vector<String> storedFieldTags = baseObject.getStoredFieldTags();
		int oldType = getTypeFromJson(json);
		int newType = baseObject.getType();
		final Vector<String> tagsToSkip = getTagsToSkipWhenLoadingJsonData(oldType, newType);
		storedFieldTags.removeAll(tagsToSkip);
		
		return storedFieldTags;
	}
	
	private static Vector<String> getTagsToSkipWhenLoadingJsonData(int oldType, int newType)
	{
		// NOTE: DF has already been switched to the new pattern.
		// When these other classes are switched to match it,
		// this whole method will go away.
		Vector<String> tags = new Vector<String>();

		if (Cause.is(oldType) && ThreatReductionResult.is(newType))
		{
			tags.add(BaseObject.TAG_TAXONOMY_CLASSIFICATION_CONTAINER);
		}
		else if (FactorLink.is(newType))
		{
			tags.add(FactorLink.TAG_FROM_REF);
			tags.add(FactorLink.TAG_TO_REF);
		}
		else if (DiagramLink.is(newType))
		{
			tags.add(DiagramLink.TAG_WRAPPED_ID);
			tags.add(DiagramLink.TAG_FROM_DIAGRAM_FACTOR_ID);
			tags.add(DiagramLink.TAG_TO_DIAGRAM_FACTOR_ID);
		}
		else if (ThreatStressRating.is(newType))
		{
			tags.add(ThreatStressRating.TAG_STRESS_REF);
			tags.add(ThreatStressRating.TAG_THREAT_REF);
		}
		
		return tags;
	}

	abstract public ORef getFactorLinkRef(ORef oldWrappedFactorLinkRef);	

	abstract public void pasteFactors(Point startPoint) throws Exception;
	
	abstract public void pasteFactorsAndLinks(Point startPoint) throws Exception;
	
	abstract public ORef getCorrespondingNewRef(ORef oldWrappedRef) throws Exception;
	
	abstract protected boolean shouldCreateObject(ORef ref, EnhancedJsonObject json);
	
	private Project project;
	private DiagramModel currentModel;
	private DiagramPanel diagramPanel;

	private Vector<String> factorDeepCopies;
	private Vector<String> diagramFactorDeepCopies;
	private Vector<String> threatStressRatings;
	private Vector<String> factorLinkDeepCopies;
	private Vector<String> diagramLinkDeepCopies;
	
	protected HashMap<ORef, ORef> oldToNewPastedObjectMap;	
	protected PointManipulator dataHelper;
	protected AbstractTransferableMiradiList transferableList;
	private Vector<EAMGraphCell> pastedCellsToSelect;

    private boolean doesPasteDiscardInvalidTaxonomyClassificationList;

	public static final String FAKE_TAG_TYPE = "Type";
	public static final String FAKE_TAG_TAG_NAMES = "TagNames";

}
