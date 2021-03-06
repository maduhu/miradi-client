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


import java.util.Set;

import javax.swing.JToggleButton;

import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;
import org.miradi.utils.CodeList;
import org.miradi.utils.HtmlUtilities;
import org.miradi.utils.XmlUtilities2;

abstract public class AbstractQuestionBasedComponent extends AbstractDataValueListComponent
{
	public AbstractQuestionBasedComponent(ChoiceQuestion questionToUse, int columnCount)
	{
		super(questionToUse, columnCount);
		
		codesToDisable = new CodeList();
	}
	
	protected CodeList getSelectedCodes()
	{
		CodeList codes = new CodeList();
		Set<ChoiceItem> choices =  choiceItemToToggleButtonMap.keySet();
		for(ChoiceItem choiceItem : choices)
		{	
			JToggleButton toggleButton = choiceItemToToggleButtonMap.get(choiceItem);
			if (toggleButton.isSelected())
			{
				codes.add(choiceItem.getCode());
			}
		}
		
		return codes;
	}
	
	protected void updateToggleButtonSelections(CodeList codes)
	{
		enableSkipNotification();
		try
		{
			Set<ChoiceItem> choices =  choiceItemToToggleButtonMap.keySet();
			for(ChoiceItem choiceItem : choices)
			{	
				JToggleButton toggleButton = choiceItemToToggleButtonMap.get(choiceItem);
				toggleButton.setSelected(false);
				boolean isChecked  = codes.contains(choiceItem.getCode());
				toggleButton.setSelected(isChecked);
			}
			
			setSameToolTipForAllToggleButtons();
		}
		finally
		{
			disableSkipNotification();
		}
	}

	protected void setSameToolTipForAllToggleButtons()
	{
		String partialToolTip = ""; 
		int selectionCount = 0;
		Set<ChoiceItem> choices = choiceItemToToggleButtonMap.keySet();
		ChoiceItem[] choicesAsArray = choices.toArray(new ChoiceItem[0]);
		for (int index = 0; (index < choicesAsArray.length && selectionCount <= MAX_ITEMS_COUNT_IN_TOOLTIP); ++index)
		{
			ChoiceItem choiceItem = choicesAsArray[index];
			JToggleButton toggleButton = choiceItemToToggleButtonMap.get(choiceItem);
			if (toggleButton.isSelected() )
			{
				partialToolTip += XmlUtilities2.getXmlEncoded(choiceItem.getLabel()) + HtmlUtilities.BR_TAG;
				++selectionCount;
			}
		}
		
		String moreText = "";
		if (getSelectedCodes().size() > (MAX_ITEMS_COUNT_IN_TOOLTIP  + 1))
			moreText = "...more";
		
		String rawCombinedText = partialToolTip + moreText;
		String toolTip = HtmlUtilities.wrapInHtmlTags(rawCombinedText);
		for(ChoiceItem choiceItem : choices)
		{
			JToggleButton toggleButton = choiceItemToToggleButtonMap.get(choiceItem);
			if (rawCombinedText.length() != 0)
				toggleButton.setToolTipText(toolTip);
		}
	}
	
	@Override
	public void setEnabled(boolean isValidObject)
	{
		super.setEnabled(isValidObject);
		disableCheckBoxes();
	}
	
	public void setSingleButtonEnabled(ChoiceItem buttonChoice, boolean shouldBeEnabled)
	{
		JToggleButton button = choiceItemToToggleButtonMap.get(buttonChoice);
		button.setEnabled(shouldBeEnabled);
	}

	protected void disableCheckBoxes()
	{
		Set<ChoiceItem> choices = choiceItemToToggleButtonMap.keySet();
		for(ChoiceItem choiceItem : choices)
		{
			JToggleButton toggleButton = choiceItemToToggleButtonMap.get(choiceItem);
			if (codesToDisable.contains(choiceItem.getCode()))
				toggleButton.setEnabled(false);
		}
	}

	public void setDisabledCodes(CodeList codesToDiableToUse)
	{
		codesToDisable = codesToDiableToUse;
	}
	
	@Override
	public String getText()
	{
		throw new RuntimeException("Unexpected call to getText");
	}

	@Override
	public void setText(String codesToUse)
	{
		throw new RuntimeException("Unexpected call to setText");
	}
	
	private CodeList codesToDisable;
	private static final int MAX_ITEMS_COUNT_IN_TOOLTIP = 5;
}
