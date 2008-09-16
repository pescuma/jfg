package jfg.gui.swt;

import static jfg.gui.swt.SWTHelper.*;
import jfg.Attribute;
import jfg.AttributeGroup;
import jfg.gui.GuiCopyManager;
import jfg.gui.GuiUpdateListener;
import jfg.gui.GuiWidget;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;

public class JfgFormComposite extends Composite
{
	private final JfgFormData data;
	private final BaseWidgetList widgets = new BaseWidgetList();
	private final GuiCopyManager copyManager;
	private final BaseGuiListenerManager listenerManager = new BaseGuiListenerManager();
	private boolean initializing = true;
	
	public JfgFormComposite(Composite parent, int style, JfgFormData data)
	{
		super(parent, style);
		this.data = data;
		
		switch (data.guiUpdateStrategy)
		{
			case Never:
				copyManager = new DontUpdateSWTCopyManager(this, data);
				break;
			case UpdateOnGuiChange:
				copyManager = new FastSWTCopyManager(this, data);
				break;
			case BufferUpdatesForTimeout:
				if (data.guiUpdateTimeout <= 0)
					throw new IllegalArgumentException();
				copyManager = new IndependentFixedTimeSWTCopyManager(this, data);
				break;
			case UpdateAfterFieldStoppedChanging:
				if (data.guiUpdateTimeout <= 0)
					throw new IllegalArgumentException();
				copyManager = new IndependentSWTCopyManager(this, data);
				break;
			case UpdateAfterAllFieldsStoppedChanging:
				if (data.guiUpdateTimeout <= 0)
					throw new IllegalArgumentException();
				copyManager = new BatchSWTCopyManager(this, data);
				break;
			default:
				throw new IllegalArgumentException();
		}
		
		parent.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event)
			{
				if (initializing)
					finishInitialize();
			}
		});
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
		SWTWidgetBuilder builder = getBuilderFor(attrib);
		if (builder == null)
		{
			// TODO Support groups when attribute is read/write
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
		
		for (SWTAttributeFilter filter : data.attributeFilters)
			if (filter.hideAttribute(attrib))
				return;
		
		GuiWidget widget = builder.build(parent, attrib, data);
		widgets.add(attrib, widget);
	}
	
	private void finishInitialize()
	{
		for (AttributeWidgetPair aw : widgets)
		{
			aw.widget.init(copyManager);
			aw.widget.copyToGUI();
		}
		
		for (AttributeWidgetPair aw : widgets)
			listenerManager.notifyChange(aw.attrib.getName(), aw.widget, widgets);
		
		initializing = false;
	}
	
	private SWTWidgetBuilder getBuilderFor(Attribute attrib)
	{
		return data.builders.get(getTypeOf(attrib));
	}
	
	private Object getTypeOf(Attribute attrib)
	{
		Object type = null;
		for (SWTBuilderTypeSelector selector : data.builderTypeSelectors)
		{
			type = selector.getTypeFor(attrib);
			if (type != null)
				return type;
		}
		return attrib.getType();
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
	
	public void copyToGUI()
	{
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToGUI();
	}
	
	public void copyToModel()
	{
		for (AttributeWidgetPair aw : widgets)
			aw.widget.copyToModel();
	}
	
	void onGuiUpdated(GuiWidget widget)
	{
		if (initializing)
			return;
		
		listenerManager.notifyChange(widgets.getAttribute(widget).getName(), widget, widgets);
	}
	
	public void addGuiUpdateListener(GuiUpdateListener listener)
	{
		listenerManager.addListener(null, listener);
	}
	
	public void addGuiUpdateListener(Attribute attribute, GuiUpdateListener listener)
	{
		addGuiUpdateListener(attribute.getName(), listener);
	}
	
	public void addGuiUpdateListener(String attributeName, GuiUpdateListener listener)
	{
		listenerManager.addListener(attributeName, listener);
	}
	
	public void removeGuiUpdateListener(GuiUpdateListener listener)
	{
		listenerManager.removeListener(null, listener);
	}
	
	public void removeGuiUpdateListener(Attribute attribute, GuiUpdateListener listener)
	{
		removeGuiUpdateListener(attribute.getName(), listener);
	}
	
	public void removeGuiUpdateListener(String attributeName, GuiUpdateListener listener)
	{
		listenerManager.removeListener(attributeName, listener);
	}
	
}
