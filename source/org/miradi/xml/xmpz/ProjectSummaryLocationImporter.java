/* 
Copyright 2005-2010, Foundations of Success, Bethesda, Maryland 
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

package org.miradi.xml.xmpz;

import org.miradi.objects.ProjectMetadata;
import org.miradi.xml.AbstractXmpzObjectImporter;
import org.miradi.xml.wcs.XmpzXmlConstants;
import org.w3c.dom.Node;

public class ProjectSummaryLocationImporter extends AbstractXmpzObjectImporter
{
	public ProjectSummaryLocationImporter(XmpzXmlImporter importerToUse)
	{
		super(importerToUse, XmpzXmlConstants.PROJECT_SUMMARY_LOCATION);
	}

	@Override
	public void importElement() throws Exception
	{
		Node projectSummaryLocationNode = getImporter().getNode(getImporter().getRootNode(), XmpzXmlConstants.PROJECT_SUMMARY_LOCATION);
		
		importGeospatialLocationField(projectSummaryLocationNode);		
		importCodeListField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_COUNTRIES);
		importField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_STATE_AND_PROVINCES);
		importField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_MUNICIPALITIES);
		importField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_LEGISLATIVE_DISTRICTS);
		importField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_LOCATION_DETAIL);
		importField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_SITE_MAP_REFERENCE);
		importField(projectSummaryLocationNode, getMetadataRef(), ProjectMetadata.TAG_LOCATION_COMMENTS);
	}
	
	private void importGeospatialLocationField(Node projectSummaryLocationNode) throws Exception
	{
		Node locationNode = getImporter().getNode(projectSummaryLocationNode, XmpzXmlConstants.PROJECT_SUMMARY_LOCATION + XmpzXmlConstants.PROJECT_LOCATION);
		if(locationNode == null)
			return;
		
		Node gespatialLocationNode = getImporter().getNode(locationNode, XmpzXmlConstants.GEOSPATIAL_LOCATION);
		if(gespatialLocationNode == null)
			return;
		
		importField(gespatialLocationNode, XmpzXmlConstants.LATITUDE, getMetadataRef(), ProjectMetadata.TAG_PROJECT_LATITUDE);
		importField(gespatialLocationNode, XmpzXmlConstants.LONGITUDE, getMetadataRef(), ProjectMetadata.TAG_PROJECT_LONGITUDE);
	}
}
