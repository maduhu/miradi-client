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
package org.miradi.dialogs.threatrating.upperPanel;

import java.awt.BorderLayout;

import javax.swing.event.ListSelectionEvent;

import org.miradi.dialogs.MultiTableUpperPanel;
import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.views.umbrella.ObjectPicker;

public class ThreatRatingUpperPanel extends MultiTableUpperPanel
{
	public static ThreatRatingUpperPanel createThreatStressRatingListTablePanel(MainWindow mainWindowToUse, ThreatRatingMultiTablePanel threatStressRatingMultiTablePanel, AbstractObjectDataInputPanel propertiesPanel) throws Exception
	{
		return new ThreatRatingUpperPanel(mainWindowToUse, threatStressRatingMultiTablePanel, propertiesPanel);
	}
	
	private ThreatRatingUpperPanel(MainWindow mainWindowToUse, ThreatRatingMultiTablePanel multiTablePanelToUse, AbstractObjectDataInputPanel propertiesPanelToUse)
	{
		super(mainWindowToUse, multiTablePanelToUse.getObjectPicker());

		multiTablePanel = multiTablePanelToUse;
		propertiesPanel = propertiesPanelToUse;

		// NOTE: Replace scroll pane that super automatically added
		add(multiTablePanelToUse, BorderLayout.CENTER);
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		multiTablePanel.dispose();
	}
	
	public ThreatRatingMultiTablePanel getMultiTablePanel()
	{
		return multiTablePanel;
	}
	
	public ObjectPicker getObjectPicker()
	{
		return getMultiTablePanel();
	}

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		super.valueChanged(event);
		ORefList[] selectedHierarchies = multiTablePanel.getSelectedHierarchies();
		propertiesPanel.setObjectRefs(selectedHierarchies[0]);
	}
	
	@Override
	public void handleCommandEventImmediately(CommandExecutedEvent event)
	{
		repaint();	
	}

	@Override
	public BaseObject getSelectedObject()
	{
		return null;
	}
	
	private AbstractObjectDataInputPanel propertiesPanel;
	private ThreatRatingMultiTablePanel multiTablePanel;
}
