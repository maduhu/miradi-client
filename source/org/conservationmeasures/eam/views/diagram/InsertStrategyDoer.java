/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.diagram.cells.DiagramStrategyCell;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.objecthelpers.ObjectType;

public class InsertStrategyDoer extends InsertFactorDoer
{
	public boolean isAvailable()
	{
		if (!getProject().isOpen()) 
			return false;
		
		return true;
	}
	
	public int getTypeToInsert()
	{
		return ObjectType.STRATEGY;
	}

	public String getInitialText()
	{
		return EAM.text("Label|New Strategy");
	}

	public void forceVisibleInLayerManager()
	{
		getProject().getLayerManager().setVisibility(DiagramStrategyCell.class, true);
	}
	
	
}
