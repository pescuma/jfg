package org.pescuma.jfg.gui;

import java.util.List;

public interface ReferenceGuiWidget extends GuiWidget
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
		public Boolean canBeNull;
		
		@SuppressWarnings("rawtypes")
		public Data(List objects, DescriptionGetter toDescription)
		{
			this.objects = objects;
			this.toDescription = toDescription;
		}
		
		@SuppressWarnings("rawtypes")
		public Data(List objects)
		{
			this.objects = objects;
		}
		
		public Data setDescriptionGetter(DescriptionGetter toDescription)
		{
			this.toDescription = toDescription;
			return this;
		}
		
		public Data avoidNullEmpty()
		{
			this.canBeNull = false;
			return this;
		}
	}
}
