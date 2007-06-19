/* 
* Copyright 2005-2007, Wildlife Conservation Society, 
* Bronx, New York (on behalf of the Conservation Measures Partnership, "CMP") and 
* Beneficent Technology, Inc. ("Benetech"), Palo Alto, California. 
*/ 
package org.conservationmeasures.eam.views.threatmatrix.wizard;

import org.conservationmeasures.eam.wizard.WizardPanel;


public class ThreatRatingWizardScopeStep extends ThreatRatingWizardSetValue
{
	public ThreatRatingWizardScopeStep(WizardPanel wizardToUse) throws Exception
	{
		super(wizardToUse, "Scope");
	}
	
	public ThreatRatingWizardScopeStep(WizardPanel wizardToUse, String critertion) throws Exception
	{
		super(wizardToUse, critertion);
	}
}
