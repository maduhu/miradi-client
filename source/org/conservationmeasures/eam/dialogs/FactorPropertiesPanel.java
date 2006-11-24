/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.cells.DiagramFactor;
import org.conservationmeasures.eam.diagram.factortypes.FactorType;
import org.conservationmeasures.eam.diagram.factortypes.FactorTypeContributingFactor;
import org.conservationmeasures.eam.diagram.factortypes.FactorTypeDirectThreat;
import org.conservationmeasures.eam.dialogfields.legacy.LegacyChoiceDialogField;
import org.conservationmeasures.eam.dialogfields.legacy.LegacyRatingDisplayField;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.icons.DirectThreatIcon;
import org.conservationmeasures.eam.icons.ContributingFactorIcon;
import org.conservationmeasures.eam.ids.FactorId;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objecthelpers.TaxonomyItem;
import org.conservationmeasures.eam.objecthelpers.TaxonomyLoader;
import org.conservationmeasures.eam.objects.Cause;
import org.conservationmeasures.eam.objects.Strategy;
import org.conservationmeasures.eam.objects.Factor;
import org.conservationmeasures.eam.project.FactorCommandHelper;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.ratings.RatingChoice;
import org.conservationmeasures.eam.ratings.StrategyCostQuestion;
import org.conservationmeasures.eam.ratings.StrategyDurationQuestion;
import org.conservationmeasures.eam.ratings.StrategyFeasibilityQuestion;
import org.conservationmeasures.eam.ratings.StrategyImpactQuestion;
import org.conservationmeasures.eam.ratings.StrategyRatingSummary;
import org.conservationmeasures.eam.utils.DialogGridPanel;
import org.conservationmeasures.eam.utils.UiTextFieldWithLengthLimit;
import org.conservationmeasures.eam.views.strategicplan.StrategicPlanPanel;
import org.martus.swing.UiCheckBox;
import org.martus.swing.UiComboBox;
import org.martus.swing.UiLabel;
import org.martus.swing.UiTextArea;
import org.martus.swing.UiTextField;

public class FactorPropertiesPanel extends DisposablePanel
{
	public FactorPropertiesPanel(MainWindow parent,DiagramComponent diagramToUse)
	{
		mainWindow = parent;
		diagram = diagramToUse;
	}
	
	public void dispose()
	{
		if(indicatorsTab != null)
			indicatorsTab.dispose();
		if(goalsTab != null)
			goalsTab.dispose();
		if(objectivesTab != null)
			objectivesTab.dispose();
		super.dispose();
	}
	
	public void selectTab(int tabIdentifier)
	{
		switch(tabIdentifier)
		{
			case TAB_OBJECTIVES:
				tabs.setSelectedComponent(objectivesTab);
				break;
			case TAB_INDICATORS:
				tabs.setSelectedComponent(indicatorsTab);
				break;
			case TAB_GOALS:
				tabs.setSelectedComponent(goalsTab);
				break;
			default:
				tabs.setSelectedComponent(detailsTab);
				break;
		}
	}

	public void setCurrentDiagramFactor(DiagramComponent diagram, DiagramFactor diagramFactor)
	{
		this.setLayout(new BorderLayout());
		this.removeAll();
		try
		{
			currentDiagramFactor = diagramFactor;
			this.add(createLabelBar(currentDiagramFactor),
					BorderLayout.BEFORE_FIRST_LINE);
			this.add(createTabbedPane(currentDiagramFactor), BorderLayout.CENTER);
		}
		catch(Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog("Error reading activity information");
		}
	}

	public DiagramFactor getCurrentDiagramFactor()
	{
		return currentDiagramFactor;
	}

	public FactorId getCurrentFactorId()
	{
		return getCurrentDiagramFactor().getWrappedId();
	}

	private Component createLabelBar(DiagramFactor diagramFactor)
	{
		createTextField(diagramFactor.getLabel(), MAX_LABEL_LENGTH);

		DialogGridPanel grid = new DialogGridPanel();
		grid.add(new UiLabel(EAM.text("Label|Label")));
		grid.add(textField);

		if(diagramFactor.isCause())
		{
			grid.add(new UiLabel(EAM.text("Label|Type")));
			String subTypeString = new FactorTypeContributingFactor().toString();
			if(diagramFactor.isDirectThreat())
				subTypeString = new FactorTypeDirectThreat().toString();
			grid.add(new UiLabel(subTypeString));
		}

		grid.add(new UiLabel());
		return grid;
	}

