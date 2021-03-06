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
package org.miradi.dialogs.stress;

import org.miradi.actions.ActionHideStressBubble;
import org.miradi.actions.ActionShowStressBubble;
import org.miradi.dialogs.base.AbstractFactorBubbleVisibilityPanel;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;

public class StressFactorVisibilityControlPanel extends AbstractFactorBubbleVisibilityPanel
{
	public StressFactorVisibilityControlPanel(MainWindow mainWindow) throws Exception
	{
		super(mainWindow, ObjectType.STRESS);		
	}

	@Override
	public String getPanelDescription()
	{
		return EAM.text("Title|Stress Visibility");
	}

	@Override
	protected String getExplanationMessage()
	{
		return EAM.text("Stresses can only be shown on Conceptual Model pages, not Results Chains");
	}

	@Override
	protected Class getHideButtonClass()
	{
		return ActionHideStressBubble.class;
	}

	@Override
	protected Class getShowButtonClass()
	{
		return ActionShowStressBubble.class;
	}
	
	@Override
	protected boolean shouldShowButtonPanel()
	{
		return getMainWindow().getCurrentDiagramComponent().getDiagramObject().isConceptualModelDiagram();
	}	
}
