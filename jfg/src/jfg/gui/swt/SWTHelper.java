package jfg.gui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

class SWTHelper
{
	public static GridLayout createBorderlessGridLayout(int numColumns, boolean makeColumnsEqualWidth)
	{
		GridLayout layout = new GridLayout(numColumns, makeColumnsEqualWidth);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		return layout;
	}
	
	public static Composite createHorizontalComposite(Composite parent, int horizontalSpan)
	{
		Composite ret = new Composite(parent, SWT.NONE);
		
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = horizontalSpan;
		ret.setLayoutData(gd);
		
		ret.setLayout(createBorderlessGridLayout(1, true));
		
		return ret;
	}
}
