/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
(on behalf of the Conservation Measures Partnership, "CMP") and 
Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 

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

package org.miradi.dialogfields;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import org.martus.swing.Utilities;
import org.miradi.dialogs.base.DisposablePanel;
import org.miradi.dialogs.base.MiradiPanel;
import org.miradi.dialogs.base.ModalDialogWithClose;
import org.miradi.dialogs.fieldComponents.PanelButton;
import org.miradi.layout.OneColumnPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.ORef;
import org.miradi.project.Project;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.Translation;

abstract public class AbstractEditableCodeListField extends ObjectDataInputField
{
	public AbstractEditableCodeListField(Project projectToUse, ORef refToUse, String tagToUse, ChoiceQuestion questionToUse, int columnCount)
	{
		super(projectToUse, refToUse, tagToUse);
		
		question = questionToUse;
		component = new MiradiPanel(new BorderLayout());
		readOnlyCodeListComponent = new ReadOnlyCodeListComponent(questionToUse.getChoices(), columnCount);
		component.add(readOnlyCodeListComponent, BorderLayout.CENTER);
		
		selectButton = new PanelButton(EAM.text("Select..."));

		selectButton.addActionListener(new SelectButtonHandler());
		OneColumnPanel buttonPanel = new OneColumnPanel();
		buttonPanel.setBackground(EAM.READONLY_BACKGROUND_COLOR);
		buttonPanel.setForeground(EAM.READONLY_FOREGROUND_COLOR);
		buttonPanel.add(selectButton);
		setDefaultFieldBorder();
		component.add(buttonPanel, BorderLayout.AFTER_LINE_ENDS);
	}
	
	@Override
	public void updateEditableState()
	{
		super.updateEditableState();
		
		selectButton.setEnabled(isValidObject());			
	}
	
	@Override
	public JComponent getComponent()
	{
		return component;
	}

	@Override
	public String getText()
	{
		return readOnlyCodeListComponent.getText();
	}

	@Override
	public void setText(String newValue)
	{
		readOnlyCodeListComponent.setText(newValue);
	}
	
	public class SelectButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent event)
		{
			try
			{
				DisposablePanel editorPanel = createEditorPanel();
				ModalDialogWithClose dialog = new ModalDialogWithClose(EAM.getMainWindow(), Translation.fieldLabel(getObjectType(), getTag()));
				dialog.setScrollableMainPanel(editorPanel);
				Utilities.centerDlg(dialog);
				dialog.setVisible(true);
			}
			catch (Exception e)
			{
				EAM.logException(e);
				EAM.unexpectedErrorDialog(e);
			}
		}
	}
	
	abstract protected DisposablePanel createEditorPanel() throws Exception;
	
	protected ChoiceQuestion question;
	private MiradiPanel component;
	private ReadOnlyCodeListComponent readOnlyCodeListComponent;
	private PanelButton selectButton;
}