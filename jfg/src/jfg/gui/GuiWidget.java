package jfg.gui;


public interface GuiWidget
{
	void init(GuiCopyManager manager);
	
	void copyToGUI();
	
	void copyToModel();
	
//	Attribute getAttribute();
	
	void setEnabled(boolean enabled);
	
	void setVisible(boolean visible);
	
	Object getValue();
	
	void setValue(Object value);
}
