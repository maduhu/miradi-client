/* 
Copyright 2005-2008, Foundations of Success, Bethesda, Maryland 
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
package org.miradi.utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.miradi.exceptions.CommandFailedException;
import org.miradi.main.EAM;
import org.miradi.main.MainWindow;


public abstract class EAMFileSaveChooser
{

	MainWindow mainWindow;

	EAMFileSaveChooser(MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}

	public File displayChooser() throws CommandFailedException
	{
		
		JFileChooser dlg = new JFileChooser(currentDirectory);

		dlg.setDialogTitle(EAM.text(getDialogApproveTitleText()));
		FileFilter[] filters = getFileFilter();
		for (int i=0; i<filters.length; ++i)
			dlg.addChoosableFileFilter(filters[i]);
		dlg.setDialogType(JFileChooser.CUSTOM_DIALOG);
		dlg.setApproveButtonToolTipText(EAM.text(getApproveButtonToolTipText()));
		if (dlg.showDialog(mainWindow, EAM.text(getDialogApprovelButtonText())) != JFileChooser.APPROVE_OPTION)
			return null;

		File chosen = dlg.getSelectedFile();
		String ext = ((MiradiFileFilter)dlg.getFileFilter()).getFileExtension();
		if (!chosen.getName().toLowerCase().endsWith(ext))
			chosen = new File(chosen.getAbsolutePath() + ext);

		if (chosen.exists())
		{
			String title = EAM.text(getDialogOverwriteTitleText());
			String[] body = { EAM.text(getDialogOverwriteBodyText()) };
			if (!EAM.confirmDialog(title, body))
				return null;
			chosen.delete();
		}

		currentDirectory = chosen.getParent();
		return chosen;

	}

	public String getDialogApproveTitleText()
	{
		return EAM.text("Title|Save " + getUiExtensionTag() + " File");
	}

	public String getApproveButtonToolTipText()
	{
		return EAM.text("TT|Save " + getUiExtensionTag() + " File");
	}

	public String getDialogApprovelButtonText()
	{
		return EAM.text("Save " + getUiExtensionTag());
	}

	public String getDialogOverwriteTitleText()
	{
		return EAM.text("Title|Overwrite existing file?");
	}

	public String getDialogOverwriteBodyText()
	{
		return EAM.text("This will replace the existing file.");
	}
	
	public abstract FileFilter[] getFileFilter();
	
	public abstract String getUiExtensionTag();
	
	private static String currentDirectory;

}
