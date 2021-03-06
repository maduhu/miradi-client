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

package org.miradi.dialogs.fieldComponents;

import java.util.Vector;

import javax.swing.DefaultListCellRenderer;

import org.miradi.diagram.renderers.ChoiceItemXmlEncodedListCellRenderer;
import org.miradi.questions.ChoiceItem;
import org.miradi.questions.ChoiceQuestion;

public class ChoiceItemWithXmlTextRendererComboBox extends AbstractChoiceItemComboBox
{
	public ChoiceItemWithXmlTextRendererComboBox(ChoiceQuestion question)
	{
		super(question.getChoices());
	}

	public ChoiceItemWithXmlTextRendererComboBox(Vector<ChoiceItem> items)
	{
		super(items.toArray(new ChoiceItem[0]));
	}
	
	public ChoiceItemWithXmlTextRendererComboBox(ChoiceItem[] items)
	{
		super(items);
	}

	@Override
	protected DefaultListCellRenderer createListCellRenderer()
	{
		return new ChoiceItemXmlEncodedListCellRenderer();
	}
}
