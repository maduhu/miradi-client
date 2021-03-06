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
package org.miradi.views.diagram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import org.martus.swing.UiLabel;
import org.miradi.commands.CommandCreateObject;
import org.miradi.commands.CommandDeleteObject;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.diagram.DiagramComponent;
import org.miradi.diagram.DiagramModel;
import org.miradi.diagram.MemoryDiagramModel;
import org.miradi.diagram.PersistentDiagramModel;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objecthelpers.ObjectType;
import org.miradi.objectpools.EAMObjectPool;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.DiagramLink;
import org.miradi.objects.DiagramObject;
import org.miradi.objects.TaggedObjectSet;
import org.miradi.objects.ViewData;
import org.miradi.project.Project;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.schemas.DiagramLinkSchema;
import org.miradi.utils.AbstractTableRightClickHandler;
import org.miradi.utils.FlexibleWidthHtmlViewer;
import org.miradi.utils.MiradiScrollPane;
import org.miradi.utils.Translation;
import org.miradi.views.umbrella.PersistentNonPercentageHorizontalSplitPane;

abstract public class DiagramSplitPane extends PersistentNonPercentageHorizontalSplitPane implements CommandExecutedListener
{
	public DiagramSplitPane(MainWindow mainWindowToUse, int objectType, String splitterName) throws Exception
	{
		super(mainWindowToUse, mainWindowToUse, splitterName);
			
		mainWindow = mainWindowToUse;
		project = mainWindow.getProject();
		diagramCards = new DiagramCards();
		reloadDiagramCards(objectType);
		
		setLeftComponent(createLeftPanel(objectType));
		setRightComponent(new MiradiScrollPane(diagramCards));
	}

	public void showCurrentCard() throws Exception
	{
		showCard(selectionPanel.getCurrentDiagramViewDataRef());
	}
	
	public void dispose()
	{
		getDiagramPageList().dispose();
		legendPanel.dispose();
	}
	
	public void becomeActive()
	{
		project.addCommandExecutedListener(this);
		selectionPanel.becomeActive();
		legendPanel.becomeActive();
	}
	
	public void becomeInactive()
	{
		project.removeCommandExecutedListener(this);
		selectionPanel.becomeInactive();
		legendPanel.becomeInactive();
	}
	
	private void reloadDiagramCards(int objectType) throws Exception
	{
		ORefList diagramObjectRefList = getDiagramObjects(objectType);
		diagramCards.clear();
		for (int i = 0; i < diagramObjectRefList.size(); ++i)
		{
			ORef diagramObjectRef = diagramObjectRefList.get(i);
			DiagramObject diagramObject = (DiagramObject) project.findObject(diagramObjectRef);
			DiagramComponent diagramComponentToAdd = createDiagram(mainWindow, diagramObject);
			diagramCards.addDiagram(diagramComponentToAdd);
		}
	}

	private ORefList getDiagramObjects(int objectType) throws Exception
	{
		EAMObjectPool pool = project.getPool(objectType);
		return pool.getORefList();
	}
	
	public static DiagramComponent createDiagram(MainWindow mainWindow, DiagramObject diagramObject) throws Exception
	{
		PersistentDiagramModel diagramModel = new PersistentDiagramModel(diagramObject.getProject());
		return createDiagram(mainWindow, diagramModel, diagramObject);
	}
	
	public static DiagramComponent createMemoryDiagram(MainWindow mainWindow, DiagramObject diagramObject) throws Exception
	{
		MemoryDiagramModel memoryDiagram = new MemoryDiagramModel(diagramObject.getProject());
		return createDiagram(mainWindow, memoryDiagram, diagramObject);
	}

	private static DiagramComponent createDiagram(MainWindow mainWindow, DiagramModel diagramModel, DiagramObject diagramObject) throws Exception
	{
		diagramModel.fillFrom(diagramObject);
		DiagramComponent diagram = new DiagramComponent(mainWindow, diagramModel);
		diagram.setGraphLayoutCache(diagramModel.getGraphLayoutCache());
		
		return diagram;
	}

