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
package org.miradi.views.threatmatrix;

import java.awt.image.BufferedImage;


import org.miradi.actions.ActionCloneStress;
import org.miradi.actions.ActionCreateStress;
import org.miradi.actions.ActionCreateStressFromKea;
import org.miradi.actions.ActionDeleteStress;
import org.miradi.actions.ActionHideCellRatings;
import org.miradi.actions.ActionManageStresses;
import org.miradi.actions.ActionShowCellRatings;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogs.threatrating.ThreatRatingManagementPanel;
import org.miradi.dialogs.threatrating.upperPanel.ThreatRatingMultiTablePanel;
import org.miradi.main.AppPreferences;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.MainWindow;
import org.miradi.main.MiradiToolBar;
import org.miradi.objects.ProjectMetadata;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ThreatRatingModeChoiceQuestion;
import org.miradi.rtf.RtfWriter;
import org.miradi.schemas.ProjectMetadataSchema;
import org.miradi.utils.BufferedImageFactory;
import org.miradi.views.CardedView;
import org.miradi.views.MiradiTabContentsPanelInterface;
import org.miradi.views.diagram.doers.CloneStressDoer;
import org.miradi.views.diagram.doers.CreateStressDoer;
import org.miradi.views.diagram.doers.CreateStressFromKeaDoer;
import org.miradi.views.diagram.doers.DeleteStressDoer;
import org.miradi.views.threatmatrix.doers.HideCellRatingsDoer;
import org.miradi.views.threatmatrix.doers.ManageStressesDoer;
import org.miradi.views.threatmatrix.doers.ShowCellRatingsDoer;
import org.miradi.views.umbrella.UmbrellaView;


public class ThreatMatrixView extends CardedView
{
	public ThreatMatrixView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		
		addThreatMatrixViewDoersToMap();
	}

	@Override
	public MiradiToolBar createToolBar()
	{
		return new ThreatRatingToolBar(getMainWindow(), isCellRatingsVisible());
	}

	private boolean isCellRatingsVisible()
	{
		return getMainWindow().getBooleanPreference(AppPreferences.TAG_CELL_RATINGS_VISIBLE);
	}

	private void addThreatMatrixViewDoersToMap()
	{
		addDoerToMap(ActionShowCellRatings.class, new ShowCellRatingsDoer());
		addDoerToMap(ActionHideCellRatings.class, new HideCellRatingsDoer());
		addDoerToMap(ActionManageStresses.class, new ManageStressesDoer());
		
		addDoerToMap(ActionCreateStress.class, new CreateStressDoer());
		addDoerToMap(ActionDeleteStress.class, new DeleteStressDoer());
		addDoerToMap(ActionCloneStress.class, new CloneStressDoer());
		addDoerToMap(ActionCreateStressFromKea.class, new CreateStressFromKeaDoer());
	}
	
	@Override
	public String cardName()
	{
		return getViewName();
	}

	static public String getViewName()
	{
		return Project.THREAT_MATRIX_VIEW_NAME;
	}
	
	@Override
	public boolean isImageAvailable()
	{
		return true;
	}

	@Override
	public BufferedImage getImage(int scale) throws Exception
	{
		return createStressBasedImage();
	}

	private BufferedImage createStressBasedImage() throws Exception
	{
		ThreatRatingMultiTablePanel multiTablePanel = new ThreatRatingMultiTablePanel(getMainWindow());
		multiTablePanel.updateAllTables();
		BufferedImage image = BufferedImageFactory.createImageFromComponent(multiTablePanel);
		
		multiTablePanel.dispose();
		return image;
	}

	@Override
	public void createCards() throws Exception
	{
		threatStressRatingManagementPanel = ThreatRatingManagementPanel.create(getMainWindow()); 
		addCard(threatStressRatingManagementPanel, getThreatStressRatingCardName());
	}
	
	@Override
	protected void showCurrentCard(String code)
	{
		showCard(getThreatStressRatingCardName());
		getMainWindow().rebuildToolBar();
	}
	
	@Override
	public void deleteCards() throws Exception
	{
		threatStressRatingManagementPanel.dispose();
	}
	
	@Override
	public boolean isRtfExportable()
	{
		return threatStressRatingManagementPanel.isRtfExportable();
	}
	
	@Override
	public void exportRtf(RtfWriter writer) throws Exception
	{
		threatStressRatingManagementPanel.exportRtf(writer);
	}
	
	@Override
	public void becomeActive() throws Exception
	{
		super.becomeActive();			
		threatStressRatingManagementPanel.becomeActive();
	}
	
	@Override
	public void becomeInactive() throws Exception
	{
		threatStressRatingManagementPanel.becomeInactive();
		super.becomeInactive();
	}
	
	@Override
	public MiradiTabContentsPanelInterface getCurrentTabPanel()
	{
		return threatStressRatingManagementPanel;
	}

	@Override
	protected String getCurrentCardChoiceName()
	{
		return getProject().getMetadata().getData(ProjectMetadata.TAG_THREAT_RATING_MODE);
	}
	
	@Override
	public void commandExecuted(CommandExecutedEvent event)
	{
		super.commandExecuted(event);
		updateCardToShow(event);
	}

	private void updateCardToShow(CommandExecutedEvent event)
	{
		if (!event.isSetDataCommandWithThisTypeAndTag(ProjectMetadataSchema.getObjectType(), ProjectMetadata.TAG_THREAT_RATING_MODE))
			return;
		
		CommandSetObjectData command = (CommandSetObjectData) event.getCommand();
		String value = command.getDataValue();
		ChoiceItem choice = new ThreatRatingModeChoiceQuestion().findChoiceByCode(value);
		showCurrentCard(choice.getCode());
		getMainWindow().rebuildToolBar();
	}

	private String getThreatStressRatingCardName()
	{
		return threatStressRatingManagementPanel.getPanelDescription();
	}

	public static boolean is(UmbrellaView view)
	{
		return view.cardName().equals(getViewName());
	}

	
	private ThreatRatingManagementPanel threatStressRatingManagementPanel;
}

