package jfg.gui.swt;

import static jfg.gui.swt.SWTHelper.*;

import java.util.ArrayList;
import java.util.Collection;

import jfg.Attribute;
import jfg.AttributeGroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class JfgFormComposite extends Composite
{
	private final JfgFormData data;
	private final Collection<SWTAttribute> attributes = new ArrayList<SWTAttribute>();
	
	public JfgFormComposite(Composite parent, int style, JfgFormData data)
	{
		super(parent, style);
		this.data = data;
	}
	
	public void setContents(AttributeGroup group)
	{
		initLayout();
		buildAttributes(group, null, 0);
	}
	
	@Override
	public void setLayout(Layout layout)
	{
		if (!(layout instanceof GridLayout))
			throw new IllegalArgumentException("JfgFormComposite needs a GridLayout");
		
		GridLayout gl = (GridLayout) layout;
		if (gl.numColumns < 2)
			throw new IllegalArgumentException("GridLayout need to have at least 2 columns");
		
		super.setLayout(layout);
	}
	
	private void initLayout()
	{
		if (getLayout() != null)
			return;
		
		setLayout(new GridLayout(2, false));
	}
	
	private String getFieldName(String parentName, String name)
	{
		if (parentName == null)
			return name;
		else
			return parentName + "." + name;
	}
	
	private void buildAttributes(AttributeGroup group, String parentName, int currentLevel)
	{
		String fieldName = getFieldName(parentName, group.getName());
		
		for (Object attrib : group.getAttributes())
		{
			if (attrib instanceof AttributeGroup)
				buildGroup(this, (AttributeGroup) attrib, fieldName, currentLevel);
			else if (attrib instanceof Attribute)
				buildAttribute(this, (Attribute) attrib, fieldName, currentLevel);
		}
	}
	
	private void buildAttribute(Composite parent, Attribute attrib, String parentName, int currentLevel)
	{
		SWTWidgetBuilder builder = data.builders.get(attrib.getType());
		if (builder == null)
		{
			if (currentLevel < data.maxGroupAttributeLevels)
			{
				AttributeGroup group = attrib.asGroup();
				if (group != null)
					buildGroup(parent, group, parentName, currentLevel + 1);
			}
			return;
		}
		
		if (!builder.acceptType(attrib.getType()))
			throw new IllegalArgumentException("Wrong configuration");
		
		int numColumns = 0;
		
		if (builder.wantNameLabel())
		{
			Label name = new Label(parent, SWT.NONE);
			name.setText(data.textTranslator.fieldName(getFieldName(parentName, attrib.getName())) + ":");
			
			numColumns++;
		}
		
		GridLayout layout = (GridLayout) getLayout();
		numColumns = layout.numColumns - numColumns;
		
		SWTAttribute swta = builder.build((numColumns > 1 ? createHorizontalComposite(parent, numColumns) : parent), attrib, data);
		swta.init();
		attributes.add(swta);
	}
	
	private void buildGroup(Composite parent, AttributeGroup group, String parentName, int currentLevel)
	{
		GridLayout layout = (GridLayout) getLayout();
		
		Group frame = new Group(createHorizontalComposite(parent, layout.numColumns), SWT.BORDER);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		
		buildAttributes(group, parentName, currentLevel);
	}
}
