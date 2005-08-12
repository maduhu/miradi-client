/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.commands.CommandSetNodeText;
import org.conservationmeasures.eam.diagram.nodes.EAMGraphCell;
import org.conservationmeasures.eam.diagram.nodes.Node;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.conservationmeasures.eam.main.BaseProject;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.NodePropertiesDialog;
import org.conservationmeasures.eam.views.ProjectDoer;

public class NodeProperties extends ProjectDoer
{
	public NodeProperties(BaseProject project)
	{
		super(project);
	}

	public boolean isAvailable()
	{
		if(!getProject().isOpen())
			return false;
		
		EAMGraphCell[] selected = getProject().getOnlySelectedCells();
		if(selected.length != 1)
			return false;
		if(!selected[0].isNode())
			return false;
		return true;
	}

	public void doIt() throws CommandFailedException
	{
		if(!isAvailable())
			return;
		
		EAMGraphCell[] selected = getProject().getOnlySelectedCells();
		Node selectedNode = (Node)selected[0];
		if(selectedNode == null)
			return;
		String title = EAM.text("Title|Node Properties");
		NodePropertiesDialog dlg = new NodePropertiesDialog(EAM.mainWindow, title, selectedNode);
		dlg.setVisible(true);
		if(!dlg.getResult())
			return;

		int id = selectedNode.getId();
		CommandSetNodeText command = new CommandSetNodeText(id, dlg.getText());
		getProject().executeCommand(command);
	}

}
