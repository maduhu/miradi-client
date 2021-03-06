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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.miradi.diagram.cells.*;
import org.miradi.ids.BaseId;
import org.miradi.ids.DiagramLinkId;
import org.miradi.ids.IdList;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.FactorSet;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.DiagramFactorPool;
import org.miradi.objectpools.DiagramLinkPool;
import org.miradi.objectpools.FactorLinkPool;
import org.miradi.objectpools.GoalPool;
import org.miradi.objectpools.ObjectivePool;
import org.miradi.objects.*;
import org.miradi.project.Project;
import org.miradi.project.threatrating.ThreatRatingFramework;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.schemas.FactorLinkSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.EnhancedJsonObject;
import org.miradi.views.diagram.GroupOfDiagrams;
import org.miradi.views.diagram.LayerManager;
import org.miradi.views.diagram.LayerSorter;
import org.miradi.views.umbrella.TaggedObjectManager;

abstract public class DiagramModel extends DefaultGraphModel
{
	public DiagramModel(Project projectToUse)
	{
		project = projectToUse;
		layerManager = new LayerManager(getDiagramObject());
		taggedObjectManager = new TaggedObjectManager(project);
		clear();
	}
		
	public void clear()
	{
		isDamaged = false;
		while(getRootCount() > 0)
			remove(new Object[] {getRootAt(0)});

		cellInventory = new CellInventory();
		graphLayoutCache = new PartialGraphLayoutCache(this);
	}

	public Project getProject()
	{
		return project;
	}
	
	public ChainWalker getDiagramChainWalker()
	{
		return getDiagramObject().getDiagramChainWalker();
	}
	
	public ThreatRatingFramework getThreatRatingFramework()
	{
		return project.getThreatRatingFramework();
	}
	
	public void addDiagramFactor(DiagramFactor diagramFactor) throws Exception
	{
		Factor factor = Factor.findFactor(project, diagramFactor.getWrappedORef());
		FactorCell factorCell = createFactorCell(diagramFactor, factor);
		addFactorCellToModel(factorCell);
	}

	private FactorCell createFactorCell(DiagramFactor diagramFactor, Factor factor)
	{
		int factorType = factor.getType();
		if (factorType == ObjectType.CAUSE)
			return new DiagramCauseCell((Cause) factor, diagramFactor);
	
		if (factorType == ObjectType.BIOPHYSICAL_FACTOR)
			return new DiagramBiophysicalFactorCell((BiophysicalFactor) factor, diagramFactor);

		if (factorType == ObjectType.BIOPHYSICAL_RESULT)
			return new DiagramBiophysicalResultCell((BiophysicalResult) factor, diagramFactor);

		if (factorType == ObjectType.STRATEGY)
			return new DiagramStrategyCell((Strategy) factor, diagramFactor);
		
		if (factorType == ObjectType.TARGET)
			return new DiagramTargetCell((Target) factor, diagramFactor);
	
		if (factorType == ObjectType.INTERMEDIATE_RESULT)
			return new DiagramIntermediateResultCell((IntermediateResult) factor, diagramFactor);
		
		if (factorType == ObjectType.THREAT_REDUCTION_RESULT)
			return new DiagramThreatReductionResultCell((ThreatReductionResult) factor, diagramFactor);
		
		if (factorType == ObjectType.TEXT_BOX)
			return new DiagramTextBoxCell((TextBox)factor, diagramFactor);
		
		if (factorType == ObjectType.GROUP_BOX)
			return new DiagramGroupBoxCell(this, (GroupBox)factor, diagramFactor);
		
		if (factorType == ObjectType.STRESS)
			return new DiagramStressCell((Stress)factor, diagramFactor);
		
		if (factorType == ObjectType.TASK)
			return new DiagramActivityCell((Task)factor, diagramFactor);
		
		if (factorType == ObjectType.SCOPE_BOX)
			return new DiagramScopeBoxCell((ScopeBox) factor, diagramFactor);
		
		if (HumanWelfareTarget.is(factorType))
			return new DiagramHumanWelfareTarget((HumanWelfareTarget) factor, diagramFactor);
		
		throw new RuntimeException("Unknown factor type "+factorType);
	}

