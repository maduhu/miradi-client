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
package org.miradi.main.menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.miradi.actions.Actions;
import org.miradi.main.MiradiHtmlMenuItem;

public abstract class MiradiMenu extends JMenu
{
	public MiradiMenu(String text, Actions actionsToUse)
	{
		super(text);
		actions = actionsToUse;
	}
	
	void addMenuItem(Class actionClass, int mnemonic)
	{
		MiradiHtmlMenuItem menuItemNewProject = new MiradiHtmlMenuItem(actions.get(actionClass), mnemonic);
		add(menuItemNewProject);
	}
	
	void addDisabledMenuItem(String label)
	{
		JMenuItem item = new JMenuItem(label);
		item.setEnabled(false);
		add(item);
	}
	
	Actions actions;
}
