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

import java.util.Comparator;

import org.miradi.ids.FactorId;
import org.miradi.project.threatrating.ThreatRatingBundle;

public class ThreatRatingBundleSorter implements Comparator<ThreatRatingBundle>
{
	public int compare(ThreatRatingBundle o1, ThreatRatingBundle o2)
	{
		final FactorId threatId2 = o2.getThreatId();
		final FactorId threatId1 = o1.getThreatId();
		final FactorId targetId1 = o1.getTargetId();
		final FactorId targetId2 = o2.getTargetId();
		if (targetId1.equals(targetId2) && threatId1.equals(threatId2))
			return 0;

		if (targetId1.equals(targetId2) && !threatId1.equals(threatId2))
			return threatId1.compareTo(threatId2);
		
		return targetId1.compareTo(targetId2);
	}
}
