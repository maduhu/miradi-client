/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.summary;

import java.awt.BorderLayout;

import javax.swing.Box;

import org.conservationmeasures.eam.actions.ActionModifyResource;
import org.conservationmeasures.eam.actions.ActionTeamCreateMember;
import org.conservationmeasures.eam.actions.ActionTeamDeleteMember;
import org.conservationmeasures.eam.actions.ActionViewPossibleTeamMembers;
import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.commands.CommandSetObjectData;
import org.conservationmeasures.eam.dialogs.DisposablePanel;
import org.conservationmeasures.eam.dialogs.ObjectListTable;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.utils.MouseAdapterDoubleClickDelegator;
import org.martus.swing.UiButton;
import org.martus.swing.UiScrollPane;

public class TeamEditorComponent extends DisposablePanel
{
	public TeamEditorComponent(Project projectToUse, Actions actions)
	{
		super(new BorderLayout());
		project = projectToUse;

		teamModel = new TeamModel(project);
		teamTable = new ObjectListTable(teamModel);
		teamTable.resizeTable(10);
		teamTable.addMouseListener(new MouseAdapterDoubleClickDelegator(actions.get(ActionModifyResource.class)));
		
		add(new UiScrollPane(teamTable), BorderLayout.CENTER);
		add(createButtonBar(actions), BorderLayout.AFTER_LINE_ENDS);
	}
	
	public void dispose()
	{
		super.dispose();
	}
	
	public void updateTableAfterCommand(CommandSetObjectData cmd)
	{
		teamTable.updateTableAfterCommand(cmd);
	}
	
	Box createButtonBar(Actions actions)
	{
		Box box = Box.createVerticalBox();
		box.add(new UiButton(actions.get(ActionTeamCreateMember.class)));
		box.add(createObjectsActionButton(actions.getObjectsAction(ActionTeamDeleteMember.class), teamTable));
		box.add(createObjectsActionButton(actions.getObjectsAction(ActionModifyResource.class), teamTable));
		box.add(new UiButton(actions.get(ActionViewPossibleTeamMembers.class)));
		return box;
	}
	
	Project project;
	TeamModel teamModel;
	ObjectListTable teamTable;
}