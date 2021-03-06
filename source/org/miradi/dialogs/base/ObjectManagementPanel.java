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

import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.BaseObject;
import org.miradi.project.Project;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.TableExporter;
import org.miradi.views.MiradiTabContentsPanelInterface;

abstract public class ObjectManagementPanel extends VerticalSplitPanel implements MiradiTabContentsPanelInterface
{
	public ObjectManagementPanel(MainWindow mainWindowToUse, ObjectCollectionPanel tablePanelToUse, AbstractObjectDataInputPanel propertiesPanelToUse, String uniqueTreeTableIdentifier) throws Exception
	{
		super(mainWindowToUse, tablePanelToUse, propertiesPanelToUse, uniqueTreeTableIdentifier);
		
		initializeManagementPanel(mainWindowToUse, tablePanelToUse, propertiesPanelToUse);
	}
	
	public ObjectManagementPanel(MainWindow mainWindowToUse, ObjectCollectionPanel tablePanelToUse, AbstractObjectDataInputPanel propertiesPanelToUse) throws Exception
	{
		super(mainWindowToUse, tablePanelToUse, propertiesPanelToUse);
		
		initializeManagementPanel(mainWindowToUse, tablePanelToUse, propertiesPanelToUse);
	}

	private void initializeManagementPanel(MainWindow mainWindowToUse, ObjectCollectionPanel tablePanelToUse, AbstractObjectDataInputPanel propertiesPanelToUse)
	{
		mainWindow = mainWindowToUse;
		setDividerThick();
		listComponent = tablePanelToUse;
		propertiesPanel = propertiesPanelToUse;
		listComponent.setPropertiesPanel(propertiesPanel);
	}
	
	@Override
	public void dispose()
	{
		listComponent.setPropertiesPanel(null);
		disposePanel(listComponent);
		listComponent = null;
		
		disposePanel(propertiesPanel);
		propertiesPanel = null;
		
		super.dispose();
	}
	
	@Override
	public void becomeActive()
	{
		getListPanel().becomeActive();
		propertiesPanel.becomeActive();
		final ORefList selectionHierarchy = getListPanel().getPicker().getSelectionHierarchy();
		if (selectionHierarchy != null)
			propertiesPanel.setObjectRefs(selectionHierarchy);
		
		updateSplitterLocation();
	}
	
	@Override
	public void becomeInactive()
	{
		ObjectCollectionPanel listPanel = getListPanel();
		if(listPanel != null)
			listPanel.becomeInactive();
		if(propertiesPanel != null)
			propertiesPanel.becomeInactive();
	}

	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public Project getProject()
	{
		return getMainWindow().getProject();
	}

	public ObjectCollectionPanel getListPanel()
	{
		return listComponent;
	}
	
	@Override
	public BaseObject getObject()
	{
		if(listComponent == null)
			return null;
		return listComponent.getSelectedObject();
	}
	
	public DisposablePanelWithDescription getTabContentsComponent()
	{
		return this;
	}

	public Icon getIcon()
	{
		return null;
	}

	public String getTabName()
	{
		return getPanelDescription();
	}
	
	public boolean isImageAvailable()
	{
		return false;
	}
	
	public BufferedImage getImage(int scale) throws Exception
	{
		return null;
	}
	
	public boolean isExportableTableAvailable()
	{
		return false;
	}
	
	public TableExporter getTableExporter() throws Exception
	{
		return null;
	}
	
	public JComponent getPrintableComponent() throws Exception
	{
		return null;
	}
	
	public boolean isPrintable()
	{
		return false;
	}

	public boolean isRtfExportable()
	{
		return false;
	}
		
	public void exportRtf(RtfWriter writer) throws Exception
	{
	}

	private MainWindow mainWindow;
	private ObjectCollectionPanel listComponent;
	public AbstractObjectDataInputPanel propertiesPanel;
}