	private Component createTabbedPane(DiagramFactor diagramFactor) throws Exception
	{
		tabs = new JTabbedPane();
		tabs.add(createMainGrid(diagramFactor), EAM.text("Tab|Details"));
		
		indicatorsTab = new IndicatorListManagementPanel(getProject(), getCurrentDiagramFactor().getWrappedId(), mainWindow.getActions());
		tabs.add(indicatorsTab, indicatorsTab.getPanelDescription());
		
		if(diagramFactor.canHaveObjectives())
		{
			objectivesTab = new ObjectiveListManagementPanel(getProject(), getCurrentDiagramFactor().getWrappedId(), mainWindow.getActions());
			tabs.add(objectivesTab, objectivesTab.getPanelDescription());
		}
		
		if(diagramFactor.canHaveGoal())
		{
			goalsTab = new GoalListManagementPanel(getProject(), getCurrentDiagramFactor().getWrappedId(), mainWindow.getActions());
			tabs.add(goalsTab, goalsTab.getPanelDescription());
		}
		
		if(diagramFactor.isStrategy())
			tabs.add(createTasksGrid(diagramFactor), EAM.text("Tab|Actions"));
		
		return tabs;
	}

	private Component createMainGrid(DiagramFactor diagramFactor)
	{
		detailsTab = new DialogGridPanel();
		statusCheckBox = new UiCheckBox(EAM.text("Label|Draft"));

		if(diagramFactor.isDirectThreat())
		{
			detailsTab.add(new UiLabel(EAM
					.text("Label|IUCN-CMP Classification")));
			detailsTab.add(createThreatClassificationDropdown());
		}

		if(diagramFactor.isStrategy())
		{
			detailsTab.add(new UiLabel(EAM.text("Label|Status")));
			statusCheckBox.setSelected(diagramFactor.isStatusDraft());
			statusCheckBox.addItemListener(new StatusChangeHandler());
			detailsTab.add(statusCheckBox);

			detailsTab.add(new UiLabel(EAM
					.text("Label|IUCN-CMP Classification")));
			detailsTab.add(createStrategyClassificationDropdown());
			
			String impactTag = Strategy.TAG_IMPACT_RATING;
			StrategyImpactQuestion impactQuestion = new StrategyImpactQuestion(impactTag);
			detailsTab.add(new UiLabel(impactQuestion.getLabel()));
			LegacyChoiceDialogField impactField = new LegacyChoiceDialogField(impactQuestion);
			impactComponent = (UiComboBox)impactField.getComponent();
			detailsTab.add(createFieldPanel(impactComponent));
			impactField.selectCode(diagramFactor.getUnderlyingObject().getData(impactTag));
			impactComponent.addItemListener(new ImpactChangeHandler());
			
			String durationTag = Strategy.TAG_DURATION_RATING;
			StrategyDurationQuestion durationQuestion = new StrategyDurationQuestion(durationTag);
			detailsTab.add(new UiLabel(durationQuestion.getLabel()));
			LegacyChoiceDialogField durationField = new LegacyChoiceDialogField(durationQuestion);
			durationComponent = (UiComboBox)durationField.getComponent();
			detailsTab.add(createFieldPanel(durationComponent));
			durationField.selectCode(diagramFactor.getUnderlyingObject().getData(durationTag));
			durationComponent.addItemListener(new DurationChangeHandler());
			
			String feasibilityTag = Strategy.TAG_FEASIBILITY_RATING;
			StrategyFeasibilityQuestion feasibilityQuestion = new StrategyFeasibilityQuestion(feasibilityTag);
			detailsTab.add(new UiLabel(feasibilityQuestion.getLabel()));
			LegacyChoiceDialogField feasibilityField = new LegacyChoiceDialogField(feasibilityQuestion);
			feasibilityComponent = (UiComboBox)feasibilityField.getComponent();
			detailsTab.add(createFieldPanel(feasibilityComponent));
			feasibilityField.selectCode(diagramFactor.getUnderlyingObject().getData(feasibilityTag));
			feasibilityComponent.addItemListener(new FeasibilityChangeHandler());
			
			String costTag = Strategy.TAG_COST_RATING;
			StrategyCostQuestion costQuestion = new StrategyCostQuestion(costTag);
			detailsTab.add(new UiLabel(costQuestion.getLabel()));
			LegacyChoiceDialogField costField = new LegacyChoiceDialogField(costQuestion);
			costComponent = (UiComboBox)costField.getComponent();
			detailsTab.add(createFieldPanel(costComponent));
			costField.selectCode(diagramFactor.getUnderlyingObject().getData(costTag));
			costComponent.addItemListener(new CostChangeHandler());

			detailsTab.add(new UiLabel(EAM.text("Label|Rating")));
			LegacyRatingDisplayField ratingSummaryField = new LegacyRatingDisplayField(new StrategyRatingSummary(""));
			ratingComponent = (UiLabel)ratingSummaryField.getComponent();
			detailsTab.add(createFieldPanel(ratingComponent));
			updateRating();
		}

		detailsTab.add(new UiLabel(EAM.text("Label|Comments")));
		detailsTab.add(createComment(diagramFactor.getComment()));

		return detailsTab;
	}

