package jfg.gui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public interface SWTComponentFactory
{
	Text createText(Composite parent, int style);
}
