package org.pescuma.jfg.gui;

import java.util.List;

public interface ReferenceListGuiWidget extends GuiWidget
{
	interface DescriptionGetter
	{
		String getDescription(Object obj);
	}
	
	@SuppressWarnings("rawtypes")
	void setObjects(List objects, DescriptionGetter toDescription);
	
	public class Data
	{
		@SuppressWarnings("rawtypes")
		public List objects;
		public DescriptionGetter toDescription;
		
		@SuppressWarnings("rawtypes")
		public Data(List objects, DescriptionGetter toDescription)
		{
			this.objects = objects;
			this.toDescription = toDescription;
		}
	}
}
