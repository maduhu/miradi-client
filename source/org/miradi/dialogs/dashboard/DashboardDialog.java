/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.dialogs.dashboard;

import org.miradi.dialogs.base.ModalDialogWithClose;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;

public class DashboardDialog extends ModalDialogWithClose
{
	public DashboardDialog(MainWindow parent) throws Exception
	{
		super(parent, EAM.text("Dashboard"));
		
		createPanel();
		addPanel();
	}
	
	private void createPanel() throws Exception
	{
		mainPanel = new DashboardMainPanel(getMainWindow());
	}

	private void addPanel()
	{
		setMainPanel(mainPanel);
	}
	
	private DashboardMainPanel mainPanel;
}