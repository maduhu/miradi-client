/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.conservationmeasures.eam.actions.Actions;
import org.conservationmeasures.eam.commands.Command;
import org.conservationmeasures.eam.diagram.DiagramView;
import org.conservationmeasures.eam.exceptions.CommandFailedException;
import org.martus.swing.UiScrollPane;

public class MainWindow extends JFrame implements CommandExecutedListener, ClipboardOwner
{
	public MainWindow()
	{
		project = new Project();
	}
	
	public void start()
	{
		project.addCommandExecutedListener(this);

		actions = new Actions(this);
		mainMenuBar = new MainMenuBar(actions);
		toolBarBox = new ToolBarContainer();
		MainStatusBar mainStatusBar = new MainStatusBar();

		updateTitle();
		setSize(new Dimension(700, 500));
		setJMenuBar(mainMenuBar);
		getContentPane().add(toolBarBox, BorderLayout.BEFORE_FIRST_LINE);
		getContentPane().add(mainStatusBar, BorderLayout.AFTER_LAST_LINE);

		addWindowListener(new WindowEventHandler());

		viewHolder = new JPanel();
		viewHolder.setLayout(new CardLayout());
		viewHolder.add(createNoProjectView(), "No project");
		viewHolder.add(createDiagramView(), "Diagram");
		getContentPane().add(viewHolder, BorderLayout.CENTER);
		setVisible(true);
	}

	private JComponent createNoProjectView()
	{
		// TODO: There *MUST* be a simpler way to center this view!
		NoProjectView noProjectView = new NoProjectView(this);
		noProjectView.setAlignmentX(0.5f);
		noProjectView.setAlignmentY(0.5f);
		
		Box centerHorizontally = Box.createVerticalBox();
		//centerHorizontally.setBorder(new LineBorder(Color.RED));
		centerHorizontally.add(noProjectView);
		
		Box centerVertically = Box.createHorizontalBox();
		//centerVertically.setBorder(new LineBorder(Color.YELLOW));
		centerVertically.add(Box.createHorizontalGlue());
		centerVertically.add(centerHorizontally);
		centerVertically.add(Box.createHorizontalGlue());
		
		return centerVertically;
	}
	
	private JComponent createDiagramView()
	{
		diagramComponent = new DiagramView(this, project.getDiagramModel());
		return new UiScrollPane(diagramComponent);
	}
	
	private void setCurrentView(String name)
	{
		CardLayout layout = (CardLayout)viewHolder.getLayout();
		layout.show(viewHolder, name);
	}

	public Project getProject()
	{
		return project;
	}
	
	public DiagramView getDiagramComponent()
	{
		return diagramComponent;
	}
	
	public Actions getActions()
	{
		return actions;
	}
	
	public boolean isProjectOpen()
	{
		return project.isOpen();
	}
	
	public void loadProject(File projectFile)
	{
		try
		{
			mainToolBar = new DiagramToolBar(actions);
			toolBarBox.removeAll();
			toolBarBox.add(mainToolBar);

			setCurrentView("Diagram");
			
			project.load(this, projectFile);
			validate();
			updateTitle();
		}
		catch(IOException e)
		{
			EAM.logException(e);
		}
		catch(CommandFailedException e)
		{
			EAM.logException(e);
		}
		
		actions.updateActionStates();

	}

	public void recordCommand(Command command)
	{
		project.recordCommand(command);
	}
	
	public void exitNormally()
	{
		System.exit(0);
	}
	
	public void commandExecuted(CommandExecutedEvent event)
	{
		actions.updateActionStates();
	}
	
	public void lostOwnership(Clipboard clipboard, Transferable contents) 
	{
	}
	
	private void updateTitle()
	{
		setTitle(EAM.text("Title|CMP e-Adaptive Management") + " - " + project.getName());
	}
	
	class WindowEventHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent event)
		{
			exitNormally();
		}
	}

	private Actions actions;
	private Project project;
	private DiagramView diagramComponent;
	private JPanel viewHolder;
	private JPanel toolBarBox;
	private DiagramToolBar mainToolBar;
	private MainMenuBar mainMenuBar;
}
