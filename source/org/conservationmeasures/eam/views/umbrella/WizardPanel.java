/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.umbrella;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;

import javax.swing.JPanel;

import org.conservationmeasures.eam.commands.CommandSwitchView;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.views.budget.BudgetView;
import org.conservationmeasures.eam.views.budget.wizard.BudgetWizardAccountingAndFunding;
import org.conservationmeasures.eam.views.budget.wizard.BudgetWizardBudgetDetail;
import org.conservationmeasures.eam.views.budget.wizard.BudgetWizardDemo;
import org.conservationmeasures.eam.views.budget.wizard.FinancialOverviewStep;
import org.conservationmeasures.eam.views.diagram.DiagramView;
import org.conservationmeasures.eam.views.diagram.wizard.DescribeTargetStatusStep;
import org.conservationmeasures.eam.views.diagram.wizard.DevelopDraftStrategiesStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramOverviewStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardConstructChainsStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardDefineTargetsStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardIdentifyDirectThreatStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardIdentifyIndirectThreatStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardLinkDirectThreatsToTargetsStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardProjectScopeStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardReviewAndModifyTargetsStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardReviewModelAndAdjustStep;
import org.conservationmeasures.eam.views.diagram.wizard.DiagramWizardVisionStep;
import org.conservationmeasures.eam.views.diagram.wizard.EditAllStrategiesStep;
import org.conservationmeasures.eam.views.diagram.wizard.RankDraftStrategiesStep;
import org.conservationmeasures.eam.views.diagram.wizard.SelectChainStep;
import org.conservationmeasures.eam.views.monitoring.MonitoringView;
import org.conservationmeasures.eam.views.monitoring.wizard.MonitoringPlanOverviewStep;
import org.conservationmeasures.eam.views.monitoring.wizard.MonitoringWizardDefineIndicatorsStep;
import org.conservationmeasures.eam.views.monitoring.wizard.MonitoringWizardEditIndicatorsStep;
import org.conservationmeasures.eam.views.monitoring.wizard.MonitoringWizardFocusStep;
import org.conservationmeasures.eam.views.monitoring.wizard.MonitoringWizardSelectMethodsStep;
import org.conservationmeasures.eam.views.noproject.NoProjectView;
import org.conservationmeasures.eam.views.noproject.wizard.NoProjectOverviewStep;
import org.conservationmeasures.eam.views.noproject.wizard.NoProjectWizardImportStep;
import org.conservationmeasures.eam.views.noproject.wizard.NoProjectWizardPanel;
import org.conservationmeasures.eam.views.noproject.wizard.NoProjectWizardProjectCreateStep;
import org.conservationmeasures.eam.views.schedule.ScheduleView;
import org.conservationmeasures.eam.views.schedule.wizard.ScheduleOverviewStep;
import org.conservationmeasures.eam.views.strategicplan.StrategicPlanView;
import org.conservationmeasures.eam.views.strategicplan.wizard.StrategicPlanDevelopGoalStep;
import org.conservationmeasures.eam.views.strategicplan.wizard.StrategicPlanDevelopObjectivesStep;
import org.conservationmeasures.eam.views.strategicplan.wizard.StrategicPlanOverviewStep;
import org.conservationmeasures.eam.views.strategicplan.wizard.StrategicPlanViewAllGoals;
import org.conservationmeasures.eam.views.strategicplan.wizard.StrategicPlanViewAllObjectives;
import org.conservationmeasures.eam.views.summary.SummaryView;
import org.conservationmeasures.eam.views.summary.wizard.SummaryOverviewStep;
import org.conservationmeasures.eam.views.summary.wizard.SummaryWizardDefineProjecScope;
import org.conservationmeasures.eam.views.summary.wizard.SummaryWizardDefineProjectLeader;
import org.conservationmeasures.eam.views.summary.wizard.SummaryWizardDefineProjectVision;
import org.conservationmeasures.eam.views.summary.wizard.SummaryWizardDefineTeamMembers;
import org.conservationmeasures.eam.views.threatmatrix.ThreatMatrixView;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatMatrixOverviewStep;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardCheckBundleStep;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardCheckTotalsStep;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardChooseBundle;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardIrreversibilityStep;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardPanel;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardScopeStep;
import org.conservationmeasures.eam.views.threatmatrix.wizard.ThreatRatingWizardSeverityStep;
import org.conservationmeasures.eam.views.workplan.WorkPlanView;
import org.conservationmeasures.eam.views.workplan.wizard.WorkPlanAssignResourcesStep;
import org.conservationmeasures.eam.views.workplan.wizard.WorkPlanCreateResourcesStep;
import org.conservationmeasures.eam.views.workplan.wizard.WorkPlanDevelopActivitiesAndTasksStep;
import org.conservationmeasures.eam.views.workplan.wizard.WorkPlanDevelopMethodsAndTasksStep;
import org.conservationmeasures.eam.views.workplan.wizard.WorkPlanOverviewStep;

