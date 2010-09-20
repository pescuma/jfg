package org.pescuma.jfg.gui;

import java.util.List;

public interface ReferenceListGuiWidget extends GuiWidget
{
	interface DescriptionGetter
	{
		String getDescription(Object obj);
	}
	
	@SuppressWarnings("unchecked")
	void setObjects(List objects, DescriptionGetter toDescription);
	
	public class Data
	{
		@SuppressWarnings("unchecked")
		public List objects;
		public DescriptionGetter toDescription;
		
		@SuppressWarnings("unchecked")
		public Data(List objects, DescriptionGetter toDescription)
		{
			this.objects = objects;
			this.toDescription = toDescription;
		}
	}
}