	private void addFactorCellToModel(FactorCell factor) throws Exception
	{
		insertCellIntoGraph(factor);
		cellInventory.addFactor(factor);
		notifyListeners(createDiagramModelEvent(factor), new ModelEventNotifierFactorAdded());
	}

	private void insertCellIntoGraph(DefaultGraphCell cell)
	{
		Object[] cells = new Object[] {cell};
		Hashtable<DefaultGraphCell, AttributeMap> nestedAttributeMap = getNestedAttributeMap(cell);
		insert(cells, nestedAttributeMap, null, null, null);
		sortLayers();
	}

	public void sortLayers()
	{
		Collections.sort(getAllRootCells(), new LayerSorter());
	}

	// NOTE: roots member is unchecked in JGraph, so we can't fix it
	@SuppressWarnings("unchecked")
	public List<EAMGraphCell> getAllRootCells()
	{
		return roots;
	}
	
	private Hashtable<DefaultGraphCell, AttributeMap> getNestedAttributeMap(DefaultGraphCell cell)
	{
		Hashtable<DefaultGraphCell, AttributeMap> nest = new Hashtable<DefaultGraphCell, AttributeMap>();
		nest.put(cell, cell.getAttributes());
		return nest;
	}
	
	private DiagramModelEvent createDiagramModelEvent(EAMGraphCell cell) throws Exception 
	{
		return new DiagramModelEvent(this, cell);
	}
	
	public void addDiagramModelListener(DiagramModelListener listener)
	{
		diagramModelListenerList.add(listener);
	}
	
    public void removeMyEventListener(DiagramModelListener listener) 
    {
    	diagramModelListenerList.remove(listener);
    }	
    
    void notifyListeners(DiagramModelEvent event, ModelEventNotifier eventNotifier) 
    {
        for (int i=0; i<diagramModelListenerList.size(); ++i) 
        {
        	eventNotifier.doNotify(diagramModelListenerList.get(i), event);
        }                
    }

    public void removeDiagramFactor(ORef diagramFactorRef) throws Exception
    {
    	FactorCell diagramFactorToDelete = getFactorCellByRef(diagramFactorRef);	
    	Object[] cells = new Object[]{diagramFactorToDelete};
		remove(cells);
		cellInventory.removeFactor(diagramFactorRef);
		notifyListeners(createDiagramModelEvent(diagramFactorToDelete), new ModelEventNotifierFactorDeleted());
    }

    public DiagramLink addLinkToDiagram(DiagramLink diagramFactorLink) throws Exception
    {
		FactorCell from = rawGetFactorCellByRef(diagramFactorLink.getFromDiagramFactorRef());
		if(from == null)
			EAM.logError("Missing from, DFL=" + diagramFactorLink.getId() + ", From=" + diagramFactorLink.getFromDiagramFactorRef());
		FactorCell to = rawGetFactorCellByRef(diagramFactorLink.getToDiagramFactorRef());
		if(to == null)
			EAM.logError("Missing to, DFL=" + diagramFactorLink.getId() + ", To=" + diagramFactorLink.getToDiagramFactorRef());
		
		LinkCell cell = new LinkCell(diagramFactorLink, from, to);
		
		EAMGraphCell[] newLinks = new EAMGraphCell[]{cell};
		Map<DefaultGraphCell, AttributeMap> nestedMap = getNestedAttributeMap(cell);
		ConnectionSet cs = new ConnectionSet(cell, from.getPort(), to.getPort());

		insert(newLinks, nestedMap, cs, null, null);
		sortLayers();
		cellInventory.addFactorLink(diagramFactorLink, cell);
		
		notifyListeners(createDiagramModelEvent(cell), new ModelEventNotifierFactorLinkAdded());
		
    	return diagramFactorLink;
    }
    
	public void deleteDiagramFactorLink(DiagramLink diagramFactorLinkToDelete) throws Exception
	{
		LinkCell cell = cellInventory.getLinkCell(diagramFactorLinkToDelete);
		Object[] links = new Object[]{cell};
		
		remove(links);
		cellInventory.removeFactorLink(diagramFactorLinkToDelete);
		
		notifyListeners(createDiagramModelEvent(cell), new ModelEventNotifierFactorLinkDeleted());
	}
	
