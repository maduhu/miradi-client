/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.views.noproject;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.Action;
import javax.swing.JScrollPane;

import org.conservationmeasures.eam.actions.ActionImportTncCapWorkbook;
import org.conservationmeasures.eam.actions.ActionImportZipFile;
import org.conservationmeasures.eam.actions.ActionNewProject;
import org.conservationmeasures.eam.actions.EAMAction;
import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.conservationmeasures.eam.project.Project;
import org.conservationmeasures.eam.views.umbrella.UmbrellaView;
import org.martus.swing.HtmlViewer;
import org.martus.swing.HyperlinkHandler;

public class NoProjectView extends UmbrellaView implements HyperlinkHandler
{
	public NoProjectView(MainWindow mainWindow) throws Exception
	{
		super(mainWindow);
		
		htmlViewer = new HtmlViewer("", this);
		setToolBar(new NoProjectToolBar(getActions()));
	}
	
	public void linkClicked(String linkDescription)
	{
		try 
		{
			if(linkDescription.equals(NoProjectHtmlText.NEW_PROJECT))
			{
				Action action = new ActionNewProject(getMainWindow());
				action.actionPerformed(null);
			}
			else if(linkDescription.startsWith(NoProjectHtmlText.OPEN_PREFIX))
			{
				String projectName = linkDescription.substring(NoProjectHtmlText.OPEN_PREFIX.length());
				File projectDirectory = new File(EAM.getHomeDirectory(), projectName);
				getMainWindow().createOrOpenProject(projectDirectory);
			}
			else if(linkDescription.equals(NoProjectHtmlText.IMPORT_ZIP))
			{
				EAMAction action = getMainWindow().getActions().get(ActionImportZipFile.class);
				action.doAction();
			}
			else if(linkDescription.equals(NoProjectHtmlText.IMPORT_TNC_CAP_PROJECT))
			{
				EAMAction action = getMainWindow().getActions().get(ActionImportTncCapWorkbook.class);
				action.doAction();
			}
			else if(linkDescription.equals("Definition:Project"))
			{
				EAM.okDialog("Definition: Project", new String[] {"A project is..."});
			}
			else if(linkDescription.equals("Definition:EAM"))
			{
				EAM.okDialog("Definition: e-Adaptive Management", new String[] {"e-Adaptive Management is..."});
			}
			else
			{
				EAM.okDialog("Not implemented yet", new String[] {"Not implemented yet"});
			}
		}
		catch (Exception e)
		{
			EAM.logException(e);
			EAM.errorDialog(EAM.text("Unable to process request: ") + e);
		}
	}

	public void becomeActive() throws Exception
	{
		super.becomeActive();
		removeAll();
		refreshText();
		
		JScrollPane scrollPane = new JScrollPane(htmlViewer);
		
		setLayout(new BorderLayout());
		add(scrollPane, BorderLayout.CENTER);
	}

	public void refreshText()
	{
		String htmlText = new NoProjectHtmlText().getText();
		htmlViewer.setText(htmlText);
	}
	
	public void becomeInactive() throws Exception
	{
		// nothing to do...would clear all view data
		super.becomeInactive();
	}

	public void valueChanged(String widget, String newValue)
	{
	}

	public void buttonPressed(String buttonName)
	{
	}

	public String cardName()
	{
		return getViewName();
	}
	
	static public String getViewName()
	{
		return Project.NO_PROJECT_VIEW_NAME;
	}

	HtmlViewer htmlViewer;
}

