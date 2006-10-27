/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.dialogfields;

import org.conservationmeasures.eam.ids.BaseId;
import org.conservationmeasures.eam.project.Project;
import org.martus.swing.UiTextField;

public class ObjectNumericInputField extends ObjectTextInputField
{
	public ObjectNumericInputField(Project projectToUse, int objectType, BaseId objectId, String tag)
	{
		super(projectToUse, objectType, objectId, tag, new UiTextField(10));
	}

}
