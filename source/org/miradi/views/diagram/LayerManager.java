/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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

import java.util.HashSet;
import java.util.Set;

import org.miradi.diagram.cells.FactorCell;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.ViewData;

public class LayerManager
{
	public LayerManager()
	{
		hiddenNodeTypes = new HashSet();
		hiddenORefs = new ORefList();
		mode = ViewData.MODE_DEFAULT;
		contributingFactorsVisibleFlag = true;
		directThreatsVisibleFlag = true;
		linkagesVisibleFlag = true;
		targetLinkagesVisibleFlag = true;
		goalsVisibleFlag = true;
		objectivesVisibleFlag = true;
		indicatorsVisibleFlag = true;
		scopeBoxVisibleFlag = true;
		stressesVisibleFlag = true;
		intermediateResultFlag = true;
		threatReductionResultFlag = true;
		textBoxesVisibleFlag = true;
		groupBoxesVisibleFlag = true;
		draftStrategyVisibleFlag = true;
	}
	
	public boolean isVisible(DiagramObject diagramObject, FactorCell node)
	{
		if(hiddenORefs.contains(node.getWrappedORef()))
			return false;
		
		boolean isDraft = node.getUnderlyingObject().isStatusDraft();
		if (isDraft)
		{
			if (isResultsChain(diagramObject))
				return false;

			if(mode.equals(ViewData.MODE_STRATEGY_BRAINSTORM))
				return areDraftsVisible(node);
			
			return false;
		}
		
		if(node.isContributingFactor())
			return areContributingFactorsVisible();
		
		if(node.isDirectThreat())
			return areDirectThreatsVisible();
		
		if (node.isIntermediateResult())
			return areIntermediateResultsVisible();
		
		if (node.isThreatRedectionResult())
			return areThreatReductionResultsVisible();


		if(isTypeVisible(node.getClass()))
			return true;
		
		return false;
	}

	private boolean isResultsChain(DiagramObject diagramObject)
	{
		if (diagramObject == null)
			return false;
		
		return diagramObject.isResultsChain();
	}

	public boolean isTypeVisible(Class nodeClass)
	{
		return !hiddenNodeTypes.contains(nodeClass);
	}
	
	public void setVisibility(Class nodeClass, boolean newVisibility)
	{
		if(newVisibility)
			hiddenNodeTypes.remove(nodeClass);
		else
			hiddenNodeTypes.add(nodeClass);
	}
	
	public boolean areAllNodesVisible()
	{
		return hiddenNodeTypes.isEmpty() && hiddenORefs.isEmpty();
	}
	
	public void setHiddenORefs(ORefList oRefsToHide)
	{
		hiddenORefs = new ORefList(oRefsToHide);
	}
	
	public void setMode(String newMode)
	{
		mode = newMode;
	}
	
	public boolean areContributingFactorsVisible()
	{
		return contributingFactorsVisibleFlag;
	}
	
	public void setContributingFactorsVisible(boolean newSetting)
	{
		contributingFactorsVisibleFlag = newSetting;
	}
	
	public boolean areDirectThreatsVisible()
	{
		return directThreatsVisibleFlag;
	}
	
	public void setDirectThreatsVisible(boolean newSetting)
	{
		directThreatsVisibleFlag = newSetting;
	}
	
	public boolean areFactorLinksVisible()
	{
		return linkagesVisibleFlag;
	}
	
	public void setFactorLinksVisible(boolean newSetting)
	{
		linkagesVisibleFlag = newSetting;
	}

	public boolean areTargetLinksVisible()
	{
		return targetLinkagesVisibleFlag;
	}
	
	public void setTargetLinksVisible(boolean newSetting)
	{
		targetLinkagesVisibleFlag = newSetting;
	}

	public boolean areGoalsVisible()
	{
		return goalsVisibleFlag;
	}
	
	public boolean areObjectivesVisible()
	{
		return objectivesVisibleFlag;
	}
	
	public void setGoalsVisible(boolean newSetting)
	{
		goalsVisibleFlag = newSetting;
	}
	
	public void setObjectivesVisible(boolean newSetting)
	{
		objectivesVisibleFlag = newSetting;
	}
	
	public boolean areTextBoxesVisible()
	{
		return textBoxesVisibleFlag;
	}
	
	public boolean areGroupBoxesVisible()
	{
		return groupBoxesVisibleFlag;
	}
	
	public boolean areDraftsVisible(FactorCell node)
	{
		if (!node.isStrategy())
			throw new RuntimeException("Unexpected non strategy draft");
		
		return areDraftStrategiesVisible();
	}

	public boolean areDraftStrategiesVisible()
	{
		return draftStrategyVisibleFlag;
	}
	
	public void setDraftStrategiesVisible(boolean newSetting)
	{
		draftStrategyVisibleFlag = newSetting;
	}

	public boolean areIndicatorsVisible()
	{
		return indicatorsVisibleFlag;
	}
	
	public void setIndicatorsVisible(boolean newSetting)
	{
		indicatorsVisibleFlag = newSetting;
	}

	public boolean isScopeBoxVisible()
	{
		return scopeBoxVisibleFlag;
	}
	
	public void setScopeBoxVisible(boolean newSetting)
	{
		scopeBoxVisibleFlag = newSetting;
	}
	
	public boolean areStressesVisible()
	{
		return stressesVisibleFlag;
	}
	
	public void setStressesVisible(boolean newSetting)
	{
		stressesVisibleFlag = newSetting;
	}
	
	public boolean areIntermediateResultsVisible()
	{
		return intermediateResultFlag;
	}
	
	public void setIntermediateResultVisible(boolean newSetting)
	{
		intermediateResultFlag = newSetting;
	}
	
	public boolean areThreatReductionResultsVisible()
	{
		return threatReductionResultFlag;
	}
	
	public void setThreatReductionResultVisible(boolean newSetting)
	{
		threatReductionResultFlag = newSetting;
	}
	
	Set hiddenNodeTypes;
	ORefList hiddenORefs;
	String mode;
	boolean contributingFactorsVisibleFlag;
	boolean directThreatsVisibleFlag;
	boolean linkagesVisibleFlag;
	boolean targetLinkagesVisibleFlag;
	boolean goalsVisibleFlag;
	boolean objectivesVisibleFlag;
	boolean indicatorsVisibleFlag;
	boolean scopeBoxVisibleFlag;
	boolean stressesVisibleFlag;
	boolean intermediateResultFlag;
	boolean threatReductionResultFlag;
	boolean textBoxesVisibleFlag;
	boolean groupBoxesVisibleFlag;
	boolean draftStrategyVisibleFlag;
}
