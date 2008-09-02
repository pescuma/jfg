package jfg.gui.swt;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;

public interface SWTComponentFactory
{
	Text createText(Composite parent, int style);
	Button createCheckbox(Composite parent, int style);
	Combo createCombo(Composite parent, int style);
	Group createGroup(Composite parent, int style);
	Scale createScale(Composite parent, int style);
}
