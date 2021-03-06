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
package org.miradi.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.miradi.actions.Actions;
import org.miradi.commands.Command;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.dialogs.fieldComponents.PanelTabbedPane;
import org.miradi.layout.OneRowPanel;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objects.ViewData;
import org.miradi.rtf.RtfWriter;
import org.miradi.utils.MiradiResourceImageIcon;
import org.miradi.utils.MiradiScrollPane;
import org.miradi.utils.TableExporter;
import org.miradi.views.umbrella.UmbrellaView;
import org.miradi.wizard.SkeletonWizardStep;

abstract public class TabbedView extends UmbrellaView
{
	public TabbedView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);

		clearCurrentTabIndex();
		tabs = new PanelTabbedPane();
		tabs.addChangeListener(new TabChangeListener());
		tabs.setFocusable(false);
		tabs.addMouseListener(new MouseHandler());
		
		tabPanels = new HashMap<String, MiradiTabContentsPanelInterface>();
		
		setBackground(AppPreferences.getDarkPanelBackgroundColor());
		setBorder(BorderFactory.createEmptyBorder(3,0,3,0));
	}

	private void clearCurrentTabIndex()
	{
		currentTabIndex = -1;
	}

	public void deleteTabs() throws Exception
	{
		tabPanels.clear();
	}
	
	public JPopupMenu getTabPopupMenu()
	{
		return null;
	}
	
	@Override
	public void setTabForStep(SkeletonWizardStep step)
	{
		String tabIdentifier = step.getTabIdentifier();
		if (tabIdentifier == null)
			return;
		
		for (int index = 0; index < getTabCount(); ++index)
		{
			String thisTabIdentifier = getTabIdentifier(index);
			if (thisTabIdentifier.equals(tabIdentifier))
				setTab(index);
		}
	}
	
	@Override
	public void becomeActive() throws Exception
	{
		super.becomeActive();
		removeAll();
		add(tabs, BorderLayout.CENTER);
		
		ignoreTabChanges = true;
		try
		{
			createTabs();
		}
		catch (Exception e)
		{
			EAM.panic(e);
		}
		finally
		{
			ignoreTabChanges = false;
		}

		int desiredTab = getViewData().getCurrentTab();
		EAM.logVerbose("Selecting tab " + desiredTab);
		if(desiredTab >= tabs.getTabCount())
		{
			EAM.logDebug("Ignoring setting tab selection, setting selection to 0");
			desiredTab = 0;
		}
		
		//NOTE Must validate before setting splitter to ensure that the height is not 0 during construction
		forceLayoutSoSplittersWork();
		setTab(desiredTab);
	}

	@Override
	public void becomeInactive() throws Exception
	{
		ignoreTabChanges = true;
		try
		{
			prepareForTabSwitch();
			deleteTabs();
			tabs.removeAll();
		}
		finally
		{
			ignoreTabChanges = false;
		}
		super.becomeInactive();
	}
	
	public int getTabCount()
	{
		return tabs.getTabCount();
	}
	
	public int getSelectedTabIndex()
	{
		return tabs.getSelectedIndex();
	}
	
	public String getSelectedTabName()
	{
		int selectedTabIndex = getSelectedTabIndex();
		if(selectedTabIndex < 0)
			return "";
		return getTabTitle(selectedTabIndex);
	}

	private String getTabIdentifier(int index)
	{
		MiradiTabContentsPanelInterface tabContentsPanel = getTabPanel(index);
		return tabContentsPanel.getTabContentsComponent().getPanelIdentifier();
	}
	
	public int getTabIndex(String identifier)
	{
		for (int index = 0; index < getTabCount(); ++index)
		{
			if (identifier.equals(getTabIdentifier(index)))
				return index;
		}
		
		throw new RuntimeException("Could not find tab index for the identifier = " + identifier);
	}
	
	private MiradiTabContentsPanelInterface getTabPanel(int index)
	{
		String tabName = getTabTitle(index);
		return getTabPanel(tabName);
	}
		
	public String getTabTitle(int selectedTabIndex)
	{
		return tabs.getTitleAt(selectedTabIndex);
	}
	
	public MiradiTabContentsPanelInterface getSelectedTabPanel()
	{
		return getTabPanel(getSelectedTabName());
	}
	
	public void addScrollingTab(MiradiTabContentsPanelInterface contents)
	{
		addTab(contents, new MiradiScrollPane(contents.getTabContentsComponent()), contents.getJumpActionClass());
	}

	public void addNonScrollingTab(MiradiTabContentsPanelInterface contents)
	{
		addTab(contents, contents.getTabContentsComponent(), contents.getJumpActionClass());
	}
	
	private void addTab(MiradiTabContentsPanelInterface contents, Component tabComponent, Class jumpClass)
	{
		String tabName = contents.getTabName();
		tabPanels.put(tabName, contents);

		if(jumpClass == null)
		{
			tabs.addTab(tabName, contents.getIcon(), tabComponent);
			return;
		}

		Actions actions = getMainWindow().getActions();
		PanelButton directionsButton = new PanelButton(new DirectionsAction(actions, jumpClass));

		OneRowPanel buttonPanel = new OneRowPanel();
		buttonPanel.setBackground(AppPreferences.getDarkPanelBackgroundColor());
		buttonPanel.add(directionsButton);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(tabComponent, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.AFTER_LAST_LINE);
		tabs.addTab(tabName, contents.getIcon(), panel);
	}
	
	static class DirectionsAction extends AbstractAction
	{
		public DirectionsAction(Actions actionsToUse, Class jumpActionClassToUse)
		{
			super(EAM.text("Instructions"), new MiradiResourceImageIcon("icons/directions.png"));
			actions = actionsToUse;
			jumpActionClass = jumpActionClassToUse;
			setEnabled(jumpActionClass != null);
				
		}
		
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				actions.get(jumpActionClass).doAction();
			}
			catch(Exception e)
			{
				EAM.panic(e);
			}
		}

		private Actions actions;
		private Class jumpActionClass;
	}
	
	public MiradiTabContentsPanelInterface getTabPanel(String tabName)
	{
		return tabPanels.get(tabName);
	}
	
	public void addTab(String name, Component contents)
	{
		addTab(name, null, contents);
	}

	private void addTab(String name, Icon icon, Component contents)
	{
		tabs.addTab(name, icon, contents);
	}
	
	public void setTab(int newTab)
	{
		prepareForTabSwitch();
		tabs.setSelectedIndex(newTab);
		tabWasSelected();
		getMainWindow().updateActionsAndStatusBar();
	}
	
	public void setCurrentSelectedTitle(String text)
	{
		int selectedTabIndex = getSelectedTabIndex();
		setTabTitle(text, selectedTabIndex);
	}

	public void setTabTitle(String text, int selectedTabIndex)
	{
		tabs.setTitleAt(selectedTabIndex, text);
	}
	
	public void tabWasSelected()
	{
		closeActivePropertiesDialog();
		getMainWindow().updateActionStates();
		getMainWindow().rebuildToolBar();
		currentTabIndex = tabs.getSelectedIndex();
		if(currentTabIndex >= 0)
		{
			MiradiTabContentsPanelInterface newPanel = getTabPanel(currentTabIndex);
			if(newPanel != null)
				newPanel.becomeActive();
		}
		
	}
	
	public Component getTabContents(int index)
	{
		return tabs.getComponent(index);
	}
	
	@Override
	public MiradiTabContentsPanelInterface getCurrentTabPanel()
	{
		return getSelectedTabPanel();
	}
	
	public Component getCurrentTabContents()
	{
		return tabs.getSelectedComponent();
	}
	
	@Override
	public boolean isImageAvailable()
	{
		MiradiTabContentsPanelInterface panel = getSelectedTabPanel();
		if(panel == null)
			return false;
		
		return panel.isImageAvailable();
	}
	
	@Override
	public BufferedImage getImage(int scale) throws Exception
	{
		MiradiTabContentsPanelInterface panel = getSelectedTabPanel();
		if(panel == null)
			return null;
		
		return panel.getImage(scale);
	}
	
	@Override
	public boolean isExportableTableAvailable()
	{
		MiradiTabContentsPanelInterface panel = getSelectedTabPanel();
		if(panel == null)
			return false;
		
		return panel.isExportableTableAvailable();
	}

	@Override
	public TableExporter getTableExporter() throws Exception
	{
		MiradiTabContentsPanelInterface panel = getSelectedTabPanel();
		if(panel == null)
			return null;
		
		return panel.getTableExporter();
	}
	
	@Override
	public JComponent getPrintableComponent() throws Exception
	{
		MiradiTabContentsPanelInterface panel = getSelectedTabPanel();
		if(panel == null)
			return null;
		
		return panel.getPrintableComponent();
	}
	
	@Override
	public boolean isPrintable()
	{
		MiradiTabContentsPanelInterface panel = getSelectedTabPanel();
		if(panel == null)
			return false;
		
		return panel.isPrintable();
	}
	
	@Override
	public boolean isRtfExportable()
	{
		return isTabRtfExportable(getSelectedTabPanel());
	}

	private boolean isTabRtfExportable(MiradiTabContentsPanelInterface panel)
	{
		if(panel == null)
			return false;
		
		return panel.isRtfExportable();
	}
	
	@Override
	public void exportRtf(RtfWriter writer) throws Exception
	{
		exportTabAsRtf(writer, getSelectedTabPanel());
	}
	
	public void exportTabAsRtf(RtfWriter writer, MiradiTabContentsPanelInterface panel) throws Exception
	{
		if(panel != null)
			panel.exportRtf(writer);
	}
	

	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		Command rawCommand = event.getCommand();
		if(!rawCommand.getCommandName().equals(CommandSetObjectData.COMMAND_NAME))
			return;
		CommandSetObjectData cmd = (CommandSetObjectData)rawCommand;
		try
		{
			if(cmd.getObjectType() != ObjectType.VIEW_DATA)
				return;
			if(!cmd.getObjectId().equals(getViewData().getId()))
				return;
			if(!cmd.getFieldTag().equals(ViewData.TAG_CURRENT_TAB))
				return;
			EAM.logVerbose("TabbedView.commandExecuted: " + cmd.toString());
			setTab(getViewData().getCurrentTab());
		}
		catch(Exception e)
		{
			EAM.logException(e);
		}
	}

	void handleRightClick(MouseEvent event)
	{
		int tab = tabs.indexAtLocation(event.getX(), event.getY());
		if(tab < 0)
			return;
		
		JPopupMenu menu = getTabPopupMenu();
		if(menu == null)
			return;
		
		getMainWindow().updateActionsAndStatusBar();
		menu.show(tabs, event.getX(), event.getY());
	}
	
	protected void prepareForTabSwitch()
	{
		closeActivePropertiesDialog();
		if(currentTabIndex >= 0)
		{
			MiradiTabContentsPanelInterface oldPanel = getTabPanel(currentTabIndex);
			if(oldPanel != null)
			{
				clearCurrentTabIndex();
				oldPanel.becomeInactive();
			}
		}
	}

	class TabChangeListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent event)
		{
			if(ignoreTabChanges)
				return;

			boolean didUserDirectlyClickOnTheTab = !getProject().getCommandExecutor().isExecutingACommand();
			if(!didUserDirectlyClickOnTheTab)
				return;
			
			try
			{
				FieldSaver.savePendingEdits();
				int newTab = tabs.getSelectedIndex();
				CommandSetObjectData command = createTabChangeCommand(newTab);
				getProject().executeCommand(command);
			}
			catch(Exception e)
			{
				EAM.panic(e);
			}
		}

		private CommandSetObjectData createTabChangeCommand(int newTab) throws Exception
		{
			CommandSetObjectData cmd = new CommandSetObjectData(ObjectType.VIEW_DATA, getViewData().getId(), ViewData.TAG_CURRENT_TAB, Integer.toString(newTab));
			return cmd;
		}

	}
	
	public class MouseHandler extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent event)
		{
			if(event.isPopupTrigger())
				doRightClickMenu(event);
		}

		@Override
		public void mouseReleased(MouseEvent event)
		{
			if(event.isPopupTrigger())
				doRightClickMenu(event);
		}
		
		private void doRightClickMenu(MouseEvent event)
		{
			handleRightClick(event);
		}
	}
	
	public abstract void createTabs() throws Exception;

	private JTabbedPane tabs;
	private int currentTabIndex;
	private boolean ignoreTabChanges;
	private HashMap<String, MiradiTabContentsPanelInterface> tabPanels;
}
