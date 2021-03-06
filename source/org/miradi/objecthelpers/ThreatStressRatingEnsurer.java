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
package org.miradi.objecthelpers;

import java.util.HashSet;

import org.miradi.commands.CommandCreateObject;
import org.miradi.diagram.ThreatTargetChainWalker;
import org.miradi.main.CommandExecutedEvent;
import org.miradi.main.CommandExecutedListener;
import org.miradi.main.EAM;
import org.miradi.objects.Cause;
import org.miradi.objects.ConceptualModelDiagram;
import org.miradi.objects.ResultsChainDiagram;
import org.miradi.objects.Target;
import org.miradi.objects.ThreatStressRating;
import org.miradi.project.Project;
import org.miradi.schemas.CauseSchema;
import org.miradi.schemas.ConceptualModelDiagramSchema;
import org.miradi.schemas.ResultsChainDiagramSchema;
import org.miradi.schemas.TargetSchema;
import org.miradi.schemas.ThreatStressRatingSchema;
import org.miradi.utils.CommandVector;

public class ThreatStressRatingEnsurer implements CommandExecutedListener 
{
	public ThreatStressRatingEnsurer(Project projectToUse)
	{
		project = projectToUse;
	}
	
	public void disable()
	{
		getProject().removeCommandExecutedListener(this);
	}

	public void enable()
	{
		getProject().addCommandExecutedListener(this);
	}
	
	public void createOrDeleteThreatStressRatingsAsNeeded() throws Exception
	{
		HashSet<ThreatStressPair> threatStressPairs1 = new HashSet<ThreatStressPair>();
		ThreatTargetChainWalker chainObject = new ThreatTargetChainWalker(getProject());
		ORefList allTargetRefs = getProject().getTargetPool().getRefList();
		for (int index = 0; index < allTargetRefs.size(); ++index)
		{
			Target target = Target.find(getProject(), allTargetRefs.get(index));
			ORefSet upstreamThreatRefs = chainObject.getUpstreamThreatRefsFromTarget(target);
			ORefSet stressRefs = new ORefSet(target.getStressRefs());
			HashSet<ThreatStressPair> thisThreatStressPairs = createThreatStressPairs(upstreamThreatRefs, stressRefs);
			threatStressPairs1.addAll(thisThreatStressPairs);			
		}
		HashSet<ThreatStressPair> threatStressPairs = threatStressPairs1;
		
		createOrDeleteThreatStressRatingsAsNeeded(threatStressPairs);
	}

	private void createOrDeleteThreatStressRatingsAsNeeded(HashSet<ThreatStressPair> desiredThreatStressPairs) throws Exception
	{
		HashSet<ThreatStressPair> existingThreatStressPairs = createThreatStressFromPoolPairs();
		createThreatStressRatings(desiredThreatStressPairs, existingThreatStressPairs);
		deleteThreatStressRatings(desiredThreatStressPairs, existingThreatStressPairs);
	}

	private void createThreatStressRatings(HashSet<ThreatStressPair> desiredThreatStressPairs, HashSet<ThreatStressPair> existingThreatStressPairs) throws Exception
	{
		HashSet<ThreatStressPair> toCreate = new HashSet<ThreatStressPair>(desiredThreatStressPairs);
		toCreate.removeAll(existingThreatStressPairs);
		for(ThreatStressPair threatStressPair : toCreate)
		{
			ORef stressRef = threatStressPair.getStressRef();
			ORef threatRef = threatStressPair.getThreatRef();
			CommandCreateObject createThreatStressRatingCommand = new CommandCreateObject(ThreatStressRatingSchema.getObjectType());
			getProject().executeAsSideEffect(createThreatStressRatingCommand);
			getProject().setObjectData(createThreatStressRatingCommand.getObjectRef(), ThreatStressRating.TAG_STRESS_REF, stressRef.toString());
			getProject().setObjectData(createThreatStressRatingCommand.getObjectRef(), ThreatStressRating.TAG_THREAT_REF, threatRef.toString());
		}
	}
	
	private void deleteThreatStressRatings(HashSet<ThreatStressPair> desiredThreatStressPairs, HashSet<ThreatStressPair> existingThreatStressPairs) throws Exception
	{
		HashSet<ThreatStressPair> toDelete = new HashSet<ThreatStressPair>(existingThreatStressPairs);
		toDelete.removeAll(desiredThreatStressPairs);
		for(ThreatStressPair threatStressPair : toDelete)
		{		
			ORef threatStressRatingToDelete = threatStressPair.findMatchingThreatStressRating();
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingToDelete);
			deleteThreatStressRating(threatStressRating);
		}
	}
	
	private void deleteThreatStressRating(ThreatStressRating threatStressRating) throws Exception
	{
		CommandVector commandsToDeleteThreatStressRating = new CommandVector();
		commandsToDeleteThreatStressRating.addAll(threatStressRating.createCommandsToDeleteChildrenAndObject());
		
		getProject().executeAsSideEffect(commandsToDeleteThreatStressRating);
	}

	private HashSet<ThreatStressPair> createThreatStressFromPoolPairs()
	{
		ORefSet allThreatStressRatingRefs = getProject().getThreatStressRatingPool().getRefSet();
		HashSet<ThreatStressPair> threatStressPairs = new HashSet<ThreatStressPair>();
		for(ORef threatStressRatingRef : allThreatStressRatingRefs)
		{
			ThreatStressRating threatStressRating = ThreatStressRating.find(getProject(), threatStressRatingRef);
			if (threatStressRating.getStressRef().isInvalid() || threatStressRating.getThreatRef().isInvalid())
			{
				EAM.logError("ThreatStressRating: " + threatStressRatingRef + " has an invalid stress or threat ref");
				continue;
			}
			
			threatStressPairs.add(new ThreatStressPair(getProject(), threatStressRating));
		}
		
		return threatStressPairs;
	}
	
	private HashSet<ThreatStressPair> createThreatStressPairs(ORefSet threatRefs, ORefSet stressRefs)
	{
		HashSet<ThreatStressPair> threatStressPairs = new HashSet<ThreatStressPair>();
		for(ORef threatRef : threatRefs)
		{
			for(ORef stressRef : stressRefs)
			{
				threatStressPairs.add(new ThreatStressPair(getProject(), threatRef, stressRef));
			}
		}
		
		return threatStressPairs;
	}

	public void commandExecuted(CommandExecutedEvent event)
	{
		try
		{
			if (isThreatStressRatingAffectingCommand(event))
				createOrDeleteThreatStressRatingsAsNeeded();
		}
		catch (Exception e)
		{
			EAM.logException(e);
			throw new RuntimeException(e);
		}
	}

	private boolean isThreatStressRatingAffectingCommand(CommandExecutedEvent event)
	{
		if (event.isSetDataCommandWithThisTypeAndTag(ResultsChainDiagramSchema.getObjectType(), ResultsChainDiagram.TAG_DIAGRAM_FACTOR_LINK_IDS))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(ConceptualModelDiagramSchema.getObjectType(), ConceptualModelDiagram.TAG_DIAGRAM_FACTOR_LINK_IDS))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(TargetSchema.getObjectType(), Target.TAG_STRESS_REFS))
			return true;
		
		if (event.isSetDataCommandWithThisTypeAndTag(CauseSchema.getObjectType(), Cause.TAG_IS_DIRECT_THREAT))
			return true;
				
		return false;
	}
	
	private Project getProject()
	{
		return project;
	}
	
	private Project project;
}
	