public class WizardPanel extends JPanel
{
	public WizardPanel(MainWindow mainWindowToUse, UmbrellaView viewToUse)
	{
		super(new BorderLayout());
		mainWindow = mainWindowToUse;
		setFocusCycleRoot(true);
		view = viewToUse;
		setupSteps();
	}

	private void setupSteps()
	{
		try
		{
			String currentView = view.cardName();
			
			if (currentView.equals(ThreatMatrixView.getViewName()))
				addThreatMatrixViewSteps();
				
			if (currentView.equals(DiagramView.getViewName()))
				addDiagramViewSteps();
			
			if (currentView.equals(MonitoringView.getViewName()))
				addMonitoringViewSteps();
			
			if (currentView.equals(WorkPlanView.getViewName()))
				addWorkPlanViewSteps();
			
			if (currentView.equals(SummaryView.getViewName()))
				addSummaryViewSteps();
				
			if (currentView.equals(ScheduleView.getViewName()))
				addScheduleViewSteps();
			
			if (currentView.equals(StrategicPlanView.getViewName()))
				addStrategicPlanViewSteps();
			
			if (currentView.equals(BudgetView.getViewName()))
				addBudgetViewSteps();
			
			if (currentView.equals(NoProjectView.getViewName()))
				addNoProjectViewSteps();
			
			setStep("");
		}
		catch (Exception e)
		{
			String body = EAM.text("Wizard load failed for view: ") + view.cardName();
			EAM.errorDialog(body);
			EAM.logError(body);
			EAM.logException(e);
		}
	}
	
	private void addNoProjectViewSteps() throws Exception
	{
		stepTable.createNoProjectStepEntries((NoProjectWizardPanel)this);
	}
	
	private void addBudgetViewSteps()
	{
		stepTable.createBudgetStepEntries(this);
	}
	
	private void addStrategicPlanViewSteps()
	{
		stepTable.createStrategicPlanStepEntries(this);
	}
	
	private void addScheduleViewSteps()
	{
		stepTable.createScheduleStepEntries(this);
	}

	private void addSummaryViewSteps()
	{
		stepTable.createSummaryStepEntries(this);
	}
	
	private void addDiagramViewSteps() throws Exception
	{
		stepTable.createDigramViewStepEntries(this);
	}
	
	private void addThreatMatrixViewSteps() throws Exception
	{
		stepTable.createThreatMatrixViewStepEntries((ThreatRatingWizardPanel)this);
	}

	public void addMonitoringViewSteps()
	{
		stepTable.createMonitoringViewStepEntries(this);
	}
	
	public void addWorkPlanViewSteps()
	{
		stepTable.createWorkPlanStepEntries(this);
	}

	public void setContents(JPanel contents)
	{
		removeAll();
		add(contents, BorderLayout.CENTER);
		allowSplitterToHideUsCompletely();
		revalidate();
		repaint();
	} 
	
	public void control(String controlName) throws Exception
	{
		WizardStepEntry entry = stepTable.findStep(currentStepName);
		jump(entry.findControlTargetStep(controlName));
	}