	class StatusChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			try
			{
				getProject().executeCommand(buildStatusCommand());
				getProject().updateVisibilityOfSingleNode(getCurrentDiagramFactor());
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	class ImpactChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			try
			{
				String tag = Strategy.TAG_IMPACT_RATING;
				String impact = getSelectedImpactCode();
				CommandSetObjectData cmd = new CommandSetObjectData(getCurrentDiagramFactor().getType(),
						getCurrentFactorId(), tag, impact);
				getProject().executeCommand(cmd);
				updateRating();
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	class DurationChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			try
			{
				String tag = Strategy.TAG_DURATION_RATING;
				String duration = getSelectedDurationCode();
				CommandSetObjectData cmd = new CommandSetObjectData(getCurrentDiagramFactor().getType(),
						getCurrentFactorId(), tag, duration);
				getProject().executeCommand(cmd);
				updateRating();
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	class FeasibilityChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			try
			{
				String tag = Strategy.TAG_FEASIBILITY_RATING;
				String feasibility = getSelectedFeasibilityCode();
				CommandSetObjectData cmd = new CommandSetObjectData(getCurrentDiagramFactor().getType(),
						getCurrentFactorId(), tag, feasibility);
				getProject().executeCommand(cmd);
				updateRating();
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	class CostChangeHandler implements ItemListener
	{
		public void itemStateChanged(ItemEvent event)
		{
			try
			{
				String tag = Strategy.TAG_COST_RATING;
				String cost = getSelectedCostCode();
				CommandSetObjectData cmd = new CommandSetObjectData(getCurrentDiagramFactor().getType(),
						getCurrentFactorId(), tag, cost);
				getProject().executeCommand(cmd);
				updateRating();
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	void updateRating()
	{
		RatingChoice rating = ((Strategy)getCurrentDiagramFactor().getUnderlyingObject()).getStrategyRating();
		ratingComponent.setText(rating.getCode());
	}
	

	private Component createTasksGrid(DiagramFactor diagramFactor) throws Exception
	{
		Strategy intervention = (Strategy) diagramFactor
				.getUnderlyingObject();
		return StrategicPlanPanel.createForStrategy(mainWindow, intervention);
	}

	private Component createTextField(String initialText, int maxLength)
	{
		textField = new UiTextFieldWithLengthLimit(maxLength);
		textField.addFocusListener(new LabelFocusHandler());
		textField.requestFocus(true);

		textField.setText(initialText);
		textField.selectAll();

		JPanel component = new JPanel(new BorderLayout());
		component.add(textField, BorderLayout.LINE_START);
		return component;
	}

	class LabelFocusHandler implements FocusListener
	{
		public void focusGained(FocusEvent event)
		{
		}

		public void focusLost(FocusEvent event)
		{
			String newText = getText();
			if(newText.equals(getCurrentDiagramFactor().getLabel()))
				return;
			try
			{
				CommandSetObjectData cmd = FactorCommandHelper
						.createSetLabelCommand(getCurrentFactorId(), newText);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}
	}


	class ThreatClassificationChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			TaxonomyItem taxonomyItem = getThreatTaxonomyItem();
			actionSaveTaxonomySelection(dropdownThreatClassification, taxonomyItem);
		}

	}
	
	class StrategyClassificationChangeHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			TaxonomyItem taxonomyItem = getStrategyTaxonomyItem();
			actionSaveTaxonomySelection(dropdownStrategyClassification, taxonomyItem);
		}
	}
	

	private void actionSaveTaxonomySelection(UiComboBox thisComboBox, TaxonomyItem taxonomyItem)
	{
		try
		{
			int type = ObjectType.MODEL_NODE;
			// TODO: This looks wrong...could be called for Strategy or DirectThreat
			String tag = Cause.TAG_TAXONOMY_CODE;
		
			if(taxonomyItem != null)
			{
				String taxonomyCode = taxonomyItem.getTaxonomyCode();
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getCurrentFactorId(), tag, taxonomyCode);
				getProject().executeCommand(cmd);
			}
			else 
			{
				EAM.errorDialog(EAM
						.text("Please choose a specific classification not a catagory"));
				String code = getCurrentDiagramFactor().getUnderlyingObject().getData(tag);
				for (int i=0; i<thisComboBox.getItemCount(); i++) 
				{
					TaxonomyItem foundItem = (TaxonomyItem)thisComboBox.getItemAt(i);
					if (code.equals(foundItem.getTaxonomyCode())) 
						thisComboBox.setSelectedIndex(i);
				}
				
			}
		}
		catch(CommandFailedException e)
		{
			EAM.logException(e);
			EAM.errorDialog("That action failed due to an unknown error");
		}
	}
	
	public JComponent createComment(String comment)
	{
		commentField = new UiTextArea(4, 25);
		commentField.setWrapStyleWord(true);
		commentField.setLineWrap(true);
		commentField.setText(comment);
		commentField.addFocusListener(new CommentFocusHandler());

		JScrollPane component = new JScrollPane(commentField);
		return component;
	}

	class CommentFocusHandler implements FocusListener
	{
		public void focusGained(FocusEvent event)
		{
		}

		public void focusLost(FocusEvent event)
		{
			String newComment = getComment();
			if(newComment.equals(getCurrentDiagramFactor().getComment()))
				return;
			try
			{
				int type = ObjectType.MODEL_NODE;
				String tag = Factor.TAG_COMMENT;
				CommandSetObjectData cmd = new CommandSetObjectData(type,
						getCurrentFactorId(), tag, newComment);
				getProject().executeCommand(cmd);
			}
			catch(CommandFailedException e)
			{
				EAM.logException(e);
				EAM.errorDialog("That action failed due to an unknown error");
			}
		}

	}

	JComponent createThreatClassificationDropdown()
	{
		String[] choices = { "error processing classifications", };
		try
		{
			TaxonomyItem[] taxonomyItems = TaxonomyLoader
					.load("ThreatTaxonomies.txt");
			dropdownThreatClassification = new UiComboBox(taxonomyItems);

			String taxonomyCode = getCurrentDiagramFactor().getUnderlyingObject()
					.getData(Cause.TAG_TAXONOMY_CODE);

			TaxonomyItem foundTaxonomyItem = SearchTaxonomyClassificationsForCode(
					taxonomyItems, taxonomyCode);

			if(foundTaxonomyItem == null)
			{
				String errorMessage = "Threat not found in table ; please make another selection";
				EAM.errorDialog(EAM.text(errorMessage));
				foundTaxonomyItem = taxonomyItems[0];
			}

			dropdownThreatClassification.setSelectedItem(foundTaxonomyItem);

			dropdownThreatClassification
					.addActionListener(new ThreatClassificationChangeHandler());

			return dropdownThreatClassification;

		}
		catch(Exception e)
		{
			EAM.logException(e);
			return new UiComboBox(choices);
		}
	}

	JComponent createStrategyClassificationDropdown()
	{
		String[] choices = { "error processing classifications", };
		try
		{
			TaxonomyItem[] taxonomyItems = TaxonomyLoader
					.load("StrategyTaxonomies.txt");
			dropdownStrategyClassification = new UiComboBox(taxonomyItems);
			
			String taxonomyCode = getCurrentDiagramFactor().getUnderlyingObject()
			.getData(Cause.TAG_TAXONOMY_CODE);

			TaxonomyItem foundTaxonomyItem = SearchTaxonomyClassificationsForCode(
					taxonomyItems, taxonomyCode);

			if(foundTaxonomyItem == null)
			{
				String errorMessage = "Strategy not found in table ; please make another selection";
				EAM.errorDialog(EAM.text(errorMessage));
				foundTaxonomyItem = taxonomyItems[0];
			}

			dropdownStrategyClassification.setSelectedItem(foundTaxonomyItem);

			dropdownStrategyClassification
				.addActionListener(new StrategyClassificationChangeHandler());

			return dropdownStrategyClassification;
		}
		catch(Exception e)
		{
			EAM.logException(e);
			return new UiComboBox(choices);
		}

	}
	
	TaxonomyItem SearchTaxonomyClassificationsForCode(
			TaxonomyItem[] taxonomyItems, String taxonomyCode)
	{
		for(int i = 0; i < taxonomyItems.length; i++)
		{
			if(taxonomyItems[i].getTaxonomyCode().equals(taxonomyCode))
				return taxonomyItems[i];
		}
		return null;
	}

	class FactorTypeRenderer extends DefaultListCellRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			Component cell = super.getListCellRendererComponent(list, value,
					index, isSelected, cellHasFocus);
			if(((FactorType) value).isDirectThreat())
				setIcon(new DirectThreatIcon());
			if(((FactorType) value).isContributingFactor())
				setIcon(new ContributingFactorIcon());
			return cell;
		}
	}

