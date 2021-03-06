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
package org.miradi.views.umbrella;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.martus.swing.UiButton;
import org.martus.swing.UiList;
import org.martus.swing.UiScrollPane;
import org.martus.swing.UiTextField;
import org.martus.util.DirectoryUtils;
import org.miradi.dialogs.base.DialogWithButtonBar;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.dialogs.fieldComponents.PanelTextFieldWithSelectAllOnFocusGained;
import org.miradi.dialogs.fieldComponents.PanelTitleLabel;
import org.miradi.legacyprojects.LegacyProjectUtilities;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;
import org.miradi.project.Project;
import org.miradi.utils.IgnoreCaseStringComparator;
import org.miradi.utils.MiradiScrollPane;
import org.miradi.utils.ProjectNameRestrictedTextField;
import org.miradi.wizard.noproject.FileSystemTreeNode;

import com.jhlabs.awt.GridLayoutPlus;

public class CreateProjectDialog extends DialogWithButtonBar implements ActionListener,
		ListSelectionListener
{
	public CreateProjectDialog(MainWindow parent, String title, File proposedProjectDirToUse, String proposedProjectNameToUse) throws Exception
	{
		super(parent);
		
		setTitle(title);
		setModal(true);
		setResizable(true);

		proposedProjectDir = proposedProjectDirToUse;
		oldName = proposedProjectNameToUse;
		
		final Vector<Component> buttons = createButtonComponents();
		final PanelTextFieldWithSelectAllOnFocusGained oldNameField = new PanelTextFieldWithSelectAllOnFocusGained(oldName);
		oldNameField.setEditable(false);

		projectFilenameField = createTextArea();
		projectFilenameField.setText(Project.withoutMpfProjectSuffix(oldName));
		
		existingProjectList = createExistingProjectList();
		UiScrollPane uiScrollPane = new UiScrollPane(existingProjectList);
		uiScrollPane.setPreferredSize(new Dimension(projectFilenameField.getPreferredSize().width, 200));

		MiradiPanel panel = new MiradiPanel(new GridLayoutPlus(0, 2));
		panel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		final PanelTextFieldWithSelectAllOnFocusGained projectDirectoryPanel = new PanelTextFieldWithSelectAllOnFocusGained(proposedProjectDir.getParent());
		projectDirectoryPanel.setEditable(false);
		panel.add(new PanelTitleLabel(EAM.text("Current project directory:")));
		panel.add(projectDirectoryPanel);
		
		panel.add(new PanelTitleLabel(EAM.text("Current project name:")));
		panel.add(oldNameField);
		
		panel.add(new PanelTitleLabel(EAM.text("New project name: ")));
		panel.add(projectFilenameField);
		
		panel.add(new JLabel(" "));
		panel.add(new JLabel(" "));

		panel.add(new PanelTitleLabel(EAM.text("Miradi data directory:")));
		panel.add(new PanelTitleLabel(EAM.getHomeDirectory().getAbsolutePath()));
		
		panel.add(new PanelTitleLabel(EAM.text("Label|Existing Projects:")));
		panel.add(uiScrollPane);
		
		getContentPane().add(new MiradiScrollPane(panel));
		
		setButtons(buttons);
		
		projectFilenameField.requestFocusInWindow();
		getRootPane().setDefaultButton(okButton);
	}

	public boolean showSaveAsDialog()
	{
		okButton.setText(EAM.text("Button|Save"));
		return showDialog();
	}

	private boolean showDialog()
	{
		pack();
		setVisible(true);
		return getResult();
	}

	public File getSelectedFile()
	{
		String filename = projectFilenameField.getText();
		if(!filename.endsWith(".Miradi"))
			filename += ".Miradi";
		
		return new File(getProjectDir(), filename);
	}

	private String getSelectedFilename()
	{
		return projectFilenameField.getText();
	}

	private UiList createExistingProjectList()
	{
		File home = EAM.getHomeDirectory();
		final String[] fileList = home.list(new ProjectFilter());
		Arrays.sort(fileList, new IgnoreCaseStringComparator());
		UiList list = new UiList(fileList);
		list.addListSelectionListener(this);
		list.addMouseListener(new DoubleClickHandler(this));
		return list;
	}

	static class DoubleClickHandler extends MouseAdapter
	{
		public DoubleClickHandler(CreateProjectDialog dialogToControl)
		{
			dialog = dialogToControl;
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount() != 2)
				return;

			dialog.ok();
		}

		CreateProjectDialog dialog;
	}

	public static class ProjectFilter implements FilenameFilter
	{
		public boolean accept(File directory, String projectName)
		{
			if(projectName.startsWith("."))
				return false;

			File projectFile = new File(directory, projectName);
			try
			{
				return FileSystemTreeNode.isProjectFile(projectFile);
			}
			catch(Exception e)
			{
				EAM.logException(e);
				return false;
			}
		}
	}

	private UiTextField createTextArea()
	{
		ProjectNameRestrictedTextField textField = new ProjectNameRestrictedTextField();
		textField.requestFocus(true);
		textField.selectAll();
		textField.getDocument().addDocumentListener(new TextFieldListener());
		return textField;
	}

	class TextFieldListener implements DocumentListener
	{
		public void changedUpdate(DocumentEvent e)
		{
			enableOkButtonIfNotEmpty();
		}

		public void insertUpdate(DocumentEvent e)
		{
			enableOkButtonIfNotEmpty();
		}

		public void removeUpdate(DocumentEvent e)
		{
			enableOkButtonIfNotEmpty();
		}

	}

	private void enableOkButtonIfNotEmpty()
	{
		boolean isNotEmpty = (projectFilenameField.getText().length() > 0);
		okButton.setEnabled(isNotEmpty);
	}

	private Vector<Component> createButtonComponents()
	{
		okButton = new PanelButton("");
		okButton.addActionListener(this);
		okButton.setEnabled(false);
		cancelButton = new PanelButton(EAM.text("Button|Cancel"));
		cancelButton.addActionListener(this);

		Vector<Component> buttons = new Vector<Component>();
		buttons.add(Box.createHorizontalGlue());
		buttons.add(okButton);
		buttons.add(Box.createHorizontalStrut(10));
		buttons.add(cancelButton);
		
		return buttons;
	}

	public boolean getResult()
	{
		return result;
	}

	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == okButton)
			ok();
		else
			cancel();
	}

	public void ok()
	{
		try
		{
			confirmAndPrepare();
		}
		catch(Exception e)
		{
			EAM.alertUserOfNonFatalException(e);
			result = false;
			dispose();
		}

	}

	private void confirmAndPrepare() throws Exception
	{
		final String newName = getSelectedFilename();
		if(newName.equals(oldName))
		{
			EAM.errorDialog(EAM.text("Cannot save a project back to its existing name"));
			return;
		}
		
		if(!Project.isValidProjectName(newName))
		{
			EAM.errorDialog(getInvalidProjectNameMessage());
			return;
		}

		if(getSelectedFile().exists())
		{
			Project project = ((MainWindow)getOwner()).getProject();
			if(project.isOpen()
					&& newName.equals(project.getFilename()))
			{
				String body = EAM.text("Cannot overwrite an open project");
				EAM.errorDialog(body);
				return;
			}

			if (!LegacyProjectUtilities.isExistingLocalProject(getSelectedFile())) 
			{
				String body = EAM.text("File exists: Cannot overwrite a non project folder");
				EAM.errorDialog(body);
				return;
			}
			
			String title = EAM.text("Title|Overwrite existing file?");
			if(!EAM.confirmOverwriteDialog(title, EAM.text("This will replace the existing file.")))
				return;
			DirectoryUtils.deleteEntireDirectoryTree(getSelectedFile());
		}

		result = true;
		dispose();
	}

	public static String getInvalidProjectNameMessage()
	{
		return EAM.text("Project filenames cannot contain punctuation other than dots, dashes, and spaces; and they cannot be longer than 32 characters. ");
	}

	public void cancel()
	{
		result = false;
		dispose();
	}

	public void valueChanged(ListSelectionEvent arg0)
	{
		projectFilenameField.setText((String) existingProjectList
				.getSelectedValue());
	}
	
	private File getProjectDir()
	{
		return proposedProjectDir;
	}

	private File proposedProjectDir;
	private String oldName;
	private boolean result;
	private UiList existingProjectList;
	private UiTextField projectFilenameField;
	private UiButton okButton;
	private UiButton cancelButton;
}
