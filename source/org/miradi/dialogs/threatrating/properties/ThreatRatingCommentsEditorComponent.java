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
package org.miradi.dialogs.threatrating.properties;

import javax.swing.JComponent;

import org.miradi.actions.Actions;
import org.miradi.commands.CommandSetObjectData;
import org.miradi.dialogfields.FieldSaver;
import org.miradi.dialogfields.ObjectScrollingMultilineInputField;
import org.miradi.dialogfields.SavableField;
import org.miradi.dialogs.base.AbstractObjectDataInputPanel;
import org.miradi.main.EAM;
import org.miradi.objecthelpers.CodeToUserStringMap;
import org.miradi.objecthelpers.ORef;
import org.miradi.objecthelpers.ORefList;
import org.miradi.objects.ThreatRatingCommentsData;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.utils.EditableHtmlPane;
import org.miradi.utils.HtmlEditorRightClickMouseHandler;
import org.miradi.utils.MiradiScrollPane;

import com.inet.jortho.SpellChecker;

public class ThreatRatingCommentsEditorComponent extends SavableField
{
	public ThreatRatingCommentsEditorComponent(Project projectToUse, Actions actions) throws Exception
	{
		super();
		
		project = projectToUse;		
		selectedHierarchy = new ORefList();
		panelTextArea = new EditableHtmlPane(EAM.getMainWindow(), AbstractObjectDataInputPanel.DEFAULT_TEXT_COLUMN_COUNT, ObjectScrollingMultilineInputField.INITIAL_MULTI_LINE_TEXT_AREA_ROW_COUNT);
		
		panelTextArea.setForeground(EAM.EDITABLE_FOREGROUND_COLOR);
		panelTextArea.setBackground(EAM.EDITABLE_BACKGROUND_COLOR);
		
		new HtmlEditorRightClickMouseHandler(actions, panelTextArea);
		panelTextArea.addFocusListener(this);
		
		if(EAM.getMainWindow().isSpellCheckerActive())
			SpellChecker.register(panelTextArea, false, false, true);
	}
	
	public void setObjectRefs(ORefList selectedHierarchyToUse)
	{
		FieldSaver.savePendingEdits();
		selectedHierarchy = selectedHierarchyToUse;

		updateText();
	}

	private void updateText()
	{
		ORef threatRef = getThreatRef();
		ORef targetRef = getTargetRef();
		String comments = getThreatRatingCommentsData().findComment(threatRef, targetRef);
		
		getTextArea().setText(comments);
		getTextArea().invalidate();
	}

	private ThreatRatingCommentsData getThreatRatingCommentsData()
	{
		return getProject().getSingletonThreatRatingCommentsData();
	}

	public JComponent getComponent()
	{
		if(scrollPane == null)
			scrollPane = new MiradiScrollPane(getTextArea());
		
		return scrollPane;
	}
	
	private ORef getTargetRef()
	{
		return getSelectedHierarchy().getRefForType(TargetSchema.getObjectType());
	}

	private ORef getThreatRef()
	{
		return getSelectedHierarchy().getRefForType(CauseSchema.getObjectType());
	}
	
	private ORefList getSelectedHierarchy()
	{
		return selectedHierarchy;
	}
	
	private EditableHtmlPane getTextArea()
	{
		return panelTextArea;
	}
		
	public Project getProject()
	{
		return project;
	}
	
	@Override
	public void saveIfNeeded()
	{
		try
		{
			ThreatRatingCommentsData threatRatingCommentsData = getThreatRatingCommentsData();
			CodeToUserStringMap commentsMap = threatRatingCommentsData.getThreatRatingCommentsMap();
			String threatTargetKey = ThreatRatingCommentsData.createKey(getThreatRef(), getTargetRef());
			commentsMap.putUserString(threatTargetKey, getTextArea().getText());
			CommandSetObjectData setComment = new CommandSetObjectData(threatRatingCommentsData.getRef(), threatRatingCommentsData.getThreatRatingCommentsMapTag(), commentsMap.toJsonString());
			getProject().executeCommand(setComment);
		}
		catch (Exception  e)
		{
			EAM.logException(e);
		}
	}
	
	private Project project;
	private MiradiScrollPane scrollPane;
	private EditableHtmlPane panelTextArea;
	private ORefList selectedHierarchy;
}