	// TODO: This method is only called by tests
	public boolean areDiagramFactorsLinked(ORef fromDiagramFactorRef, ORef toDiagramFactorRef) throws Exception
	{
		DiagramFactor.ensureType(fromDiagramFactorRef);
		DiagramFactor.ensureType(toDiagramFactorRef);
		
		DiagramFactor fromDiagramFactor = DiagramFactor.find(project, fromDiagramFactorRef);
		DiagramFactor toDiagramFactor = DiagramFactor.find(project, toDiagramFactorRef);
		
		return areLinked(fromDiagramFactor.getWrappedORef(), toDiagramFactor.getWrappedORef());
	}

	public boolean areLinked(ORef fromFactorRef, ORef toFactorRef)
	{
		Factor.ensureFactor(fromFactorRef);
		Factor.ensureFactor(toFactorRef);
		return getDiagramObject().areLinkedEitherDirection(fromFactorRef, toFactorRef);
	}

	public DiagramLink getDiagramLink(ORef factorRef1, ORef factorRef2)
	{
		Factor.ensureFactor(factorRef1);
		Factor.ensureFactor(factorRef2);
		return getDiagramObject().getDiagramLink(factorRef1, factorRef2);
	}

	
	public boolean isResultsChain()
	{
		return diagramContents.isResultsChain();
	}
	
	public boolean isConceptualModelDiagram()
	{
		return diagramContents.isConceptualModelDiagram();
	}
	
	public boolean isSharedInResultsChain(DiagramFactor diagramFactorToCheck)
	{
		DiagramFactor[] allResultsChainDiagramFactors = GroupOfDiagrams.findAllResultsChainDiagrams(project);
		return isSharedInDiagramFactors(allResultsChainDiagramFactors, diagramFactorToCheck);
	}
	
	public boolean isSharedInConceptualModel(DiagramFactor diagramFactorToCheck)
	{
		DiagramFactor[] allConceptualModelDiagramFactors = GroupOfDiagrams.findAllConceptualModelDiagrams(project);
		return isSharedInDiagramFactors(allConceptualModelDiagramFactors, diagramFactorToCheck);
	}
	
	public boolean isSharedInDiagramFactors(DiagramFactor[] potentialShares, DiagramFactor diagramFactorToCheck)
	{
		for (int i = 0; i < potentialShares.length; ++i)
		{
			DiagramFactor possibleAliasDiagramFactor = potentialShares[i];
			if (isAliasOf(diagramFactorToCheck, possibleAliasDiagramFactor))
				return true;
		}
		
		return false;	
	}
	
	private boolean isAliasOf(DiagramFactor diagramFactorToCheck, DiagramFactor possibleAliasDiagramFactor)
	{
		final boolean isSameDiagramFactor = diagramFactorToCheck.getId().equals(possibleAliasDiagramFactor.getId());
		if (isSameDiagramFactor)
			return false;

		final boolean isDifferentWrappedFactor = ! diagramFactorToCheck.getWrappedORef().equals(possibleAliasDiagramFactor.getWrappedORef());
		if (isDifferentWrappedFactor)
			return false;
				
		return true;
	}

	public FactorSet getNodesInChain(DiagramFactor startingFactor)
	{
		ChainWalker chainObject = getDiagramChainWalker();
		return chainObject.buildNormalChainAndGetFactors(startingFactor);
	}
		
	public void moveFactors(int deltaX, int deltaY, ORefList diagramFactorRefs) throws Exception
	{
		moveFactorsWithoutNotification(deltaX, deltaY, diagramFactorRefs);
		factorsWereMoved(diagramFactorRefs);
	}

	public void moveFactorsWithoutNotification(int deltaX, int deltaY, ORefList diagramFactorRefs) throws Exception
	{
		for(int i = 0; i < diagramFactorRefs.size(); ++i)
		{
			FactorCell factorToMove = getFactorCellByRef(diagramFactorRefs.get(i));
			Point oldLocation = factorToMove.getLocation();
			Point newLocation = new Point(oldLocation.x + deltaX, oldLocation.y + deltaY);
			Point newSnappedLocation = getProject().getSnapped(newLocation);
			EAM.logVerbose("moved Node from:"+ oldLocation +" to:"+ newSnappedLocation);
			factorToMove.setLocation(newSnappedLocation);
			updateCell(factorToMove);
		}
	}
	
