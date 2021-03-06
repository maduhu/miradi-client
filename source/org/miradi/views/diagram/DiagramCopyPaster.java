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

import org.miradi.diagram.DiagramModel;
import org.miradi.dialogs.diagram.DiagramPanel;
import org.miradi.main.AbstractTransferableMiradiList;
import org.miradi.objecthelpers.ORef;
import org.miradi.objects.Stress;
import org.miradi.objects.Task;
import org.miradi.utils.EnhancedJsonObject;

public class DiagramCopyPaster extends DiagramPaster
{
	public DiagramCopyPaster(DiagramPanel diagramPanelToUse, DiagramModel modelToUse, AbstractTransferableMiradiList transferableListToUse)
	{
		super(diagramPanelToUse, modelToUse, transferableListToUse);
	}

	@Override
	public void pasteFactorsAndLinks(Point startPoint) throws Exception
	{	
		pasteFactors(startPoint);
		createNewFactorLinks();
		createNewDiagramLinks();		
		updateAutoCreatedThreatStressRatings();
		selectNewlyPastedItems();
	}
	
	@Override
	public void pasteFactors(Point startPoint) throws Exception
	{	
		dataHelper = new PointManipulator(startPoint, transferableList.getUpperMostLeftMostCorner());
		createNewFactorsAndContents();	
		createNewDiagramFactors();
	}
	
	@Override
	public ORef getCorrespondingNewRef(ORef oldWrappedRef) throws Exception
	{
		ORef foundRef = getOldToNewObjectRefMap().get(oldWrappedRef);
		if (foundRef == null)
		{
			if (Stress.is(oldWrappedRef) || Task.is(oldWrappedRef))
				return oldWrappedRef;
		}
		
		return foundRef;
	}
	
	@Override
	public ORef getFactorLinkRef(ORef oldWrappedFactorLinkRef)
	{
		if(oldWrappedFactorLinkRef.isInvalid())
			return oldWrappedFactorLinkRef;
		
		return getOldToNewObjectRefMap().get(oldWrappedFactorLinkRef);
	}
	
	@Override
	protected boolean shouldCreateObject(ORef ref, EnhancedJsonObject json)
	{
		return true;
	}
}
