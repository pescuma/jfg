package jfg.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SWTSimpleComponentFactory implements SWTComponentFactory
{
	public Text createText(Composite parent, int style)
	{
		return new Text(parent, SWT.BORDER | style);
	}
	
	public Button createCheckbox(Composite parent, int style)
	{
		return new Button(parent, SWT.CHECK | style);
	}
	
	public Combo createCombo(Composite parent, int style)
	{
		return new Combo(parent, style);
	}
	
}