	public void factorsWereMoved(ORefList diagramFactorRefs)
	{
		for(int index = 0; index < diagramFactorRefs.size(); ++index)
		{
			try
			{
				FactorCell factorCell = getFactorCellByRef(diagramFactorRefs.get(index));
				notifyListeners(createDiagramModelEvent(factorCell), new ModelEventNotifierFactorMoved());
			}
			catch (Exception e)
			{
				EAM.logException(e);
			}
		}
	}
	
	public Point recursivelyGetNonOverlappingFactorPoint(Point pointToUse)
	{
		Point point = (Point) pointToUse.clone();
		DiagramFactor[] allDiagramFactors = getAllDiagramFactorsAsArray();
		for (int i = 0; i < allDiagramFactors.length; ++i)
		{
			Point thisLocation = allDiagramFactors[i].getLocation();
			if (thisLocation.equals(point))
			{
				point.translate(getProject().getGridSize(), getProject().getGridSize());
				return recursivelyGetNonOverlappingFactorPoint(point);
			}
		}
		
		return pointToUse;
	}
		
	public int getFactorCount()
	{
		return getAllFactorCells().size();
	}
	
	public int getFactorLinkCount()
	{
		return getAllDiagramFactorLinks().size();
	}

	public int getFactorLinksSize(ORef diagramFactorRef) throws Exception
	{
		FactorCell factorCell = getFactorCellByRef(diagramFactorRef);
		return getFactorLinks(factorCell).size();
	}
	
	// NOTE: Suppressing warnings because we are calling an unchecked JGraph method
	@SuppressWarnings("unchecked")
	public Set<LinkCell> getFactorLinks(FactorCell node)
	{
		return getEdges(this, new Object[] {node});
	}
	
	public LinkCell findLinkCell(DiagramLink link)
	{
		return cellInventory.getLinkCell(link);
	}
	
	public void updateVisibilityOfFactorsAndLinks() throws Exception
	{
		updateVisibilityOfFactors();
		updateVisibilityOfLinks();
	}
	
	
	private void updateVisibilityOfFactors() throws Exception
	{
		// TODO: Probably can handle GroupBox and ScopeBox toBack here also
		HashSet<FactorCell> topLayerCells = new HashSet<FactorCell>();
		
		Vector factorCells = getAllFactorCells();
		for(int i = 0; i < factorCells.size(); ++i)
		{
			FactorCell factorCell = (FactorCell)factorCells.get(i);
			updateVisibilityOfSingleFactor(factorCell.getDiagramFactorRef());
			if(factorCell.isTextBox() || factorCell.isActivity() || factorCell.isStress())
				topLayerCells.add(factorCell);
		}
		
		sortLayers();
	}	
	
	private void updateVisibilityOfSingleFactor(ORef diagramFactorRef) throws Exception
	{
		try
		{
			FactorCell factorCell = getFactorCellByRef(diagramFactorRef);
			boolean shouldBeVisible = shouldFactorCellBeVisible(factorCell);

			if(shouldBeVisible != getGraphLayoutCache().isVisible(factorCell))
				getGraphLayoutCache().setVisible(factorCell, shouldBeVisible);
		}
		catch (Exception e)
		{
			EAM.logException(e);
		}
	}

	private boolean shouldFactorCellBeVisible(FactorCell factorCell) throws Exception
	{
		boolean isLayerVisible = getLayerManager().isVisible(getDiagramObject(), factorCell);
		ORefList selectedTaggedRefs = getDiagramObject().getSelectedTaggedObjectSetRefs();
		if (selectedTaggedRefs.isEmpty())
			return isLayerVisible;
		
		boolean isTagged = getTaggedObjectManager().isVisible(getDiagramObject(), factorCell.getWrappedFactorRef());
			
		return isLayerVisible && isTagged;
	}

	private void updateVisibilityOfLinks() throws Exception
	{
		LinkCell[] linkCells = getAllFactorLinkCells();
		for(int i = 0; i < linkCells.length; ++i)
		{
			updateVisibilityOfSingleLink(linkCells[i]);
		}
	}	
	
