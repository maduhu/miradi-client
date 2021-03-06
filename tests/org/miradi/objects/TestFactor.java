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
package org.miradi.objects;

import java.util.Vector;

import org.miradi.ids.BaseId;
import org.miradi.ids.FactorId;
import org.miradi.ids.IdList;
import org.miradi.main.TestCaseWithProject;
import org.miradi.objecthelpers.ORefList;
import org.miradi.schemas.GoalSchema;
import org.miradi.schemas.IndicatorSchema;
import org.miradi.schemas.TargetSchema;

public class TestFactor extends TestCaseWithProject
{
	public TestFactor(String name)
	{
		super(name);
	}
	
	public void testComments() throws Exception
	{
		FactorId id = new FactorId(35);
		Cause factor = new Cause(getObjectManager(), id);
		assertEquals("started with a comment?", "", factor.getComment());
		String sampleComment = "yowza";
		factor.setComment(sampleComment);
		assertEquals("couldn't getComment?", sampleComment, factor.getComment());
	}
	
	public void testSetGetData() throws Exception
	{
		FactorId id = new FactorId(72);
		Target target = new Target(getObjectManager(), id);
		String[] tags = {
			Factor.TAG_COMMENTS,
			Factor.TAG_INDICATOR_IDS,
			AbstractTarget.TAG_GOAL_IDS,
		};

		Vector<BaseId> sampleIds = new Vector<BaseId>();
		sampleIds.add(new BaseId(12));
		sampleIds.add(new BaseId(275));
		
		String[] sampleData = {
			"Whatever comment",
			new IdList(IndicatorSchema.getObjectType(), sampleIds).toString(),
			new IdList(GoalSchema.getObjectType(), sampleIds).toString(),
		};
		
		for(int i = 0; i < tags.length; ++i)
		{
			target.setData(tags[i], sampleData[i]);
			assertEquals("didn't set/get for " + tags[i], sampleData[i], target.getData(tags[i]));
		}
	}

	public void testIsShared() throws Exception
	{
		Target target = getProject().createTarget();
		assertFalse("Target is shared?", target.mustBeDeletedBecauseParentIsGone());
		
		DiagramFactor diagramFactor = getProject().createDiagramFactorAndAddToDiagram(TargetSchema.getObjectType());
		assertTrue("Target is not shared?", diagramFactor.getWrappedFactor().mustBeDeletedBecauseParentIsGone());
		
		
	}
	
	public void testGetReferringTags() throws Exception
	{
		TaggedObjectSet taggedObjectSet1 = getProject().createTaggedObjectSet();
		TaggedObjectSet taggedObjectSet2 = getProject().createTaggedObjectSet();
		Cause cause = getProject().createCause();
		
		assertEquals("there are tagged objects?", 0, taggedObjectSet1.getTaggedObjectRefs().size());
		assertEquals(0, cause.findReferringTagRefs().size());
		getProject().setObjectData(taggedObjectSet1.getRef(), TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, new ORefList(cause.getRef()).toString());
		verifyTaggedObjectSet(taggedObjectSet1, cause, 1);
		ORefList referringTagRefs = cause.findReferringTagRefs();
		assertTrue("wrong tag ref?", referringTagRefs.contains(taggedObjectSet1.getRef()));
		
		getProject().setObjectData(taggedObjectSet2.getRef(), TaggedObjectSet.TAG_TAGGED_OBJECT_REFS, new ORefList(cause.getRef()).toString());		
		verifyTaggedObjectSet(taggedObjectSet2, cause, 2);
		ORefList referringTagRefs2 = cause.findReferringTagRefs();
		assertTrue("wrong tag ref?", referringTagRefs2.contains(taggedObjectSet2.getRef()));
	}

	private void verifyTaggedObjectSet(TaggedObjectSet taggedObjectSet, Cause cause, int taggedFactorCount ) throws Exception
	{
		assertEquals("cause was not tagged?", 1, taggedObjectSet.getTaggedObjectRefs().size());
		
		ORefList referringTagRefs = cause.findReferringTagRefs();
		assertEquals("wrong referring tag count?", taggedFactorCount, referringTagRefs.size());
	}
}