	public void setStep(Class newStep) throws Exception
	{
		setStep(newStep.getSimpleName());
	}
	
	
	public void setStep(String newStep) throws Exception
	{
		if (newStep.equals(""))
		{
			newStep = removeSpaces(view.cardName()) + "OverviewStep";
			currentStepName = newStep;
		}
		
		WizardStepEntry entryCur = stepTable.findStep(currentStepName);
		String viewNameCur = entryCur.getStepClass().getWizard().getView().cardName();
		
		WizardStepEntry entryNew = stepTable.findStep(newStep);
		String viewNameNew = entryNew.getStepClass().getWizard().getView().cardName();
	
		
		//FIXME: view switch should not happen here
		if (!viewNameNew.equals(viewNameCur))
		{
			mainWindow.getProject().executeCommand(new CommandSwitchView(viewNameNew));
			SkeletonWizardStep stepClass = stepTable.findStep(newStep).getStepClass();
			mainWindow.getCurrentView().jump(stepClass.getClass());
			return;
		}
		SkeletonWizardStep stepClass = stepTable.findStep(newStep).getStepClass();
		if (stepClass!=null)
		{
			currentStepName = newStep;
			stepClass.refresh();
			setContents(stepClass);
		}

	}

	private String removeSpaces(String name)
	{
		return name.replaceAll(" ", "");
	}

	
	public void refresh() throws Exception
	{
		SkeletonWizardStep stepClass = stepTable.findStep(currentStepName).getStepClass();
		stepClass.refresh();
		stepClass.validate();
	}
	
	public void jump(Class stepMarker) throws Exception
	{
		String name = stepMarker.getSimpleName();
		if (name.startsWith("ActionJump"))
			name = name.substring("ActionJump".length());
		jump(name);
	}

	
	public void jump(String stepMarker) throws Exception
	{
			setStep(stepMarker);
	}

	public MainWindow getMainWindow()
	{
		return mainWindow;
	}
	
	private void allowSplitterToHideUsCompletely()
	{
		setMinimumSize(new Dimension(0, 0));
	}
	
	public UmbrellaView getView()
	{
		return view;
	}
	
	protected UmbrellaView view;
	protected MainWindow mainWindow;
	public String currentStepName;
	//TODO: this should not be a static but is really a wizard manager and should be pulled from above.
	private static StepTable stepTable = new StepTable();

}

class StepTable extends Hashtable
{
	
	public void createNoProjectStepEntries(NoProjectWizardPanel panel) throws Exception
	{	
		createStepEntry(new NoProjectOverviewStep(panel));
		
		createStepEntry(new NoProjectWizardImportStep(panel))
			.createBackControl(NoProjectOverviewStep.class);
		
		createStepEntry(new NoProjectWizardProjectCreateStep(panel))
			.createBackControl(NoProjectOverviewStep.class);
	}
	
	public void createBudgetStepEntries(WizardPanel panel)
	{
		createStepEntry(new FinancialOverviewStep(panel));
		createStepEntry(new BudgetWizardAccountingAndFunding(panel));
		createStepEntry(new BudgetWizardBudgetDetail(panel));
		createStepEntry(new BudgetWizardDemo(panel));
	}
	
	public void createStrategicPlanStepEntries(WizardPanel panel)
	{
		createStepEntry(new StrategicPlanOverviewStep(panel));
		createStepEntry(new StrategicPlanViewAllGoals(panel));
		createStepEntry(new StrategicPlanViewAllObjectives(panel));
	}
	
	public void createScheduleStepEntries(WizardPanel panel)
	{
		createStepEntry(new ScheduleOverviewStep(panel));
	}
	
	public void createSummaryStepEntries(WizardPanel panel)
	{
		createStepEntry(new SummaryOverviewStep(panel));
		createStepEntry(new SummaryWizardDefineTeamMembers(panel));
		createStepEntry(new SummaryWizardDefineProjectLeader(panel));;
		createStepEntry(new SummaryWizardDefineProjecScope(panel));
		createStepEntry(new SummaryWizardDefineProjectVision(panel));
	}
	
	public void createWorkPlanStepEntries(WizardPanel panel)
	{
		createStepEntry(new WorkPlanOverviewStep(panel));
		createStepEntry(new MonitoringWizardSelectMethodsStep(panel));;
		createStepEntry(new WorkPlanDevelopActivitiesAndTasksStep(panel));
		createStepEntry(new WorkPlanDevelopMethodsAndTasksStep(panel));
		createStepEntry(new WorkPlanCreateResourcesStep(panel));
		createStepEntry(new WorkPlanAssignResourcesStep(panel));
	}
	
	public void createMonitoringViewStepEntries(WizardPanel panel)
	{		
		createStepEntry(new MonitoringPlanOverviewStep(panel));
		createStepEntry(new MonitoringWizardEditIndicatorsStep(panel));
	}
	