	public void updateVisibilityOfSingleLink(LinkCell linkCell) throws Exception
	{
		boolean shouldLinkBeVisible = !linkCell.getDiagramLink().isCoveredByGroupBoxLink();
		if(!shouldFactorCellBeVisible(linkCell.getFrom()))
			shouldLinkBeVisible = false;
		if(!shouldFactorCellBeVisible(linkCell.getTo()))
			shouldLinkBeVisible = false;
		
		if(shouldLinkBeVisible != getGraphLayoutCache().isVisible(linkCell))
			getGraphLayoutCache().setVisible(linkCell, shouldLinkBeVisible);
	}

	public void updateCell(EAMGraphCell cellToUpdate) throws Exception
	{
		edit(getNestedAttributeMap(cellToUpdate), null, null, null);
		notifyListeners(createDiagramModelEvent(cellToUpdate), new ModelEventNotifierFactorChanged());
	}
	
	public void updateDiagramFactor(ORef diagramFactorRef) throws Exception
	{
		FactorCell cellToUpdate = getFactorCellByRef(diagramFactorRef);
		edit(getNestedAttributeMap(cellToUpdate), null, null, null);
		notifyListeners(createDiagramModelEvent(cellToUpdate), new ModelEventNotifierFactorChanged());
	}
	
	public boolean doesFactorExist(ORef factorRef)
	{
		return (rawGetFactorByWrappedRef(factorRef) != null);
	}
	
	public FactorCell getFactorCellByRef(ORef diagramFactorRef) throws Exception
	{
		diagramFactorRef.ensureExactType(DiagramFactorSchema.getObjectType());
		FactorCell factorCell = rawGetFactorCellByRef(diagramFactorRef);
		if(factorCell == null)
			throw new Exception("FactorCell doesn't exist, ref: " + diagramFactorRef);
		
		return factorCell;
	}
	
	public DiagramFactor getDiagramFactor(ORef factorRef)
	{
		if (!Factor.isFactor(factorRef.getObjectType()))
			throw new RuntimeException("Trying to get FactorId from non factor ref.");
		
		return rawGetFactorByWrappedRef(factorRef).getDiagramFactor();
	}
	
	public FactorCell getFactorCellByWrappedRef(ORef factorRef)
	{
		FactorCell factorCell = rawGetFactorByWrappedRef(factorRef);
		if(factorCell == null)
			EAM.logDebug("getDiagramFactorByWrappedId about to return null for: " + factorRef);
		
		return factorCell;
	}
	
	public boolean containsDiagramFactor(ORef diagramFactorRef)
	{
		diagramFactorRef.ensureExactType(DiagramFactorSchema.getObjectType());
		FactorCell node = rawGetFactorCellByRef(diagramFactorRef);

		return node != null;
	}

	private FactorCell rawGetFactorCellByRef(ORef diagramFactorRef)
	{
		return cellInventory.getFactorCellByDiagramFactorRef(diagramFactorRef);
	}
	
	private FactorCell rawGetFactorByWrappedRef(ORef factorRef)
	{
		return cellInventory.getFactorByRef(factorRef);
	}

	public LinkCell getLinkCell(DiagramLink diagramFactorLink)
	{
		return cellInventory.getLinkCell(diagramFactorLink);
	}
	
	public DiagramLink getDiagramLinkByRef(ORef diagramLinkRef) throws Exception
	{
		DiagramLink diagramLink = cellInventory.getDiagramLink(diagramLinkRef);
		if(diagramLink == null)
			throw new Exception("Link doesn't exist, ref: " + diagramLinkRef);
		
		return diagramLink;
	}

	public DiagramLink getDiagramLinkByWrappedRef(ORef factorLinkRef) throws Exception
	{
		DiagramLink diagramLink = cellInventory.getFactorLinkByRef(factorLinkRef);
		if(diagramLink == null)
			throw new Exception("Factor Link doesn't exist, ref: " + factorLinkRef);
		
		return diagramLink;
	}
	
	public boolean doesDiagramLinkExist(ORef factorLinkRef)
	{
		factorLinkRef.ensureExactType(FactorLinkSchema.getObjectType());
		DiagramLink diagramLink = cellInventory.getFactorLinkByRef(factorLinkRef);
		return (diagramLink != null);
	}
	
