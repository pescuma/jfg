package org.pescuma.jfg.gui;

public interface ObjectListGuiWidget extends GuiWidget
{
	void removeAllObjects();
	
	void addObject(Object obj);
	
	public static class Data
	{
		public boolean collapse = false;
		
		public Data allowToCollapse()
		{
			collapse = true;
			return this;
		}
		
		public Data dontAllowToCollapse()
		{
			collapse = false;
			return this;
		}
	}
}
