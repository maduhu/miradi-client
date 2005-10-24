/*
 * Copyright 2005, The Benetech Initiative
 * 
 * This file is confidential and proprietary
 */

/*
 * Portions of this file are based on work with this copyright:
 * Copyright (c) 2001-2004, Gaudenz Alder
 * All rights reserved. 
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * - Neither the name of JGraph nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */


package org.conservationmeasures.eam.diagram.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JLabel;

import org.conservationmeasures.eam.diagram.DiagramComponent;
import org.conservationmeasures.eam.diagram.nodes.DiagramNode;
import org.conservationmeasures.eam.diagram.nodes.Goals;
import org.conservationmeasures.eam.diagram.nodes.Indicator;
import org.conservationmeasures.eam.diagram.nodes.NodeAnnotations;
import org.conservationmeasures.eam.diagram.nodes.Objectives;
import org.conservationmeasures.eam.diagram.nodes.ThreatPriority;
import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;


public abstract class MultilineNodeRenderer extends MultilineCellRenderer implements CellViewRenderer
{
	abstract Dimension getInsetDimension();
	abstract public void fillShape(Graphics g, Rectangle rect, Color color);
	abstract public void drawBorder(Graphics2D g2, Rectangle rect, Color color);
	
	public Component getRendererComponent(JGraph graphToUse, CellView view,
			boolean sel, boolean focus, boolean previewMode)
	{
		super.getRendererComponent(graphToUse, view, sel, focus, previewMode);

		DiagramNode node = (DiagramNode)view.getCell();
		isVisible = ((DiagramComponent)graphToUse).isNodeVisible(node);
		if(node.canHavePriority())
			priority = node.getThreatPriority();
		else
			priority = null;
		indicator = node.getIndicator();
		objectives = node.getObjectives();
		goals = node.getGoals();
		return this;
	}
	
	public void paint(Graphics g1)
	{
		if(!isVisible)
			return;
		
		super.paint(g1);
		
		Rectangle rect = getNonBorderBounds();
		Graphics2D g2 = (Graphics2D) g1;

		drawIndicator(rect, g2);
		drawAnnotation(objectives, rect, g2);
		drawAnnotation(goals, rect, g2);
	}
	
	private void drawAnnotation(NodeAnnotations annotations, Rectangle rect, Graphics2D g2) 
	{
		if(annotations != null && annotations.hasAnnotation())
		{
			int xInset = getInsetDimension().width;

			RectangleRenderer annotationRenderer = new RectangleRenderer();
			Rectangle annotationsRectangle = new Rectangle();
			annotationsRectangle.x = rect.x + xInset + borderWidth;
			int annotationsHeight = annotations.size() * ANNOTATIONS_HEIGHT;
			annotationsRectangle.y = rect.y + (rect.height - annotationsHeight);
			annotationsRectangle.width = rect.width - (2 * xInset) - borderWidth;
			annotationsRectangle.height = annotationsHeight;
			setPaint(g2, annotationsRectangle, annotations.getColor());
			annotationRenderer.fillShape(g2, annotationsRectangle, annotations.getColor());
			
			//TODO allow multiple Objectives
			JLabel annotationsLabel = new JLabel(annotations.getAnnotation(0).toString());
			annotationsLabel.setSize(annotationsRectangle.getSize());
			annotationsLabel.setHorizontalAlignment(JLabel.CENTER);
			annotationsLabel.setVerticalAlignment(JLabel.CENTER);
			
			// The graphics2D object controls the location where the label 
			// will paint (the label's location will be ignored at this point)
			// Tell g2 where the new origin is, paint, and revert to the original origin
			g2.translate(annotationsRectangle.x, annotationsRectangle.y);
			annotationsLabel.paint(g2);
			g2.translate(-annotationsRectangle.x, -annotationsRectangle.y);
		}
	
	}
	
	private void drawIndicator(Rectangle rect, Graphics2D g2) 
	{
		if(indicator != null && indicator.hasIndicator())
		{
			TriangleRenderer indicatorRenderer = new TriangleRenderer();
			Rectangle smallTriangle = new Rectangle();
			smallTriangle.x = rect.x;
			smallTriangle.y = rect.y;
			smallTriangle.width = INDICATOR_WIDTH;
			smallTriangle.height = INDICATOR_HEIGHT;
			setPaint(g2, smallTriangle, indicator.getColor());
			indicatorRenderer.fillShape(g2, smallTriangle, indicator.getColor());

			JLabel indicatorLabel = new JLabel(indicator.toString());
			indicatorLabel.setSize(smallTriangle.getSize());
			indicatorLabel.setHorizontalAlignment(JLabel.CENTER);
			indicatorLabel.setVerticalAlignment(JLabel.CENTER);
			indicatorLabel.paint(g2);
		}
	}
	
	private static final int INDICATOR_WIDTH = 30;
	private static final int INDICATOR_HEIGHT = 30;
	private static final int ANNOTATIONS_HEIGHT = 30;
	
	boolean isVisible;
	ThreatPriority priority;
	Indicator indicator;
	Objectives objectives;
	Goals goals;
}
