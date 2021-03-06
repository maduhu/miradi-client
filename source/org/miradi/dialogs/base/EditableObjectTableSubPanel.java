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

package org.miradi.dialogs.base;

import java.awt.BorderLayout;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.miradi.actions.Actions;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.layout.OneColumnGridLayout;
import org.miradi.layout.OneRowPanel;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.project.Project;
import org.miradi.utils.MiradiScrollPane;
import org.miradi.views.umbrella.ObjectPicker;

abstract public class EditableObjectTableSubPanel extends ObjectDataInputPanel
{
	public EditableObjectTableSubPanel(Project projectToUse, int ObjectType) throws Exception
	{
		super(projectToUse, ORef.createInvalidWithType(ObjectType));
		
		setLayout(new OneColumnGridLayout());
		
		rowCountLanel = new PanelTitleLabel();
		createTable();
		addComponents();
		updateFieldsFromProject();
	}
	
	@Override
	public void dispose()
	{
		super.dispose();
		objectTable.dispose();
	}
	
	@Override
	public void becomeActive()
	{
		super.becomeActive();
		
		objectTable.becomeActive();
	}
	
	@Override
	public void becomeInactive()
	{
		objectTable.becomeInactive();

		super.becomeInactive();
	}
	
	private void refreshModel()
	{
		ORefList[] selectedHierarchies = getPicker().getSelectedHierarchies();
		if (selectedHierarchies.length > 0)
		{
			setObjectRefs(selectedHierarchies[0].toArray());
			stopCellEditingBecauseSelectionDoeesNotMatchPreviousObjectForRow(objectTable);
		}
	}
	
	@Override
	public void setObjectRefs(ORef[] hierarchyToSelectedRef)
	{
		super.setObjectRefs(hierarchyToSelectedRef);
		
		objectTable.stopCellEditing();
		objectTableModel.setObjectRefs(new ORefList(hierarchyToSelectedRef));
		objectTableModel.fireTableDataChanged();		
		rowCountLanel.setText(getPanelDescription() + ": " + objectTableModel.getRowCount());
		objectTable.sortTable();
	}
	
	protected Actions getActions()
	{
		return getMainWindow().getActions();
	}
	
	private JPanel createButtonBar()
	{
		OneRowPanel box = new OneRowPanel();
		box.setBackground(AppPreferences.getDataPanelBackgroundColor());
		box.setGaps(3);
		LinkedHashMap<Class, ObjectPicker> buttonsMap = getButtonsActionsPickerMap();
		Set<Class> buttonClasses = buttonsMap.keySet();
		for(Class buttonClass : buttonClasses)
		{
			box.add(createObjectsActionButton(getActions().getObjectsAction(buttonClass), buttonsMap.get(buttonClass)));
		}
		
		return box;
	}

	private void addComponents()
	{
		MiradiScrollPane scroller = new MiradiScrollPane(objectTable);
		add(createButtonBar(), BorderLayout.PAGE_START);
		add(rowCountLanel);
		add(scroller, BorderLayout.CENTER);
	}
	
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		
		if (event.isSetDataCommandWithThisType(getEditableObjectType()))
			objectTableModel.fireTableDataChanged();
		
		if (shouldRefreshModel(event))
			refreshModel();
	}

	private void stopCellEditingBecauseSelectionDoeesNotMatchPreviousObjectForRow(DynamicWidthEditableObjectTable table)
	{
		SwingUtilities.invokeLater(new CellEditStopper(table));
	}
	
	abstract protected boolean shouldRefreshModel(CommandExecutedEvent event);
	
	abstract protected int getEditableObjectType();
	
	abstract protected LinkedHashMap<Class, ObjectPicker> getButtonsActionsPickerMap();
	
	abstract protected void createTable() throws Exception;
	
	private class CellEditStopper implements Runnable
	{
		public CellEditStopper(DynamicWidthEditableObjectTable tableToUse)
		{
			table = tableToUse;
		}

		public void run()
		{
			table.stopCellEditing();
		}

		private DynamicWidthEditableObjectTable table;
	}
	
	private PanelTitleLabel rowCountLanel;
	protected EditableObjectRefsTableModel objectTableModel;
	protected DynamicWidthEditableObjectTable objectTable;
}