	public boolean doesDiagramFactorExist(ORef diagramFactorRef)
	{
		return (rawGetFactorCellByRef(diagramFactorRef) != null);
	}
	
	public boolean doesDiagramFactorLinkExist(DiagramLinkId linkId)
	{
		return doesDiagramFactorLinkExist(new ORef(DiagramLinkSchema.getObjectType(), linkId));	
	}
	
	public boolean doesDiagramFactorLinkExist(ORef diagramLinkRef)
	{
		return (cellInventory.getDiagramLink(diagramLinkRef) != null);	
	}
	
	public Vector getAllFactorCells()
	{
		return cellInventory.getAllFactors();
	}
	
	public Vector<DiagramFactor> getAllDiagramFactors()
	{
		return new Vector<DiagramFactor>(Arrays.asList(getAllDiagramFactorsAsArray()));
	}
	
	public DiagramFactor[] getAllDiagramFactorsAsArray()
	{
		Vector<DiagramFactor> allDiagramFactors = new Vector<DiagramFactor>();
		Vector allFactorCells = getAllFactorCells();
		for (int i = 0; i < allFactorCells.size(); i++)
		{
			FactorCell factorCell = (FactorCell) allFactorCells.get(i);
			allDiagramFactors.add(factorCell.getDiagramFactor());
		}
		
		return allDiagramFactors.toArray(new DiagramFactor[0]);
	}

	public LinkCell[] getAllFactorLinkCells()
	{
		return cellInventory.getAllFactorLinkCells().toArray(new LinkCell[0]);
	}
	
	public DiagramLink[] getAllDiagramLinksAsArray()
	{
		return getAllDiagramFactorLinks().toArray(new DiagramLink[0]);
	}
	
	public HashSet<EAMGraphCell> getAllSelectedCellsWithRelatedLinkages(Object[] rawSelectedCells) throws Exception 
	{
		Vector<EAMGraphCell> selectedCells = castRawObjectsToEAMGraphCells(rawSelectedCells);
		Vector<EAMGraphCell> selectedCellsWithGroupBoxChildren = new Vector<EAMGraphCell>();
		selectedCellsWithGroupBoxChildren.addAll(selectedCells);		
		selectedCellsWithGroupBoxChildren.addAll(getGroupBoxChildrenAndGroupLinkChildrenCells(selectedCells));
		
		HashSet<EAMGraphCell> selectedCellsWithLinkages = new HashSet<EAMGraphCell>();
		for(int i = 0; i < selectedCellsWithGroupBoxChildren.size(); ++i)
		{
			EAMGraphCell cell = selectedCellsWithGroupBoxChildren.get(i);
			if(cell.isFactorLink())
			{
				selectedCellsWithLinkages.add(cell);
			}
			else if(cell.isFactor())
			{
				FactorCell factorCell = (FactorCell) cell;
				selectedCellsWithLinkages.addAll(getFactorRelatedLinks(factorCell));
				selectedCellsWithLinkages.add(cell);
			}
		}
		return selectedCellsWithLinkages;
	}

	private Vector<EAMGraphCell> castRawObjectsToEAMGraphCells(Object[] rawSelectedCells)
	{
		Vector<EAMGraphCell> castedToEAMGraphCellObjects = new Vector<EAMGraphCell>();
		for (int i = 0; i < rawSelectedCells.length; ++i)
		{
			castedToEAMGraphCellObjects.add((EAMGraphCell) rawSelectedCells[i]);
		}
		
		return castedToEAMGraphCellObjects;
	}

	private HashSet<EAMGraphCell> getGroupBoxChildrenAndGroupLinkChildrenCells(Vector<EAMGraphCell> selectedCells) throws Exception
	{
		HashSet<EAMGraphCell> groupChildrenCells = new HashSet<EAMGraphCell>();
		for(int i = 0; i < selectedCells.size(); ++i)
		{
			EAMGraphCell cell = selectedCells.get(i);
			groupChildrenCells.addAll(getGroupBoxFactorChildren(cell));
			groupChildrenCells.addAll(getGroupLinkChildren(cell));
		}

		return groupChildrenCells;
	}

