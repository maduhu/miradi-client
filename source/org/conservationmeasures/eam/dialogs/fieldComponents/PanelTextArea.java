/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.dialogs.fieldComponents;

import java.awt.Font;

import org.conservationmeasures.eam.main.EAM;
import org.conservationmeasures.eam.main.MainWindow;
import org.martus.swing.UiTextArea;

public class PanelTextArea extends UiTextArea
{
	public PanelTextArea(int rows, int cols)
	{
		super(rows, cols);
		setFont(new Font(getMainWindow().getDataPanelFontFamily(),Font.PLAIN, getMainWindow().getDataPanelFontSize()));
	}

	public PanelTextArea(String text)
	{
		super(text);
		setFont(new Font(getMainWindow().getDataPanelFontFamily(),Font.PLAIN, getMainWindow().getDataPanelFontSize()));
	}
	
	public MainWindow getMainWindow()
	{
		return EAM.mainWindow;
	}
}