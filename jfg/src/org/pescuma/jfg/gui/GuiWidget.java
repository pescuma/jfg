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
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui;

import java.util.Collection;

import org.pescuma.jfg.Attribute;

public interface GuiWidget
{
	Attribute getAttribute();
	
	void copyToGUI();
	void copyToModel();
	
	void setEnabled(boolean enabled);
	
	Object getValue();
	void setValue(Object value);
	
	void setValidators(WidgetValidator... validator);
	Collection<ValidationError> getValidationErrors();
	
	void setShadowText(String text);
	
	Collection<GuiWidget> getChildren();
	
	GuiWidget getChild(String attributeName);
	Collection<GuiWidget> getChildren(String attributeName);
	
	GuiWidget findChild(String attributeName);
	Collection<GuiWidget> findChildren(String attributeName);
	
	public class ValidationError
	{
		public Attribute attribute;
		public WidgetValidator validator;
	}
}