	protected JComponent createLeftPanel(int objectType) throws Exception
	{
		// NOTE: This code is convoluted, but I couldn't find a simpler way to have 
		// the table at the top be fixed-height but variable width
		
		selectionPanel = createPageList(mainWindow);
		selectionPanel.listChanged();
		JScrollPane selectionScrollPane = new MiradiScrollPane(selectionPanel);
		selectionScrollPane.setBackground(AppPreferences.getControlPanelBackgroundColor());
		selectionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		selectionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Border cushion = BorderFactory.createEmptyBorder(5,5,5,5);
		Border newBorder = BorderFactory.createCompoundBorder(cushion, selectionScrollPane.getBorder());
		selectionScrollPane.setBorder(newBorder);
		selectionScrollPane.setMinimumSize(new Dimension(0,0));
		
		MouseHandler rightClickMouseHandler = new MouseHandler(getMainWindow(), selectionPanel);
		selectionScrollPane.addMouseListener(rightClickMouseHandler);
		selectionPanel.addMouseListener(rightClickMouseHandler);

		legendPanel = createLegendPanel(mainWindow);
		scrollableLegendPanel = new MiradiScrollPane(legendPanel);
		scrollableLegendPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollableLegendPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollableLegendPanel.setMinimumSize(new Dimension(0,0));

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(AppPreferences.getControlPanelBackgroundColor());
		UiLabel title = createControlPanelTitle();
		topPanel.add(title, BorderLayout.BEFORE_FIRST_LINE);
		topPanel.add(selectionScrollPane, BorderLayout.AFTER_LAST_LINE);
		
		JPanel box = new JPanel(new BorderLayout());
		box.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
		box.add(scrollableLegendPanel, BorderLayout.CENTER);
		
		return box;
	}
	
	// TODO: Should be combined with similar code in ControlPanel
	public UiLabel createControlPanelTitle()
	{
		UiLabel title = new PanelTitleLabel(EAM.text("Control Bar"));
		title.setBorder(new LineBorder(Color.BLACK, 2));
		title.setHorizontalAlignment(UiLabel.CENTER);
		title.setBackground(getBackground());
		title.setMinimumSize(new Dimension(0,0));
		return title;
	}
	
	public DiagramLegendPanel getLegendPanel()
	{
		return legendPanel;
	}
	
	public DiagramModel getDiagramModel()
	{
		DiagramComponent diagram = getCurrentDiagramComponent();
		if (diagram != null)
			return getCurrentDiagramComponent().getDiagramModel();
		
		return null;
	}
	
	public DiagramComponent getCurrentDiagramComponent()
	{
		return diagramCards.findByRef(getCurrentDiagramObjectRef());
	}
	
	public DiagramComponent[] getAllOwenedDiagramComponents()
	{
		return diagramCards.getAllDiagramComponents();
	}
	
	public ORef getCurrentDiagramObjectRef()
	{
		return currentRef;
	}
	
	public class DiagramCards extends JPanel
	{
		public DiagramCards()
		{
			super(new BorderLayout());
			cards = new Vector<DiagramComponent>();
		}
		
		public void clear()
		{
			removeAll();
			cards = new Vector<DiagramComponent>();
		}

		public void showDiagram(ORef ref) throws Exception
		{
			removeAll();
			
			showFoundReloadedDiagram(ref);
			
			invalidate();
			repaint();
		}

		private void showFoundReloadedDiagram(ORef ref) throws Exception
		{
			if (ref.isInvalid())
			{
				displayResultsChainHelpText();
				return;
			}
			
			//FIXME low: why does loading all the cards work (shows newly created RC)
			reloadDiagramCards(ref.getObjectType());
			DiagramComponent diagramComponent = findByRef(ref);
			getLegendPanel().resetCheckBoxes();
			if (diagramComponent != null)
			{
				add(diagramComponent);
				getDiagramModel().updateGroupBoxCells();
			}
		}
		
		private void displayResultsChainHelpText()
		{
			if (!selectionPanel.isResultsChainPageList())
				return;
			
			try
			{
				String html = Translation.getHtmlContent("ResultsChainHelp.html");
				add(new FlexibleWidthHtmlViewer(getMainWindow(), html));
			}
			catch(Exception e)
			{
				EAM.logException(e);
			}	

		}

		public void addDiagram(DiagramComponent diagramComponent)
		{
			cards.add(diagramComponent);
		}

		public DiagramComponent findByRef(ORef ref)
		{
			for (int i = 0; i < cards.size(); ++i)
			{
				DiagramComponent diagramComponent = cards.get(i);
				ORef diagramObjectRef = diagramComponent.getDiagramModel().getDiagramObject().getRef();
				if (diagramObjectRef.equals(ref))
				{
					return diagramComponent;
				}
			}

			return null;
		}
		
		public DiagramComponent[] getAllDiagramComponents()
		{
			return cards.toArray(new DiagramComponent[0]);
		}
		