	Command buildStatusCommand()
	{
		String newValue = Strategy.STATUS_REAL;
		if(statusCheckBox.isSelected())
			newValue = Strategy.STATUS_DRAFT;
		return new CommandSetObjectData(ObjectType.MODEL_NODE, currentDiagramFactor
				.getDiagramFactorId(), Strategy.TAG_STATUS,
				newValue);
	}

	Project getProject()
	{
		return getDiagram().getProject();
	}

	DiagramComponent getDiagram()
	{
		return diagram;
	}

	public String getText()
	{
		return textField.getText();
	}

	public String getComment()
	{
		return commentField.getText();
	}

	public TaxonomyItem getThreatTaxonomyItem()
	{
		TaxonomyItem taxonomyItem = (TaxonomyItem) dropdownThreatClassification.getSelectedItem();
		
		if (!taxonomyItem.isLeaf()) 
			return null;

		return taxonomyItem;
	}

	public TaxonomyItem getStrategyTaxonomyItem()
	{
		TaxonomyItem taxonomyItem = (TaxonomyItem) dropdownStrategyClassification.getSelectedItem();
		
		if (!taxonomyItem.isLeaf()) 
			return null;
		
		return taxonomyItem;
	}
	

	public String getSelectedImpactCode()
	{
		RatingChoice selected = getSelectedImpactChoice();
		if(selected == null)
			return "";
		return selected.getCode();
	}

