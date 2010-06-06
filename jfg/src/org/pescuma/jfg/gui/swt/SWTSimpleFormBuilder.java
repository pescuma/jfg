package org.pescuma.jfg.gui.swt;

import static org.eclipse.swt.layout.GridData.*;
import static org.pescuma.jfg.gui.swt.SWTHelper.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SWTSimpleFormBuilder implements SWTLayoutBuilder
{
	private JfgFormData data;
	private final List<Composite> currents = new ArrayList<Composite>();
	
	@Override
	public void init(Composite root, JfgFormData data)
	{
		if (currents.size() != 0)
			throw new IllegalStateException();
		
		this.data = data;
		
		GridLayout layout = (GridLayout) root.getLayout();
		
		if (layout == null)
			root.setLayout(createBorderlessGridLayout(2, false));
		
		else if (!(layout instanceof GridLayout))
			throw new IllegalArgumentException("JfgFormComposite needs a GridLayout");
		
		else
			layout.numColumns = 2;
		
		currents.add(root);
	}
	
	@Override
	public void finish()
	{
	}
	
	private Composite getCurrent()
	{
		return currents.get(currents.size() - 1);
	}
	
	public Composite[] getParentsForLabelWidget(String attributeName)
	{
		Composite current = getCurrent();
		return new Composite[] { current, current };
	}
	
	public void addLabelWidget(String attributeName, Label label, Control widget, boolean wantToFillVertical)
	{
		label.setLayoutData(new GridData(HORIZONTAL_ALIGN_END));
		widget.setLayoutData(new GridData(wantToFillVertical ? FILL_BOTH : FILL_HORIZONTAL));
	}
	
	public Composite getParentForWidget(String attributeName)
	{
		return createFullRowComposite();
	}
	
	public void addWidget(String attributeName, Control widget, boolean wantToFillVertical)
	{
		widget.setLayoutData(new GridData(wantToFillVertical ? FILL_BOTH : FILL_HORIZONTAL));
	}
	
	public void startGroup(String groupName)
	{
		Group frame = data.componentFactory.createGroup(createFullRowComposite(), SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.groupName(groupName));
		
		currents.add(frame);
	}
	
	public void endGroup(String groupName)
	{
		if (currents.size() < 2)
			throw new IllegalStateException();
		
		currents.remove(currents.size() - 1);
	}
	
	private Composite createFullRowComposite()
	{
		Composite composite = data.componentFactory.createComposite(getCurrent(), SWT.NONE);
		setupHorizontalComposite(composite, 2);
		return composite;
	}
}
