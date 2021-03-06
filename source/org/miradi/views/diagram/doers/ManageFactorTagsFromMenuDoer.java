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
package org.miradi.views.diagram.doers;

import org.miradi.diagram.cells.FactorCell;
import org.miradi.objects.Factor;

public class ManageFactorTagsFromMenuDoer extends AbstractManageFactorTagDoer
{	
	@Override
	public boolean isAvailable()
	{
		boolean superIsAvailable = super.isAvailable();
		if (!superIsAvailable)
			return false;
		
		return getOnlySelectedFactorCells().length == 1;
	}
	
	@Override
	protected Factor getSingleSelectedFactor()
	{
		FactorCell[] selectedCells = getOnlySelectedFactorCells();
		if (selectedCells.length == 0)
			return null;
	
		return selectedCells[0].getWrappedFactor();
	}

	private FactorCell[] getOnlySelectedFactorCells()
	{
		return getDiagramView().getDiagramPanel().getOnlySelectedFactorCells();
	}
}