	private RatingChoice getSelectedImpactChoice()
	{
		RatingChoice selected = (RatingChoice)impactComponent.getSelectedItem();
		return selected;
	}
	
	public String getSelectedDurationCode()
	{
		RatingChoice selected = getSelectedDurationChoice();
		if(selected == null)
			return "";
		return selected.getCode();
	}

	private RatingChoice getSelectedDurationChoice()
	{
		RatingChoice selected = (RatingChoice)durationComponent.getSelectedItem();
		return selected;
	}
	
	public String getSelectedFeasibilityCode()
	{
		RatingChoice selected = getSelectedFeasibilityChoice();
		if(selected == null)
			return "";
		return selected.getCode();
	}

	private RatingChoice getSelectedFeasibilityChoice()
	{
		RatingChoice selected = (RatingChoice)feasibilityComponent.getSelectedItem();
		return selected;
	}

	public String getSelectedCostCode()
	{
		RatingChoice selected = getSelectedCostChoice();
		if(selected == null)
			return "";
		return selected.getCode();
	}

	private RatingChoice getSelectedCostChoice()
	{
		RatingChoice selected = (RatingChoice)costComponent.getSelectedItem();
		return selected;
	}


	Component createFieldPanel(Component component)
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(component, BorderLayout.BEFORE_LINE_BEGINS);
		return panel;
	}

	static final int MAX_LABEL_LENGTH = 40;
	public static final int TAB_DETAILS = 0;
	public static final int TAB_INDICATORS = 1;
	public static final int TAB_OBJECTIVES = 2;
	public static final int TAB_GOALS = 3;

	JTabbedPane tabs;
	DialogGridPanel detailsTab;
	ObjectiveListManagementPanel objectivesTab;
	IndicatorListManagementPanel indicatorsTab;
	GoalListManagementPanel goalsTab;
	MainWindow mainWindow;
	DiagramComponent diagram;
	DiagramFactor currentDiagramFactor;
	UiTextField textField;
	UiTextArea commentField;
	UiCheckBox statusCheckBox;
	UiComboBox dropdownThreatClassification;
	UiComboBox dropdownStrategyClassification;
	UiComboBox impactComponent;
	UiComboBox durationComponent;
	UiComboBox feasibilityComponent;
	UiComboBox costComponent;
	UiLabel ratingComponent;
	boolean ignoreObjectiveChanges;
	

}
