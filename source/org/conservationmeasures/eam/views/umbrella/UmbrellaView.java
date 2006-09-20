/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.umbrella;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.conservationmeasures.eam.actions.ActionAbout;
import org.conservationmeasures.eam.actions.ActionClose;
import org.conservationmeasures.eam.actions.ActionExit;
import org.conservationmeasures.eam.actions.ActionNewProject;
import org.conservationmeasures.eam.actions.ActionPreferences;
import org.conservationmeasures.eam.actions.ActionRedo;
import org.conservationmeasures.eam.actions.ActionUndo;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.actions.jump.ActionJumpActivitiesAndActionPlan;
import org.conservationmeasures.eam.actions.jump.ActionJumpAdaptAndMonitorPlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeData;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeInterventions;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeProjectCapacity;
import org.conservationmeasures.eam.actions.jump.ActionJumpAnalyzeResourcesFeasibilityAndRisk;
import org.conservationmeasures.eam.actions.jump.ActionJumpArticulateCoreAssumptions;
import org.conservationmeasures.eam.actions.jump.ActionJumpAssessStakeholders;
import org.conservationmeasures.eam.actions.jump.ActionJumpCloseTheLoop;
import org.conservationmeasures.eam.actions.jump.ActionJumpCommunicateResults;
import org.conservationmeasures.eam.actions.jump.ActionJumpCreate;
import org.conservationmeasures.eam.actions.jump.ActionJumpCreateModel;
import org.conservationmeasures.eam.actions.jump.ActionJumpDefineAudiences;
import org.conservationmeasures.eam.actions.jump.ActionJumpDefineIndicators;
import org.conservationmeasures.eam.actions.jump.ActionJumpDefineScope;
import org.conservationmeasures.eam.actions.jump.ActionJumpDefineTasks;
import org.conservationmeasures.eam.actions.jump.ActionJumpDescribeTargets;
import org.conservationmeasures.eam.actions.jump.ActionJumpDesignateLeader;
import org.conservationmeasures.eam.actions.jump.ActionJumpDetermineNeeds;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopActivities;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopBudgets;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopCharter;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopObjectives;
import org.conservationmeasures.eam.actions.jump.ActionJumpDevelopTargetGoals;
import org.conservationmeasures.eam.actions.jump.ActionJumpDocument;
import org.conservationmeasures.eam.actions.jump.ActionJumpEstablishVision;
import org.conservationmeasures.eam.actions.jump.ActionJumpGroundTruthRevise;
import org.conservationmeasures.eam.actions.jump.ActionJumpIdentifyDirectThreats;
import org.conservationmeasures.eam.actions.jump.ActionJumpIdentifyIndirectThreats;
import org.conservationmeasures.eam.actions.jump.ActionJumpIdentifyStrategies;
import org.conservationmeasures.eam.actions.jump.ActionJumpIdentifyTargets;
import org.conservationmeasures.eam.actions.jump.ActionJumpImplementPlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpPlanDataStorage;
import org.conservationmeasures.eam.actions.jump.ActionJumpRankDirectThreats;
import org.conservationmeasures.eam.actions.jump.ActionJumpRankDraftStrategies;
import org.conservationmeasures.eam.actions.jump.ActionJumpRefinePlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpResultsChains;
import org.conservationmeasures.eam.actions.jump.ActionJumpSelectAppropriateMethods;
import org.conservationmeasures.eam.actions.jump.ActionJumpSelectTeam;
import org.conservationmeasures.eam.actions.jump.ActionJumpShare;
import org.conservationmeasures.eam.actions.jump.ActionJumpShorttermPlans;
import org.conservationmeasures.eam.actions.jump.ActionJumpTeamRoles;
import org.conservationmeasures.eam.actions.views.ActionViewBudget;
import org.conservationmeasures.eam.actions.views.ActionViewCalendar;
import org.conservationmeasures.eam.actions.views.ActionViewDiagram;
import org.conservationmeasures.eam.actions.views.ActionViewImages;
import org.conservationmeasures.eam.actions.views.ActionViewInterview;
import org.conservationmeasures.eam.actions.views.ActionViewMap;
import org.conservationmeasures.eam.actions.views.ActionViewMonitoring;
import org.conservationmeasures.eam.actions.views.ActionViewStrategicPlan;
import org.conservationmeasures.eam.actions.views.ActionViewThreatMatrix;
import org.conservationmeasures.eam.actions.views.ActionViewWorkPlan;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandCreateObject;
import org.conservationmeasures.eam.commands.CommandDeleteObject;
import org.conservationmeasures.eam.dialogs.IndicatorPropertiesDialog;
import org.conservationmeasures.eam.dialogs.ObjectPropertiesDialog;
import org.conservationmeasures.eam.dialogs.ObjectivePropertiesDialog;
import org.conservationmeasures.eam.dialogs.ProjectResourcePropertiesDialog;
import org.conservationmeasures.eam.dialogs.TaskPropertiesDialog;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.CommandExecutedEvent;
import org.conservationmeasures.eam.main.CommandExecutedListener;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.main.ViewChangeListener;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.EAMObject;
import org.conservationmeasures.eam.objects.Indicator;
import org.conservationmeasures.eam.objects.Objective;
import org.conservationmeasures.eam.objects.ViewData;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.Doer;
import org.conservationmeasures.eam.views.NullDoer;
import org.martus.swing.UiLabel;

