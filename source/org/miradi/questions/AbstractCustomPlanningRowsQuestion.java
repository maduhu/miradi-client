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

package org.miradi.questions;

import java.util.Vector;

import javax.swing.Icon;

import org.miradi.icons.*;
import org.miradi.main.EAM;
import org.miradi.schemas.*;

abstract public class AbstractCustomPlanningRowsQuestion extends ProjectBasedDynamicQuestion
{
	@Override
	public ChoiceItem[] getChoices()
	{
		return getRowChoices().toArray(new ChoiceItem[0]);
	}

	protected Vector<ChoiceItem> getRowChoices()
	{	
		Vector<ChoiceItem> choiceItems = new Vector<ChoiceItem>();

		choiceItems.add(createChoiceItem(ConceptualModelDiagramSchema.getObjectType(), ConceptualModelDiagramSchema.OBJECT_NAME, new ConceptualModelIcon()));
		choiceItems.add(createChoiceItem(ResultsChainDiagramSchema.getObjectType(), ResultsChainDiagramSchema.OBJECT_NAME, new ResultsChainIcon()));
		choiceItems.add(createChoiceItem(TargetSchema.getObjectType(), TargetSchema.OBJECT_NAME, new TargetIcon()));
		
		if (shouldIncludeHumanWellbeingTargetRow())
			choiceItems.add(createChoiceItem(HumanWelfareTargetSchema.getObjectType(), HumanWelfareTargetSchema.OBJECT_NAME, new HumanWelfareTargetIcon()));
		
		choiceItems.add(createChoiceItem(SubTargetSchema.getObjectType(), SubTargetSchema.OBJECT_NAME, new SubTargetIcon()));
		choiceItems.add(createChoiceItem(GoalSchema.getObjectType(), GoalSchema.OBJECT_NAME, new GoalIcon()));
		choiceItems.add(createChoiceItem(ObjectiveSchema.getObjectType(), ObjectiveSchema.OBJECT_NAME, new ObjectiveIcon()));

        if (shouldIncludeBiophysicalFactorRow())
		    choiceItems.add(createChoiceItem(BiophysicalFactorSchema.getObjectType(), BiophysicalFactorSchema.OBJECT_NAME, new BiophysicalFactorIcon()));

		choiceItems.addAll(createCauseChoiceItems());

        if (shouldIncludeBiophysicalFactorRow())
		    choiceItems.add(createChoiceItem(BiophysicalResultSchema.getObjectType(), BiophysicalResultSchema.OBJECT_NAME, new BiophysicalResultIcon()));

		choiceItems.add(createChoiceItem(ThreatReductionResultSchema.getObjectType(), ThreatReductionResultSchema.OBJECT_NAME, new ThreatReductionResultIcon()));
		choiceItems.add(createChoiceItem(IntermediateResultSchema.getObjectType(), IntermediateResultSchema.OBJECT_NAME, new IntermediateResultIcon()));
		choiceItems.add(createChoiceItem(StrategySchema.getObjectType(), StrategySchema.OBJECT_NAME, IconManager.getStrategyIcon()));
		choiceItems.add(createChoiceItem(IndicatorSchema.getObjectType(), IndicatorSchema.OBJECT_NAME, IconManager.getIndicatorIcon()));
		choiceItems.addAll(createTaskChoiceItems());
		choiceItems.add(createChoiceItem(MeasurementSchema.getObjectType(), MeasurementSchema.OBJECT_NAME, new MeasurementIcon()));
		choiceItems.add(createChoiceItem(FutureStatusSchema.getObjectType(), FutureStatusSchema.OBJECT_NAME, new FutureStatusIcon()));
		
		return choiceItems;
	}

	protected static ChoiceItem createChoiceItem(int objectType, String objectName, Icon iconToUse)
	{
		return new ChoiceItem(objectName, EAM.fieldLabel(objectType, objectName), iconToUse);
	}
	
	abstract protected boolean shouldIncludeHumanWellbeingTargetRow();

	abstract protected boolean shouldIncludeBiophysicalFactorRow();

	abstract protected Vector<ChoiceItem> createCauseChoiceItems();
	
	abstract protected Vector<ChoiceItem> createTaskChoiceItems();
}