	public HashSet<LinkCell> getFactorRelatedLinks(FactorCell factorCell)
	{
		HashSet<LinkCell> factorRelatedLinks = new HashSet<LinkCell>();
		Set linkages = getFactorLinks(factorCell);
		for (Iterator iter = linkages.iterator(); iter.hasNext();) 
		{
			EAMGraphCell link = (EAMGraphCell) iter.next();
			factorRelatedLinks.add((LinkCell) link);
		}
		
		return factorRelatedLinks;
	}
	
	public HashSet<FactorCell> getGroupBoxFactorChildren(EAMGraphCell cell) throws Exception
	{
		if(!cell.isFactor())
			return new HashSet<FactorCell>();
		
		FactorCell factorCell = (FactorCell) cell;
		ORefList groupBoxChildrenRefs = factorCell.getDiagramFactor().getGroupBoxChildrenRefs();
		HashSet<FactorCell> groupBoxChildrenCells = new HashSet<FactorCell>();
		for (int childIndex = 0; childIndex < groupBoxChildrenRefs.size(); ++childIndex)
		{
			ORef childRef = groupBoxChildrenRefs.get(childIndex);
			FactorCell childCell = getFactorCellByRef(childRef);		
			groupBoxChildrenCells.add(childCell);
		}
		return groupBoxChildrenCells;
	}

	private HashSet<EAMGraphCell> getGroupLinkChildren(EAMGraphCell cell) throws Exception
	{
		if (!cell.isFactorLink())
			return new HashSet<EAMGraphCell>();
		
		LinkCell linkCell = (LinkCell) cell;
		ORefList groupLinkChildRefs = linkCell.getDiagramLink().getGroupedDiagramLinkRefs();
		HashSet<EAMGraphCell> groupLinkChildCells = new HashSet<EAMGraphCell>();
		for (int childIndex = 0; childIndex < groupLinkChildRefs.size(); ++childIndex)
		{
			ORef childRef = groupLinkChildRefs.get(childIndex);
			DiagramLink diagramLink  = DiagramLink.find(getProject(), childRef);
			LinkCell childLinkCell = findLinkCell(diagramLink);		
			groupLinkChildCells.add(childLinkCell);
		}
		return groupLinkChildCells;
	}
	
	public Vector<DiagramLink> getAllDiagramFactorLinks()
	{
		return cellInventory.getAllFactorLinks();
	}
	
	public void fillFrom(DiagramObject diagramContentsToUse) throws Exception
	{
		diagramContents = diagramContentsToUse;
		
		clear();
		addFactorsToModel(diagramContents.toJson());
		addLinksToModel(diagramContents.toJson());
		reloadLayerManager(diagramContentsToUse);
		
		if (isDamaged())
			EAM.errorDialog(EAM.text("An error is preventing this diagram from displaying correctly. " +
				 "Most likely, the project has gotten corrupted. Please contact " +
				 "the Miradi team for help and advice. We recommend that you not " +
				 "make any changes to this project until this problem has been resolved."));
	}

	private void reloadLayerManager(DiagramObject diagramContentsToUse)
	{
		getLayerManager().setDiagramObject(diagramContentsToUse);
	}

	private void addFactorsToModel(EnhancedJsonObject json) throws Exception
	{
		IdList diagramFactorIds = new IdList(DiagramFactorSchema.getObjectType(), json.getString(TAG_DIAGRAM_FACTOR_IDS));
		for(int i = 0; i < diagramFactorIds.size(); ++i)
		{
			try
			{
				DiagramFactor diagramFactor = (DiagramFactor) project.findObject(ObjectType.DIAGRAM_FACTOR, diagramFactorIds.get(i));
				addDiagramFactor(diagramFactor);
			}
			catch (Exception e)
			{
				EAM.logException(e);
				isDamaged = true;
			}
		}
	}
	
	public void addLinksToModel(EnhancedJsonObject json) throws Exception
	{
		IdList allDiagramFactorLinkIds = new IdList(DiagramLinkSchema.getObjectType(), json.getString(TAG_DIAGRAM_FACTOR_LINK_IDS));
		for (int i = 0; i < allDiagramFactorLinkIds.size(); i++)
		{
			BaseId factorLinkId = allDiagramFactorLinkIds.get(i);
			DiagramLink diagramFactorLink = (DiagramLink) project.findObject(new ORef(ObjectType.DIAGRAM_LINK, factorLinkId));
			addLinkToDiagram(diagramFactorLink);
		}
	}
	
