package org.conservationmeasures.eam.views.budget;

import javax.swing.JLabel;

import org.conservationmeasures.eam.dialogs.BudgetManagementPanel;
import org.conservationmeasures.eam.dialogs.BudgetPropertiesPanel;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.TabbedView;
import org.conservationmeasures.eam.views.budget.wizard.BudgetWizardPanel;
import org.conservationmeasures.eam.views.umbrella.WizardPanel;
import org.conservationmeasures.eam.views.workplan.WorkPlanPanel;
import org.martus.swing.ResourceImageIcon;
import org.martus.swing.UiScrollPane;

public class BudgetView extends TabbedView
{
	public BudgetView(MainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		setToolBar(new BudgetToolBar(mainWindowToUse.getActions()));
	}

	public String cardName() 
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.BUDGET_VIEW_NAME;
	}

	public void becomeActive() throws Exception
	{
		super.becomeActive();
	}

	public void becomeInactive() throws Exception
	{
		super.becomeInactive();
	}

	public void createTabs() throws Exception
	{
		workPlanPanel = new WorkPlanPanel(getMainWindow(), getProject());
		budgetPropertiesPanel = new BudgetPropertiesPanel(getProject(), getMainWindow().getActions());
		budgetManagetmentPanel = new BudgetManagementPanel(workPlanPanel, budgetPropertiesPanel);
		addTab(budgetManagetmentPanel.getPanelDescription(), budgetManagetmentPanel);
		addTab(EAM.text("Reporting"), new UiScrollPane(new BudgetComponent()));
	}

	public WizardPanel createWizardPanel() throws Exception
	{
		return new BudgetWizardPanel();
	}

	public void deleteTabs() throws Exception
	{
		budgetPropertiesPanel.dispose();
		budgetPropertiesPanel = null;
		
		workPlanPanel.dispose();
		workPlanPanel = null;
		
		budgetManagetmentPanel.dispose();
		budgetManagetmentPanel = null;
	}
	
	BudgetPropertiesPanel budgetPropertiesPanel;
	BudgetManagementPanel budgetManagetmentPanel;
	WorkPlanPanel workPlanPanel;
}

class BudgetComponent extends JLabel
{
	public BudgetComponent()
	{
		super(new ResourceImageIcon("images/Budget.png"));
	}
}