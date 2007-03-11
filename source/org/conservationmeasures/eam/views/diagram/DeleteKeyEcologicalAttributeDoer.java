/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.diagram;

import org.conservationmeasures.eam.objecthelpers.ObjectType;
import org.conservationmeasures.eam.objects.Factor;

public class DeleteKeyEcologicalAttributeDoer extends DeleteAnnotationDoer
{
	
	public boolean isAvailable()
	{
		if (getObjects().length != 1)
			return false;
		
		if (getObjects()[getObjects().length-1].getType() != ObjectType.KEY_ECOLOGICAL_ATTRIBUTE)
			return false;
		
		return true;
	}

	
	String[] getDialogText()
	{
		return new String[] { "Are you sure you want to delete this Key Ecological Attribute?",};
	}

	String getAnnotationIdListTag()
	{
		return Factor.TAG_KEY_ECOLOGICAL_ATTRIBUTE_IDS;
	}
}