abstract public class UmbrellaView extends JPanel implements ViewChangeListener, CommandExecutedListener
{
	public UmbrellaView(MainWindow mainWindowToUse)
	{
		super(new BorderLayout());
		mainWindow = mainWindowToUse;
		nullDoer = new NullDoer();
		actionToDoerMap = new HashMap();
		addUmbrellaDoersToMap();
		getProject().addViewChangeListener(this);
		getProject().addCommandExecutedListener(this);
	}
	
	abstract public String cardName();
	
	public void switchToView(String viewName) throws Exception
	{
		if(cardName().equals(viewName))
			becomeActive();
		else
			becomeInactive();
	}
	
	public void jump(Class stepMarker) throws Exception
	{
		return;
	}

	abstract public void becomeActive() throws Exception;
	abstract public void becomeInactive() throws Exception;
	
	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	public Project getProject()
	{
		return getMainWindow().getProject();
	}
	
	public Actions getActions()
	{
		return getMainWindow().getActions();
	}
	
	public JComponent getToolBar()
	{
		return toolBar;
	}
	
	protected void setToolBar(JComponent newToolBar)
	{
		toolBar = newToolBar;
	}
	
	public BufferedImage getImage()
	{
		throw new RuntimeException("This view doesn't support getImage");
	}
	
	public JComponent getPrintableComponent()
	{
		throw new RuntimeException("This view doesn't support getPrintableComponent");
	}
	
	
	public void createObjective() throws Exception
	{
		CommandCreateObject cmd = new CommandCreateObject(ObjectType.OBJECTIVE);
		getProject().executeCommand(cmd);
		Objective objective = getProject().getObjectivePool().find(cmd.getCreatedId());
		modifyObject(objective);
		selectObject(objective);
	}

	public void createIndicator() throws Exception
	{
		CommandCreateObject cmd = new CommandCreateObject(ObjectType.INDICATOR);
		getProject().executeCommand(cmd);
		Indicator indicator = getProject().getIndicatorPool().find(cmd.getCreatedId());
		modifyObject(indicator);
		selectObject(indicator);
	}

	public void objectWasSelected(EAMObject object) throws Exception
	{
		if(activePropertiesDlg == null)
			return;
		
		if(activePropertiesDlg.getObject().equals(object))
			return;
		
		modifyObject(object);
	}
	
	public void modifyObject(EAMObject object) throws Exception
	{
		ObjectPropertiesDialog dlg = createDialog(object);
		dlg.addWindowListener(new ObjectPropertiesDialogWindowEventHandler());
		showObjectPropertiesDialog(dlg);
	}
	
	class ObjectPropertiesDialogWindowEventHandler extends WindowAdapter
	{

		public void windowClosing(WindowEvent e)
		{
			closeActivePropertiesDialog();
			super.windowClosing(e);
		}

	}
	
