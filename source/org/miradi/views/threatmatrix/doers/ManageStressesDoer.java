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
package org.miradi.views.threatmatrix.doers;

import org.martus.swing.Utilities;
import org.miradi.dialogs.stress.ManageStressesDialog;
import org.miradi.dialogs.stress.StressListManagementPanel;
import org.miradi.exceptions.CommandFailedException;
import org.miradi.objects.BaseObject;
import org.miradi.objects.Target;
import org.miradi.schemas.TargetSchema;
import org.miradi.views.ObjectsDoer;

public class ManageStressesDoer extends ObjectsDoer
{
	@Override
	public boolean isAvailable()
	{
		if (getSingleSelected(TargetSchema.getObjectType()) == null)
			return false;
		
		return true;
	}

	@Override
	protected void doIt() throws Exception
	{
		if (!isAvailable())
			return;
		
		try
		{
			BaseObject selectedTarget = getSingleSelected(TargetSchema.getObjectType());
			// NOTE: Should create Stress panel without visibility panel
			ManageStressesDialog manageStressesDialog = new ManageStressesDialog(getMainWindow(), (Target)selectedTarget);
			StressListManagementPanel stressListManagementPanel = StressListManagementPanel.createStressManagementPanelWithoutVisibilityPanel(manageStressesDialog, getMainWindow(), selectedTarget.getRef());
			manageStressesDialog.setMainPanel(stressListManagementPanel);
			stressListManagementPanel.becomeActive();
			Utilities.centerDlg(manageStressesDialog);
			stressListManagementPanel.updateSplitterLocation();
			manageStressesDialog.setVisible(true);
		}
		catch (Exception e)
		{
			throw new CommandFailedException(e);
		}
	}
}
