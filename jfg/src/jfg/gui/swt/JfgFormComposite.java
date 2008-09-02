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
	
	/** Add all the attributes from the group, without adding the group itself */
	public void addContentsFrom(AttributeGroup group)
	{
		initLayout();
		buildAttributes(this, group, 0);
	}
	
	public void add(Attribute attrib)
	{
		initLayout();
		buildAttribute(this, attrib, 0);
	}
	
	public void add(AttributeGroup group)
	{
		initLayout();
		buildGroup(this, group, 0);
	}
	
	public void addText(Attribute text)
	{
		initLayout();
		addAttribute(this, new SWTTextBuilder(), text);
	}
	
	public void addNumer(Attribute number)
	{
		initLayout();
		addAttribute(this, new SWTNumberBuilder(), number);
	}
	
	public void addReal(Attribute real)
	{
		initLayout();
		addAttribute(this, new SWTRealBuilder(), real);
	}
	
	public void addCheckbox(Attribute bool)
	{
		initLayout();
		addAttribute(this, new SWTCheckboxBuilder(), bool);
	}
	
	public void addCombo(Attribute enumer)
	{
		initLayout();
		addAttribute(this, new SWTComboBuilder(), enumer);
	}
	
	public void addPassword(Attribute enumer)
	{
		initLayout();
		addAttribute(this, new SWTPasswordBuilder(), enumer);
	}
	
	public void addScale(Attribute enumer)
	{
		initLayout();
		addAttribute(this, new SWTScaleBuilder(), enumer);
	}
	
	public void addCustom(SWTWidgetBuilder builder, Attribute custom)
	{
		initLayout();
		addAttribute(this, builder, custom);
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
	
	private void buildAttributes(Composite parent, AttributeGroup group, int currentLevel)
	{
		for (Object attrib : group.getAttributes())
		{
			if (attrib instanceof AttributeGroup)
				buildGroup(parent, (AttributeGroup) attrib, currentLevel);
			else if (attrib instanceof Attribute)
				buildAttribute(parent, (Attribute) attrib, currentLevel);
		}
	}
	
	private void buildAttribute(Composite parent, Attribute attrib, int currentLevel)
	{
		SWTWidgetBuilder builder = getBuilderFor(attrib.getType());
		if (builder == null)
		{
			// TODO: Support groups when attribute is read/write
			if (!attrib.canWrite())
				buildGroup(parent, attrib.asGroup(), currentLevel + 1);
			return;
		}
		
		addAttribute(parent, builder, attrib);
	}
	
	private void addAttribute(Composite parent, SWTWidgetBuilder builder, Attribute attrib)
	{
		if (!builder.accept(attrib))
			throw new IllegalArgumentException("Wrong configuration");
		
		if (!data.showReadOnly && !attrib.canWrite())
			return;
		
		if (builder.wantNameLabel())
		{
			Label name = new Label(parent, SWT.NONE);
			name.setText(data.textTranslator.fieldName(attrib.getName()) + ":");
		}
		
		GridLayout layout = (GridLayout) getLayout();
		int numColumns = layout.numColumns;
		if (builder.wantNameLabel())
			numColumns--;
		
		SWTAttribute swta = builder.build((numColumns > 1 ? createHorizontalComposite(parent, numColumns) : parent), attrib, data);
		swta.init();
		attributes.add(swta);
	}
	
	private SWTWidgetBuilder getBuilderFor(Object type)
	{
		SWTWidgetBuilder builder = data.builders.get(type);
		if (builder != null)
			return builder;
		
		if (type instanceof Class)
		{
			Class<?> cls = (Class<?>) type;
			if (cls.isEnum())
				return data.builders.get(Enum.class);
		}
		
		return builder;
	}
	
	private void buildGroup(Composite parent, AttributeGroup group, int currentLevel)
	{
		if (group == null)
			return;
		if (currentLevel > data.maxGroupAttributeLevels)
			return;
		
		GridLayout layout = (GridLayout) getLayout();
		
		Group frame = data.componentFactory.createGroup(createHorizontalComposite(parent, layout.numColumns), SWT.NONE);
		frame.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		frame.setLayout(new GridLayout(2, false));
		frame.setText(data.textTranslator.groupName(group.getName()));
		
		buildAttributes(frame, group, currentLevel);
	}
}
