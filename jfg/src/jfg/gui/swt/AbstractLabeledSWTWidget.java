package jfg.gui.swt;

import static jfg.gui.swt.SWTHelper.*;
import jfg.Attribute;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

abstract class AbstractLabeledSWTWidget extends AbstractSWTWidget
{
	private Label name;
	
	public AbstractLabeledSWTWidget(Composite parent, Attribute attrib, JfgFormData data)
	{
		super(parent, attrib, data);
	}
	
	@Override
	protected Composite createComposite(Composite parent)
	{
		name = new Label(parent, SWT.NONE);
		name.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		name.setText(data.textTranslator.fieldName(attrib.getName()) + ":");
		
		Composite contentParent;
		GridLayout layout = (GridLayout) parent.getLayout();
		if (layout.numColumns < 2)
			throw new IllegalArgumentException();
		else if (layout.numColumns == 2)
			contentParent = parent;
		else
			contentParent = createHorizontalComposite(parent, layout.numColumns - 1);
		return contentParent;
	}
	
	@Override
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		
		name.setEnabled(enabled);
	}
}