	private ObjectPropertiesDialog createDialog(EAMObject object) throws Exception
	{
		switch(object.getType())
		{
			case ObjectType.OBJECTIVE:
				return new ObjectivePropertiesDialog(getMainWindow(), object);
			case ObjectType.INDICATOR:
				return new IndicatorPropertiesDialog(getMainWindow(), object);
			case ObjectType.PROJECT_RESOURCE:
				return new ProjectResourcePropertiesDialog(getMainWindow(), object);
			case ObjectType.TASK:
				return new TaskPropertiesDialog(getMainWindow(), object);
		}
		
		throw new RuntimeException("Attempted to modify unknown type: " + object.getType());
	}

	private void showObjectPropertiesDialog(ObjectPropertiesDialog newDialog)
	{
		if(activePropertiesDlg != null)
			activePropertiesDlg.dispose();
		
		activePropertiesDlg = newDialog;
		activePropertiesDlg.setVisible(true);
	}
	
	public void selectObject(EAMObject objectToSelect)
	{
		EAM.logWarning("UmbrellaView.selectObject don't know how to select: " + objectToSelect);
	}

	protected UiLabel createScreenShotLabel()
	{
		UiLabel label = new UiLabel("Demo Screen Shot");
		label.setBorder(new LineBorder(Color.BLACK));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	////////////////////////////////////////////////////////////
	// these doers are available in this class
	
	private void addUmbrellaDoersToMap()
	{
		addDoerToMap(ActionAbout.class, new About());
		addDoerToMap(ActionNewProject.class, new NewProject());
		addDoerToMap(ActionClose.class, new Close());
		addDoerToMap(ActionExit.class, new Exit());
		addDoerToMap(ActionUndo.class, new Undo());
		addDoerToMap(ActionRedo.class, new Redo());
		addDoerToMap(ActionPreferences.class, new Preferences());
		
		addDoerToMap(ActionViewDiagram.class, new ViewDiagram());
		addDoerToMap(ActionViewInterview.class, new ViewInterview());
		addDoerToMap(ActionViewThreatMatrix.class, new ViewThreatMatrix());
		addDoerToMap(ActionViewBudget.class, new ViewBudget());
		addDoerToMap(ActionViewWorkPlan.class, new ViewWorkPlan());
		addDoerToMap(ActionViewMap.class, new ViewMap());
		addDoerToMap(ActionViewImages.class, new ViewImages());
		addDoerToMap(ActionViewCalendar.class, new ViewCalendar());
		addDoerToMap(ActionViewStrategicPlan.class, new ViewStrategicPlan());
		addDoerToMap(ActionViewMonitoring.class, new ViewMonitoring());
		
		JumpDoer jumpDoer = new JumpDoer();
		addDoerToMap(ActionJumpSelectTeam.class, jumpDoer);
		addDoerToMap(ActionJumpDesignateLeader.class, jumpDoer);
		addDoerToMap(ActionJumpDevelopCharter.class, jumpDoer);
		
		addDoerToMap(ActionJumpDefineScope.class, jumpDoer);
		addDoerToMap(ActionJumpEstablishVision.class, jumpDoer);
		addDoerToMap(ActionJumpIdentifyTargets.class, jumpDoer);
		addDoerToMap(ActionJumpDescribeTargets.class, jumpDoer);
		
		addDoerToMap(ActionJumpIdentifyDirectThreats.class, jumpDoer);
		addDoerToMap(ActionJumpRankDirectThreats.class, jumpDoer);
		addDoerToMap(ActionJumpIdentifyIndirectThreats.class, jumpDoer);
		addDoerToMap(ActionJumpAssessStakeholders.class, jumpDoer);
		addDoerToMap(ActionJumpAnalyzeProjectCapacity.class, jumpDoer);
		
		addDoerToMap(ActionJumpArticulateCoreAssumptions.class, jumpDoer);
		addDoerToMap(ActionJumpCreateModel.class, jumpDoer);
		addDoerToMap(ActionJumpGroundTruthRevise.class, jumpDoer);
		
		addDoerToMap(ActionJumpDevelopTargetGoals.class, jumpDoer);
		addDoerToMap(ActionJumpIdentifyStrategies.class, jumpDoer);
		addDoerToMap(ActionJumpDevelopObjectives.class, jumpDoer);
		
		addDoerToMap(ActionJumpRankDraftStrategies.class, jumpDoer);
		addDoerToMap(ActionJumpResultsChains.class, jumpDoer);
		addDoerToMap(ActionJumpActivitiesAndActionPlan.class, jumpDoer);
		addDoerToMap(ActionJumpAnalyzeResourcesFeasibilityAndRisk.class, jumpDoer);
		
		addDoerToMap(ActionJumpDetermineNeeds.class, jumpDoer);
		addDoerToMap(ActionJumpDefineAudiences.class, jumpDoer);
		
		addDoerToMap(ActionJumpDefineIndicators.class, jumpDoer);
		addDoerToMap(ActionJumpSelectAppropriateMethods.class, jumpDoer);
		addDoerToMap(ActionJumpPlanDataStorage.class, jumpDoer);
		
		addDoerToMap(ActionJumpShorttermPlans.class, jumpDoer);
		addDoerToMap(ActionJumpDevelopActivities.class, jumpDoer);
		addDoerToMap(ActionJumpDefineTasks.class, jumpDoer);
		addDoerToMap(ActionJumpDevelopBudgets.class, jumpDoer);
		addDoerToMap(ActionJumpTeamRoles.class, jumpDoer);
		addDoerToMap(ActionJumpRefinePlans.class, jumpDoer);
		addDoerToMap(ActionJumpImplementPlans.class, jumpDoer);
		
		addDoerToMap(ActionJumpAnalyzeData.class, jumpDoer);
		addDoerToMap(ActionJumpAnalyzeInterventions.class, jumpDoer);
		addDoerToMap(ActionJumpCommunicateResults.class, jumpDoer);
		
		addDoerToMap(ActionJumpAdaptAndMonitorPlans.class, jumpDoer);
		
		addDoerToMap(ActionJumpDocument.class, jumpDoer);
		addDoerToMap(ActionJumpShare.class, jumpDoer);
		addDoerToMap(ActionJumpCreate.class, jumpDoer);
		
		addDoerToMap(ActionJumpCloseTheLoop.class, jumpDoer);
	}
	
	public void addDoerToMap(Class actionClass, Doer doer)
	{
		actionToDoerMap.put(actionClass, doer);
	}
	
	public Doer getDoer(Class actionClass)
	{
		Doer doer = (Doer)actionToDoerMap.get(actionClass);
		if(doer == null)
			doer = nullDoer;
		
		doer.setView(this);
		doer.setProject(getProject());
		return doer;
	}
	
	protected ViewData getViewData() throws Exception
	{
		ViewData ourViewData = getProject().getViewData(cardName());
		return ourViewData;
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		closeActivePropertiesDialogIfWeDeletedItsObject(event.getCommand());
	}

	public void commandUndone(CommandExecutedEvent event)
	{
	}

	public void commandFailed(Command command, CommandFailedException e)
	{
	}
	
	void closeActivePropertiesDialogIfWeDeletedItsObject(Command rawCommand)
	{
		if(activePropertiesDlg == null)
			return;
		
		if(!rawCommand.getCommandName().equals(CommandDeleteObject.COMMAND_NAME))
			return;
		
		CommandDeleteObject cmd = (CommandDeleteObject)rawCommand;
		EAMObject objectBeingEdited = activePropertiesDlg.getObject();
		if(cmd.getObjectType() != objectBeingEdited.getType())
			return;
		if(cmd.getObjectId() != objectBeingEdited.getId())
			return;
		
		closeActivePropertiesDialog();
	}
	
	public void closeActivePropertiesDialog()
	{
		if(activePropertiesDlg != null && activePropertiesDlg.isDisplayable())
			activePropertiesDlg.dispose();
		activePropertiesDlg = null;
	}

	private MainWindow mainWindow;
	private NullDoer nullDoer;
	private JComponent toolBar;
	private HashMap actionToDoerMap;
	
	private ObjectPropertiesDialog activePropertiesDlg;
 
}