	public void createDigramViewStepEntries(WizardPanel panel)
	{
		createStepEntry(new DiagramOverviewStep(panel));
		createStepEntry(new DiagramWizardProjectScopeStep(panel));
		createStepEntry(new DiagramWizardVisionStep(panel));
		createStepEntry(new DiagramWizardDefineTargetsStep(panel));
		createStepEntry(new DiagramWizardReviewAndModifyTargetsStep(panel));
		createStepEntry(new DescribeTargetStatusStep(panel));
		createStepEntry(new DiagramWizardIdentifyDirectThreatStep(panel));
		createStepEntry(new DiagramWizardLinkDirectThreatsToTargetsStep(panel));
		createStepEntry(new DiagramWizardIdentifyIndirectThreatStep(panel));		
		createStepEntry(new DiagramWizardConstructChainsStep(panel));	
		createStepEntry(new DiagramWizardReviewModelAndAdjustStep(panel));		
		createStepEntry(new SelectChainStep(panel));		
		createStepEntry(new DevelopDraftStrategiesStep(panel));
		createStepEntry(new RankDraftStrategiesStep(panel));
		createStepEntry(new EditAllStrategiesStep(panel));
		createStepEntry(new StrategicPlanDevelopGoalStep(panel));
		createStepEntry(new StrategicPlanDevelopObjectivesStep(panel));		
		createStepEntry(new MonitoringWizardFocusStep(panel));		
		createStepEntry(new MonitoringWizardDefineIndicatorsStep(panel));
	}


	public void createThreatMatrixViewStepEntries(ThreatRatingWizardPanel panel) throws Exception
	{
		//TODO: View:Diagram...should be Step:StepName or support both
		
		createStepEntry(new ThreatMatrixOverviewStep(panel))
			.createControl("View:Diagram", DiagramOverviewStep.class);


		createStepEntry(new ThreatRatingWizardChooseBundle(panel))
			.createControl("Done", ThreatRatingWizardCheckTotalsStep.class);
		
		createStepEntry(new ThreatRatingWizardScopeStep(panel));
		createStepEntry(new ThreatRatingWizardSeverityStep(panel));
		createStepEntry(new ThreatRatingWizardIrreversibilityStep(panel));

		createStepEntry(new ThreatRatingWizardCheckBundleStep(panel))
			.createNextControl(ThreatRatingWizardChooseBundle.class); 

		createStepEntry(new ThreatRatingWizardCheckTotalsStep(panel))
			.createNextControl(ThreatMatrixOverviewStep.class)
			.createBackControl(ThreatRatingWizardChooseBundle.class); 
	}


	static Class[] getSequence()
	{
		Class[] entries = 
		{
				SummaryOverviewStep.class,
				SummaryWizardDefineTeamMembers.class,
				SummaryWizardDefineProjectLeader.class,
				SummaryWizardDefineProjecScope.class,
				SummaryWizardDefineProjectVision.class,
				
				DiagramOverviewStep.class,
				DiagramWizardProjectScopeStep.class,
				DiagramWizardVisionStep.class,
				DiagramWizardDefineTargetsStep.class,
				DiagramWizardReviewAndModifyTargetsStep.class,
				DescribeTargetStatusStep.class,
				DiagramWizardIdentifyDirectThreatStep.class,
				DiagramWizardLinkDirectThreatsToTargetsStep.class,
				DiagramWizardIdentifyIndirectThreatStep.class,		
				DiagramWizardConstructChainsStep.class,	
				DiagramWizardReviewModelAndAdjustStep.class,		
				
				StrategicPlanOverviewStep.class,
				StrategicPlanDevelopGoalStep.class,
				StrategicPlanViewAllGoals.class,
				StrategicPlanDevelopObjectivesStep.class, 	
				StrategicPlanViewAllObjectives.class,

				SelectChainStep.class,		
				DevelopDraftStrategiesStep.class,
				RankDraftStrategiesStep.class,
				EditAllStrategiesStep.class,
				
				ThreatMatrixOverviewStep.class,
				ThreatRatingWizardChooseBundle.class,
				ThreatRatingWizardScopeStep.class,
				ThreatRatingWizardSeverityStep.class,
				ThreatRatingWizardIrreversibilityStep.class,
				ThreatRatingWizardCheckBundleStep.class,
				
				EditAllStrategiesStep.class,
				
				MonitoringPlanOverviewStep.class,
				MonitoringWizardFocusStep.class,
				MonitoringWizardDefineIndicatorsStep.class,
				
				MonitoringWizardEditIndicatorsStep.class,
				
				WorkPlanOverviewStep.class,
				MonitoringWizardSelectMethodsStep.class,
				WorkPlanDevelopActivitiesAndTasksStep.class,
				WorkPlanDevelopMethodsAndTasksStep.class,
				WorkPlanCreateResourcesStep.class,
				WorkPlanAssignResourcesStep.class,
				
				FinancialOverviewStep.class, 
				BudgetWizardAccountingAndFunding.class,
				BudgetWizardBudgetDetail.class,
				BudgetWizardDemo.class, 
				
				ScheduleOverviewStep.class,
		};
		
		return entries;
		
	}
	
