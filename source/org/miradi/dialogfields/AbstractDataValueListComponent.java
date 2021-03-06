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
package org.miradi.dialogfields;

import java.util.Vector;

import javax.swing.event.ListSelectionListener;

import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;

abstract public class AbstractDataValueListComponent extends AbstractQuestionEditorComponent
{
	public AbstractDataValueListComponent(ChoiceQuestion questionToUse)
	{
		this(questionToUse, SINGLE_COLUMN);
	}
	
	public AbstractDataValueListComponent(ChoiceQuestion questionToUse,	int columnCount)
	{
		super(questionToUse, columnCount);
		
		listSelectionListeners = new Vector<ListSelectionListener>();
	}
	
	@Override
	public void toggleButtonStateChanged(ChoiceItem choiceItem, boolean isSelected) throws Exception
	{
		if (!shouldSkipNotification())
		{
			for(ListSelectionListener listSelectionListener : listSelectionListeners)
			{
				ChoiceItemListSelectionEvent event = new ChoiceItemListSelectionEvent(choiceItem,0,0, false);
				listSelectionListener.valueChanged(event);
			}
		}
	}
	
	@Override
	public void addListSelectionListener(ListSelectionListener listSelectionListenerToAdd)
	{
		if (!listSelectionListeners.contains(listSelectionListenerToAdd))
			listSelectionListeners.add(listSelectionListenerToAdd);
	}
	
	@Override
	public void removeListSelectionListener(ListSelectionListener listSelectionListenerToRemove)
	{
		listSelectionListeners.remove(listSelectionListenerToRemove);
	}

	private boolean shouldSkipNotification()
	{
		return skipNotification;
	}
	
	protected void enableSkipNotification()
	{
		skipNotification = true;
	}
	
	protected void disableSkipNotification()
	{
		skipNotification = false;
	}
	
	@Override
	abstract public String getText();
	
	@Override
	abstract public void setText(String codesToUse);
	
	private boolean skipNotification;
	private Vector<ListSelectionListener> listSelectionListeners;
}