		public int getCardCount()
		{
			return cards.size();
		}
		
		Vector<DiagramComponent> cards;
	}
		
	public void showCard(ORef diagramObjectRef)
	{
		try
		{
			setCurrentDiagramObjectRef(diagramObjectRef);
			diagramCards.showDiagram(diagramObjectRef);
			DiagramComponent diagramComponent = diagramCards.findByRef(diagramObjectRef);
			if (diagramComponent == null)
				return;

			mainWindow.getDiagramView().updateVisibilityOfFactorsAndLinks();
			selectionPanel.setSelectedRow(diagramObjectRef);
			
			// TODO: This is here because setting a factor/link to be visible also has
			// the side effect of selecting it, so the last item added is selected but 
			// shouldn't be. So our quick fix is to clear the selection. 
			// Cleaner fixes ran into strange problems where Windows and Linux systems
			// behaved differently. SEE ALSO DiagramView.getPrintableComponent()
			diagramComponent.clearSelection();

			diagramComponent.forceRepaint();
			validate();
			updateStatusBar();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("An error is preventing this diagram from displaying correctly. " +
									 "Most likely, the project has gotten corrupted. Please contact " +
									 "the Miradi team for help and advice. We recommend that you not " +
									 "make any changes to this project until this problem has been resolved."));
		}		
	}

	private void setCurrentDiagramObjectRef(ORef currentDiagramObjectRef)
	{
		currentRef = currentDiagramObjectRef;
	}

	public DiagramPageList getDiagramPageList()
	{
		return selectionPanel;
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		try
		{
			if (event.isSetDataCommand())
				handleCommandSetObjectData((CommandSetObjectData) event.getCommand());

			if (event.isCreateObjectCommand())
				handleCommandCreateObject((CommandCreateObject) event.getCommand());

			if (event.isDeleteObjectCommand())
				handleCommandDeleteObject((CommandDeleteObject) event.getCommand());
		}
		catch(Exception e)
		{
			EAM.panic(e); 
		}
	}

	private void handleCommandDeleteObject(CommandDeleteObject commandDeleteObject) throws Exception
	{
		handleCreateOrDeleteObject(commandDeleteObject.getObjectType());
	}

	private void handleCommandCreateObject(CommandCreateObject commandCreateObject) throws Exception
	{
		handleCreateOrDeleteObject(commandCreateObject.getObjectType());
	}
	
	private void handleCreateOrDeleteObject(int objectTypeFromCommand) throws Exception
	{
		if (getContentType() == objectTypeFromCommand)
			reloadDiagramCards();
		
		if (TaggedObjectSet.is(objectTypeFromCommand))
			updateLegendScrollPane();
	}
	
	private void updateLegendScrollPane()
	{
		scrollableLegendPanel.revalidate();
	}

	private void handleCommandSetObjectData(CommandSetObjectData commandSetObjectData) throws Exception
	{
		if (commandSetObjectData.getObjectType() == getContentType())
			handleDiagramContentsChange(commandSetObjectData);
		
		if (commandSetObjectData.getObjectType()== ObjectType.VIEW_DATA)
			handleViewDataContentsChange(commandSetObjectData);
		
		if (commandSetObjectData.getObjectType() == DiagramLinkSchema.getObjectType())
			handleDiagramLinkContentsChange(commandSetObjectData);

		handleGroupBoxTypes(commandSetObjectData);
		handleDiagramZooming(commandSetObjectData);
		handleTaggedObjectSets(commandSetObjectData);
	}

	private void handleTaggedObjectSets(CommandSetObjectData commandSetObjectData)
	{
		if (getCurrentDiagramComponent() == null)
			return;
		
		if (commandSetObjectData.isJustTagInAnyType(DiagramObject.TAG_SELECTED_TAGGED_OBJECT_SET_REFS))
			updateStatusBar();
	}
	
	private void updateStatusBar()
	{
		ORefList activeTaggedObjectSetRefs = getCurrentDiagramComponent().getDiagramObject().getSelectedTaggedObjectSetRefs();
		if (activeTaggedObjectSetRefs.hasRefs())
			getMainWindow().setStatusBarWarningMessage(EAM.substituteSingleInteger(EAM.text("Not all factors are being shown because %s tag(s) are checked"), activeTaggedObjectSetRefs.size()));
		else
			getMainWindow().clearStatusBar();
	}

	private void handleDiagramZooming(CommandSetObjectData commandSetObjectData)
	{
		DiagramComponent currentDiagramComponent = getCurrentDiagramComponent();
		if (currentDiagramComponent == null)
			return;

		ORef currentDiagramRef = currentDiagramComponent.getDiagramObject().getRef();
		if (commandSetObjectData.isRefAndTag(currentDiagramRef, DiagramObject.TAG_ZOOM_SCALE))
			currentDiagramComponent.updateDiagramZoomSetting();
	}

	private void handleDiagramLinkContentsChange(CommandSetObjectData commandSetObjectData) throws Exception
	{
		DiagramModel diagramModel = getDiagramModel();
		if(diagramModel == null)
			return;

		if (commandSetObjectData.getFieldTag().equals(DiagramLink.TAG_BEND_POINTS))
			getDiagramModel().updateGroupBoxCells();

		if(!commandSetObjectData.getFieldTag().equals(DiagramLink.TAG_GROUPED_DIAGRAM_LINK_REFS))
			return;
		
		diagramModel.updateVisibilityOfFactorsAndLinks();
	}

	private void handleGroupBoxTypes(CommandSetObjectData commandSetObjectData) throws Exception
	{
		if (commandSetObjectData.getObjectType() != DiagramFactorSchema.getObjectType())
			return;
		
		if (getDiagramModel() == null)
			return;
		
		if (commandSetObjectData.getFieldTag().equals(DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS))
			getDiagramModel().updateGroupBoxCells();
		
		if (commandSetObjectData.getFieldTag().equals(DiagramFactor.TAG_LOCATION))
			getDiagramModel().updateGroupBoxCells();
	
		if (commandSetObjectData.getFieldTag().equals(DiagramFactor.TAG_SIZE))
			getDiagramModel().updateGroupBoxCells();
	}

	private void handleViewDataContentsChange(CommandSetObjectData commandSetObjectData)
	{
		if (commandSetObjectData.getFieldTag() != selectionPanel.getCurrentDiagramViewDataTag())
			return;
		
		ViewData viewData = (ViewData) project.findObject(commandSetObjectData.getObjectORef());
		ORef viewDataCurrentDiagramRef = getCurrentDiagramRef(viewData);
		showCard(viewDataCurrentDiagramRef);
	}

	private void handleDiagramContentsChange(CommandSetObjectData setCommand) throws Exception
	{
		DiagramModel diagramModel = getDiagramModel();
		if (diagramModel == null)
			return;

		DiagramModelUpdater modelUpdater = new DiagramModelUpdater(project, diagramModel);
		modelUpdater.commandSetObjectDataWasExecuted(setCommand);
	}
	
	private void reloadDiagramCards() throws Exception
	{
		reloadDiagramCards(getContentType());
		getDiagramPageList().listChanged();
		showCurrentCard();
	}

	public int getContentType()
	{
		return getDiagramPageList().getManagedDiagramType();
	}
	
	private ORef getCurrentDiagramRef(ViewData viewData)
	{
		if (getContentType() == ObjectType.CONCEPTUAL_MODEL_DIAGRAM)
			return viewData.getCurrentConceptualModelRef();
		
		if (getContentType() == ObjectType.RESULTS_CHAIN_DIAGRAM)
			return viewData.getCurrentResultsChainRef();
		
		return ORef.INVALID;
	}
	
	protected MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public class MouseHandler extends AbstractTableRightClickHandler
	{
		public MouseHandler(MainWindow mainWindowToUse, DiagramPageList tableToUse)
		{
			super(mainWindowToUse, tableToUse);
		}

		@Override
		protected void populateMenu(JPopupMenu popupMenu)
		{
			Class[] rightClickMenuActions = getPopUpMenuActions();
			for (int i = 0; i < rightClickMenuActions.length; ++i)
			{
				if(rightClickMenuActions[i] == null)
					popupMenu.addSeparator();
				else
					popupMenu.add(getMainWindow().getActions().get(rightClickMenuActions[i]));
			}
		}
	}
	
	abstract public Class[] getPopUpMenuActions();
	
	abstract public DiagramPageList createPageList(MainWindow mainWindowToUse);
	
	abstract public DiagramLegendPanel createLegendPanel(MainWindow mainWindowToUse) throws Exception;
	
	protected DiagramLegendPanel legendPanel;
	private DiagramPageList selectionPanel;
	private JScrollPane scrollableLegendPanel;
	private MainWindow mainWindow;
	private Project project;
	private DiagramCards diagramCards;
	private ORef currentRef;
}
