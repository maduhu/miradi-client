/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.slideshow;

import java.awt.Image;
import java.text.ParseException;

import javax.swing.event.ListSelectionEvent;

import org.conservationmeasures.eam.actions.ActionCreateSlide;
import org.conservationmeasures.eam.actions.ActionDeleteSlide;
import org.conservationmeasures.eam.actions.ActionMoveSlideDown;
import org.conservationmeasures.eam.actions.ActionMoveSlideUp;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.ObjectListTablePanel;
import org.conservationmeasures.eam.dialogs.ObjectTableModel;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ORef;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.BaseObject;
import org.conservationmeasures.eam.objects.Slide;
import org.conservationmeasures.eam.objects.SlideShow;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.CodeList;
import org.conservationmeasures.eam.views.diagram.DiagramImageCreator;
import org.conservationmeasures.eam.views.diagram.DiagramLegendPanel;
import org.conservationmeasures.eam.views.diagram.DiagramView;


public class SlideListTablePanel extends ObjectListTablePanel
{
	public SlideListTablePanel(Project project, Actions actions, ORef oref)
	{
		super(project, ObjectType.SLIDE, new SlideListTableModel(project, oref), actions, buttons);
	}
	
	static Class[] buttons = new Class[] {
		ActionCreateSlide.class,
		ActionDeleteSlide.class,
		ActionMoveSlideUp.class,
		ActionMoveSlideDown.class
	};
	
	public void valueChanged(ListSelectionEvent event)
	{
		super.valueChanged(event);
		if (getSelectedObject()==null)
			return;
		ORef oref = ((Slide)getSelectedObject()).getDiagramRef();
		if (!oref.equals(ORef.INVALID))
		{
			getDiagramView().setDiagramTab(oref);
			updateLegendPanel();
		}
	}
	
	protected void selectFirstRow()
	{
	}

	public Image createImage()
	{
		return  DiagramImageCreator.getImage(getMainWindow(), getDiagramView().getDiagramModel().getDiagramObject());
	}

	private MainWindow getMainWindow()
	{
		return EAM.mainWindow;
	}
	
	
	public void updateLegendPanel()
	{
		updatingLegendPanel = true;
		Slide slide = (Slide)getSelectedObject();
		CodeList list = getDiagarmLegendSettingsForSlide(slide);
		DiagramLegendPanel panel = getDiagramView().getDiagramPanel().getDiagramLegendPanel();
		panel.updateLegendPanel(list);
		updatingLegendPanel = false;
	}
	
	
	private CodeList getDiagarmLegendSettingsForSlide(Slide slide)
	{
		try
		{
			return  new CodeList(slide.getData(Slide.TAG_DIAGRAM_LEGEND_SETTINGS));
		}
		catch(ParseException e)
		{
			EAM.logException(e);
			EAM.errorDialog("Unable to read slide settings:" + e.getMessage());
			return new CodeList();
		}
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		int selectedRow = getTable().getSelectedRow();
		
		if (((selectedRow >= 0) && isSetDataForSlideShow(event)))
		{
			BaseObject object = getTable().getObjectTableModel().getObjectFromRow(selectedRow);
			ObjectTableModel model = getTable().getObjectTableModel();
			model.resetRows();
			model.fireTableDataChanged();
			
			int newRow = model.findRowObject(object.getId());
			if (newRow>=0)
				getTable().setRowSelectionInterval(newRow, newRow);
			return;
		}

		if (isLegendPanelUpdate())
		{
			updateCurrentSelectedSlidesLegendSettings();
			return;
		}
		
		super.commandExecuted(event);
	}
	
	private void updateCurrentSelectedSlidesLegendSettings()
	{
		//TODO: update slides legend settings
	}

	private boolean isLegendPanelUpdate()
	{
		if (updatingLegendPanel)
			return false;
		//TODO: is this a legend panel update?
		return false;
	}
	
	private boolean isSetDataForSlideShow(CommandExecutedEvent event)
	{
		if (!event.isSetDataCommand())
			return false;
		CommandSetObjectData cmd = ((CommandSetObjectData)event.getCommand());
		return (cmd.getObjectType()==SlideShow.getObjectType());
	}
	
	
	private DiagramView getDiagramView()
	{
		return ((DiagramView)getMainWindow().getCurrentView());
	}
	
	boolean updatingLegendPanel;
}