	public LinkCell updateCellFromDiagramFactorLink(ORef diagramLinkRef) throws Exception
	{
		if (!DiagramLink.is(diagramLinkRef))
			throw new Exception("ORef is not of type DiagramLink : ref = " + diagramLinkRef);
		
		if (! doesDiagramFactorLinkExist(diagramLinkRef))
			return null;
		
		DiagramLink diagramFactorLink  = getDiagramLinkByRef(diagramLinkRef);
		LinkCell linkCell = getLinkCell(diagramFactorLink);
		linkCell.updateFromDiagramFactorLink();
		updateCell(linkCell);
		return linkCell;
	}

	public void updateCellFromDiagramFactor(ORef diagramFactorRef) throws Exception
	{
		if (! doesDiagramFactorExist(diagramFactorRef))
			return;
			
		FactorCell factorCell = getFactorCellByRef(diagramFactorRef);
		factorCell.updateFromDiagramFactor();
		updateCell(factorCell);
	}

	public DiagramFactorPool getDiagramFactorPool()
	{
		return project.getDiagramFactorPool();
	}
	
	public DiagramLinkPool getDiagramFactorLinkPool()
	{
		return project.getDiagramFactorLinkPool();
	}
	
	FactorLinkPool getFactorLinkPool()
	{
		return project.getFactorLinkPool();
	}
	
	ObjectivePool getObjectivePool()
	{
		return project.getObjectivePool();
	}
	
	GoalPool getGoalPool()
	{
		return project.getGoalPool();
	}
	
	public Vector<FactorCell> getAllDiagramTargets()
	{
		return getFactorCells(TargetSchema.getObjectType());
	}

	public Vector<FactorCell> getFactorCells(int wrappedType)
	{
		Vector<FactorCell> allFactorCells = new Vector<FactorCell>();
		Vector allFactors = getAllFactorCells();
		for (int i = 0; i < allFactors.size(); i++)
		{
			FactorCell factorCell = (FactorCell)allFactors.get(i);
			if (factorCell.getWrappedType() == wrappedType)
				allFactorCells.add(factorCell);
		}
		
		return allFactorCells;
	}
	
	public void updateGroupBoxCells() throws Exception
	{
		Vector<FactorCell> allGroupBoxes = getAllGroupBoxCells();
		for (int i = 0; i < allGroupBoxes.size(); ++i)
		{
			DiagramGroupBoxCell cell = (DiagramGroupBoxCell) allGroupBoxes.get(i);
			cell.autoSurroundChildren();
		}
		
		sortLayers();
	}

	public Vector<FactorCell> getAllGroupBoxCells()
	{
		Vector<FactorCell> allGroupBoxCells = new Vector<FactorCell>();
		Vector allFactors = getAllFactorCells();
		for (int i = 0; i < allFactors.size(); i++)
		{
			FactorCell factorCell = (FactorCell)allFactors.get(i);
			if (factorCell.isGroupBox())
				allGroupBoxCells.add(factorCell);
		}
		
		return allGroupBoxCells;	
	}
	
	public DiagramObject getDiagramObject()
	{
		return diagramContents;
	}
	
	public LayerManager getLayerManager()
	{
		return layerManager;
	}
	
	public TaggedObjectManager getTaggedObjectManager()
	{
		return taggedObjectManager;
	}
	
	public GraphLayoutCache getGraphLayoutCache()
	{
		return graphLayoutCache;
	}
	
	private boolean isDamaged()
	{
		return isDamaged;
	}
	
	abstract public boolean shouldSaveChangesToDisk();
		
	public static final String TAG_DIAGRAM_FACTOR_IDS = "DiagramFactorIds";
	public static final String TAG_DIAGRAM_FACTOR_LINK_IDS = "DiagramFactorLinkIds";
	
	private Project project;
	private CellInventory cellInventory;
	protected List<DiagramModelListener> diagramModelListenerList = new ArrayList<DiagramModelListener>();
	
	private DiagramObject diagramContents;
	
	private GraphLayoutCache graphLayoutCache;
	private boolean isDamaged;
	private LayerManager layerManager;
	private TaggedObjectManager taggedObjectManager;
}

