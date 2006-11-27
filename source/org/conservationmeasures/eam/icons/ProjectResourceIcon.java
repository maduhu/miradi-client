/*
 * Copyright 2006, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */
package org.conservationmeasures.eam.icons;

import java.awt.Color;

import org.conservationmeasures.eam.diagram.renderers.FactorRenderer;
import org.conservationmeasures.eam.diagram.renderers.RectangleRenderer;

public class ProjectResourceIcon extends EamIcon
{
	Color getIconColor()
	{
		return Color.MAGENTA;
	}

	FactorRenderer getRenderer()
	{
		return new RectangleRenderer();
	}

}
