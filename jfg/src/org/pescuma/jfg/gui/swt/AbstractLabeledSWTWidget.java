/*
 * Copyright 2008 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with Foobar. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import static org.pescuma.jfg.gui.swt.SWTHelper.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.pescuma.jfg.Attribute;

abstract class AbstractLabeledSWTWidget extends AbstractSWTWidget
{
	private Label name;
	
	public AbstractLabeledSWTWidget(Composite parent, Attribute attrib, JfgFormData data)
	{
		super(parent, attrib, data);
	}
	
	@Override
	protected Composite createComposite(Composite parent)
	{
		name = data.componentFactory.createLabel(parent, SWT.NONE);
		name.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		name.setText(data.textTranslator.fieldName(attrib.getName()) + ":");
		
		Composite contentParent;
		GridLayout layout = (GridLayout) parent.getLayout();
		if (layout.numColumns < 2)
			throw new IllegalArgumentException();
		else if (layout.numColumns == 2)
			contentParent = parent;
		else
			contentParent = setupHorizontalComposite(data.componentFactory.createComposite(parent, SWT.NONE), layout.numColumns - 1);
		return contentParent;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		name.setEnabled(enabled);
	}
}
