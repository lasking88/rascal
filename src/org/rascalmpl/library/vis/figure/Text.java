/*******************************************************************************
 * Copyright (c) 2009-2011 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:

 *   * Paul Klint - Paul.Klint@cwi.nl - CWI
*******************************************************************************/
package org.rascalmpl.library.vis.figure;

import static org.rascalmpl.library.vis.properties.Properties.*;

import java.util.List;

import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import org.rascalmpl.library.vis.graphics.GraphicsContext;
import org.rascalmpl.library.vis.properties.PropertyManager;
import org.rascalmpl.library.vis.properties.PropertyValue;
import org.rascalmpl.library.vis.swt.applet.IHasSWTElement;
import org.rascalmpl.library.vis.util.Util;
import org.rascalmpl.library.vis.util.vector.Rectangle;

/**
 * Text element.
 * 
 * @author paulk
 *
 */
public class Text extends Figure {
	private static final int TAB_WIDTH = 4;
	private static boolean debug = false;
	private String [] lines;
	private double[] indents;
	private PropertyValue<String> txt;


	public Text(PropertyManager properties,PropertyValue<String> txt) {
		super( properties);
		this.txt = txt;
		children = childless;
	}

	@Override
	public void computeMinSize() {
		lines = txt.getValue().split("\n");
		indents = new double[lines.length];
		double width = 0;
		for(int i = 0 ; i < lines.length ; i++){
			//lines[i] = Util.tabs2spaces(TAB_WIDTH,lines[i]);
			indents[i] = getTextWidth(lines[i]);
			width = Math.max(width,indents[i]);
		}
		double innerAlign = prop.getReal(INNER_ALIGN);
		for(int i = 0 ; i < indents.length ; i++){
			indents[i] = (width - indents[i]) * innerAlign;
		}
		double height = lines.length * getTextHeight();
		minSize.set(width,height);
		resizable.set(false,false);
	}

	@Override
	public void resizeElement(Rectangle view) {}
	
	@Override
	public void drawElement(GraphicsContext gc, List<IHasSWTElement> visibleSWTElements){
		double y = location.getY();
		for(int i = 0 ; i < lines.length ; i++){
			gc.text(lines[i], location.getX() + indents[i],y);
			y+= getTextHeight();
		}
	}
	
	@Override
	public
	String toString(){
		return String.format("text %s %s %s", txt.getValue(), location, minSize);
	}
}