	private WizardStepEntry createStepEntry(SkeletonWizardStep step)
	{
		WizardStepEntry stepEntry = new WizardStepEntry(step.getClass().getSimpleName());
		stepEntry.setStepClass(step);
		put(stepEntry.getStepName(),stepEntry);
		return stepEntry;
	}

	WizardStepEntry findStep(Class stepClass)
	{
		return findStep(stepClass.getSimpleName());
	}
	
	
	WizardStepEntry findStep(String stepName)
	{
		WizardStepEntry entry =(WizardStepEntry)get(stepName);
		if (entry==null)
			EAM.logError("ENTRY NOT FOUND FOR STEP NAME=:" + stepName);
		return entry;
	}
}

class WizardStepEntry
{
	//TODO: If we can change the jump classes later to contain the static reference to the step class (.class) to go to then we can get rid of string step names
	WizardStepEntry(String stepNameToUse)
	{
		stepName = stepNameToUse;
		entries = new Hashtable();
	}
	
	//TODO: add control directly to wizard step; get rid of WizardControl class
	WizardStepEntry createControl(String controlName , Class controlStep)
	{
		entries.put(controlName, new WizardControl(controlName, controlStep.getSimpleName()));
		return this;
	}
	
	WizardStepEntry createNextControl(Class controlStep)
	{
		return createControl("Next", controlStep);
	}
	
	WizardStepEntry createBackControl(Class controlStep)
	{
		return createControl("Back", controlStep);
	}

	
	String findControlTargetStep(String controlName)
	{
		WizardControl control = (WizardControl)entries.get(controlName);
		
		if (control==null)
			return doDeferedLookup(controlName);

		return control.getStepName();
	}


	String doDeferedLookup(String controlName)
	{
		Class[] sequences = StepTable.getSequence();

		int found = findPositionInSequence(sequences);
			
		if (found<0)
		{
			reportError(EAM.text("Step not found in sequence table: ") + stepName);
			return null;
		}
		
		if (controlName.equals("Next"))
		{
			String name = sequences[0].getSimpleName();
			if (found != sequences.length-1)
				name = sequences[found+1].getSimpleName();
			return name;
		}
		
		if (controlName.equals("Back"))
		{
			String name = sequences[sequences.length-1].getSimpleName();
			if (found != 0)
				name = sequences[found-1].getSimpleName();
			return name;
			
		}
		
		reportError(EAM.text("Control not specified: ") + stepName);
		return null;
	}

	private void reportError(String msg)
	{
		EAM.logError(msg);
		EAM.errorDialog(msg);
	}


	private int findPositionInSequence(Class[] sequences)
	{
		for (int i=0; i<sequences.length; ++i)
			if (sequences[i].getSimpleName().equals(stepName)) 
				return i;
		return -1;
	}
	
	void setStepClass(SkeletonWizardStep stepToUse)
	{
		step = stepToUse;
	}
	
	SkeletonWizardStep getStepClass()
	{
		return step;
	}
	
	String getStepName()
	{
		return stepName;
	}
	
	private String stepName;
	private Hashtable entries;
	private SkeletonWizardStep step;
}

//TODO: THis class will go away once controls are stored directly in step classes
class WizardControl
{
	WizardControl(String controlNameToUse, String stepNameToUse)
	{
		controlName = controlNameToUse;
		stepName = stepNameToUse;
	}

	String getStepName()
	{
		return stepName;
	}
	
	String stepName;
	String controlName;
}
