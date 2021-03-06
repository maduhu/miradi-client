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

package org.miradi.xml.xmpz2.objectExporters;

import org.miradi.objects.BaseObject;
import org.miradi.objects.DiagramFactor;
import org.miradi.objects.Factor;
import org.miradi.questions.DiagramFactorBackgroundQuestion;
import org.miradi.questions.DiagramFactorFontColorQuestion;
import org.miradi.questions.DiagramFactorFontSizeQuestion;
import org.miradi.questions.DiagramFactorFontStyleQuestion;
import org.miradi.schemas.BaseObjectSchema;
import org.miradi.schemas.DiagramFactorSchema;
import org.miradi.utils.XmlUtilities2;
import org.miradi.xml.xmpz2.BaseObjectExporter;
import org.miradi.xml.xmpz2.Xmpz2XmlWriter;

public class DiagramFactorExporter extends BaseObjectExporter
{
	public DiagramFactorExporter(Xmpz2XmlWriter writerToUse)
	{
		super(writerToUse, DiagramFactorSchema.getObjectType());
	}
	
	@Override
	protected void writeFields(final BaseObject baseObject, final BaseObjectSchema baseObjectSchema) throws Exception
	{
		super.writeFields(baseObject, baseObjectSchema);
		
		DiagramFactor diagramFactor = (DiagramFactor) baseObject;
		writeWrappedFactorId(diagramFactor.getWrappedFactor());
		exportFontStylingElements(diagramFactor);
		getWriter().writeReflist(DIAGRAM_FACTOR + GROUP_BOX_CHILDREN_IDS, DIAGRAM_FACTOR, diagramFactor.getGroupBoxChildrenRefs());
	}
	
	@Override
	protected boolean doesFieldRequireSpecialHandling(String tag)
	{
		if (tag.equals(DiagramFactor.TAG_WRAPPED_REF))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_GROUP_BOX_CHILDREN_REFS))
			return true;
		
		if (isFontStyleField(tag))
			return true;
		
		return super.doesFieldRequireSpecialHandling(tag);
	}
	
	private boolean isFontStyleField(String tag)
	{
		if (tag.equals(DiagramFactor.TAG_FONT_SIZE))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_FONT_STYLE))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_BACKGROUND_COLOR))
			return true;
		
		if (tag.equals(DiagramFactor.TAG_FOREGROUND_COLOR))
			return true;

        if (tag.equals(DiagramFactor.TAG_HEADER_HEIGHT))
            return true;

        return false;
	}

	protected void writeWrappedFactorId(Factor wrappedFactor) throws Exception
	{
		getWriter().writeStartElement(DIAGRAM_FACTOR + WRAPPED_FACTOR_ID_ELEMENT_NAME);
		writeWrappedFactorIdElement(wrappedFactor);
		getWriter().writeEndElement(DIAGRAM_FACTOR + WRAPPED_FACTOR_ID_ELEMENT_NAME);
	}
	
	private void writeWrappedFactorIdElement(Factor wrappedFactor) throws Exception
	{
		getWriter().writeStartElement(WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME);
		
		String factorTypeName = getDiagramFactorWrappedFactorTypeName(wrappedFactor);
		getWriter().writeElement(factorTypeName, ID_ELEMENT_NAME, wrappedFactor.getFactorId().toString());
		
		getWriter().writeEndElement(WRAPPED_BY_DIAGRAM_FACTOR_ID_ELEMENT_NAME);
	}
	
	private void exportFontStylingElements(DiagramFactor diagramFactor) throws Exception
	{
		final String STYLING_ELEMENT_NAME = DIAGRAM_FACTOR + STYLE;
		getWriter().writeStartElement(STYLING_ELEMENT_NAME);
		
		getWriter().writeStartElement(STYLE);
		getWriter().writeNonOptionalCodeElement(DIAGRAM_FACTOR, DiagramFactor.TAG_FONT_SIZE, new DiagramFactorFontSizeQuestion(), diagramFactor.getFontSize());
		getWriter().writeNonOptionalCodeElement(DIAGRAM_FACTOR, DiagramFactor.TAG_FONT_STYLE, new DiagramFactorFontStyleQuestion(), XmlUtilities2.convertXmlTextToHtmlWithoutSurroundingHtmlTags(diagramFactor.getFontStyle()));
        if (diagramFactor.getHeaderHeight() != 0)
            getWriter().writeElement(DIAGRAM_FACTOR, DiagramFactor.TAG_HEADER_HEIGHT, diagramFactor.getHeaderHeight());
		getWriter().writeNonOptionalCodeElement(DIAGRAM_FACTOR, DiagramFactor.TAG_FOREGROUND_COLOR, new DiagramFactorFontColorQuestion(), diagramFactor.getFontColor());
		
		Factor wrappedFactor = diagramFactor.getWrappedFactor();
		if (wrappedFactor.isGroupBox() || wrappedFactor.isTextBox())
			getWriter().writeNonOptionalCodeElement(DIAGRAM_FACTOR, DiagramFactor.TAG_BACKGROUND_COLOR, new DiagramFactorBackgroundQuestion(), diagramFactor.getBackgroundColor());

		getWriter().writeEndElement(STYLE);
		
		getWriter().writeEndElement(STYLING_ELEMENT_NAME);
	}
}
