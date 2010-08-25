/* 
Copyright 2005-2009, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.views.umbrella;

import java.awt.Container;

import org.miradi.utils.SplitterPositionSaverAndGetter;

public class PersistentHorizontalSplitPane extends PersistentSplitPane
{
	public PersistentHorizontalSplitPane(Container futureParent, SplitterPositionSaverAndGetter splitPositionSaverToUse, String splitterNameToUse)
	{
		super(futureParent, splitPositionSaverToUse, splitterNameToUse);
		setOrientation(HORIZONTAL_SPLIT);
	}

	protected int getContainerHeightOrWidth()
	{
		return getParent().getWidth();
	}

}
