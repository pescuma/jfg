/*
 * Copyright 2010 Ricardo Pescuma Domenecci
 * 
 * This file is part of jfg.
 * 
 * jfg is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * jfg is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with jfg. If not, see <http://www.gnu.org/licenses/>.
 */

package org.pescuma.jfg.gui.swt;

import static org.pescuma.jfg.gui.swt.SWTHelper.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.pescuma.jfg.Attribute;

import de.ikoffice.widgets.SplitButton;

public class SWTImageBuilder implements SWTWidgetBuilder
{
	@Override
	public boolean accept(Attribute attrib)
	{
		Object type = attrib.getType();
		return type == Image.class || "image".equals(type) || "webcam".equals(type);
	}
	
	@Override
	public SWTGuiWidget build(Attribute attrib, JfgFormData data)
	{
		return new WebcamSWTWidget(attrib, data);
	}
	
	private static class WebcamSWTWidget extends AbstractLabelWidgetSWTWidget
	{
		private WebcamControl webcam;
		private SplitButton buttom;
		private Image snapshot;
		private final Color[] colors = new Color[4];
		private Color background;
		private boolean marked = false;
		
		private final Listener showWebcamListener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				showWebcam();
			}
		};
		private final Listener takeSnapshotListener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				takeSnapshot();
			}
		};
		private final Listener setImageListener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				setImage();
			}
		};
		private final Listener clearListener = new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				clearImage();
			}
		};
		private Composite webcamBorder;
		
		public WebcamSWTWidget(Attribute attrib, JfgFormData data)
		{
			super(attrib, data);
		}
		
		@Override
		protected Control createWidget(Composite parent)
		{
			Composite composite = data.componentFactory.createComposite(parent, SWT.NONE);
			composite.setLayout(createBorderlessGridLayout(1, false));
			
			composite.addListener(SWT.Dispose, new Listener() {
				@Override
				public void handleEvent(Event event)
				{
					if (snapshot != null)
					{
						snapshot.dispose();
						snapshot = null;
					}
					
					cleanupButton();
				}
			});
			
			webcamBorder = data.componentFactory.createComposite(composite, SWT.NONE);
			webcamBorder.setLayoutData(new GridData(GridData.FILL_BOTH));
			GridLayout layout = new GridLayout(1, false);
			layout.marginHeight = 2;
			layout.marginWidth = 2;
			webcamBorder.setLayout(layout);
			
			colors[0] = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
			colors[1] = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
			webcamBorder.addPaintListener(new PaintListener() {
				@Override
				public void paintControl(PaintEvent e)
				{
					e.gc.setForeground(colors[marked ? 2 : 0]);
					e.gc.drawRectangle(0, 0, webcamBorder.getSize().x - 1, webcamBorder.getSize().y - 1);
					
					e.gc.setForeground(colors[marked ? 3 : 1]);
					e.gc.drawRectangle(1, 1, webcamBorder.getSize().x - 3, webcamBorder.getSize().y - 3);
				}
			});
			
			webcam = new WebcamControl(webcamBorder, SWT.NONE);
			webcam.setLayoutData(new GridData(GridData.FILL_BOTH));
			webcam.addListener(SWT.Dispose, getDisposeListener());
			//webcam.setCropImage(false);
			
			background = webcam.getBackground();
			
			if (attrib.canWrite())
			{
				buttom = new SplitButton(composite, SWT.NONE);
				buttom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				updateButton();
			}
			
			return composite;
		}
		
		@Override
		public Object getValue()
		{
			return snapshot;
		}
		
		@Override
		public void setValue(Object value)
		{
			replaceSnapshot((Image) value, true);
			
			updateButton();
		}
		
		@Override
		protected void markField()
		{
			super.markField();
			
			if (colors[2] == null)
				colors[2] = data.createBackgroundColor(webcam, colors[0]);
			if (colors[3] == null)
				colors[3] = data.createBackgroundColor(webcam, colors[1]);
			
			marked = true;
			
			webcam.setBackground(colors[3]);
			webcamBorder.redraw();
		}
		
		@Override
		protected void unmarkField()
		{
			super.unmarkField();
			
			marked = false;
			
			webcam.setBackground(background);
			webcamBorder.redraw();
		}
		
		@Override
		public void setEnabled(boolean enabled)
		{
			super.setEnabled(enabled);
			
			if (!enabled)
				webcam.stopWebcam();
			
			webcam.setEnabled(enabled);
			
			if (buttom != null)
				buttom.setEnabled(enabled);
		}
		
		private void updateButton()
		{
			if (buttom == null)
				return;
			
			buttom.setRedraw(false);
			
			cleanupButton();
			
			boolean hasWebcam = (webcam.getWebcamCount() > 0);
			Menu menu = buttom.getMenu();
			
			if (webcam.isShowingWebcam())
			{
				createTakeImage();
				createSetImage(menu);
				createClear(menu);
			}
			else if (snapshot != null)
			{
				if (hasWebcam)
				{
					createWebcam();
					createSetImage(menu);
				}
				else
				{
					createSetImage();
				}
				createClear(menu);
			}
			else
			{
				if (hasWebcam)
				{
					createWebcam();
					createSetImage(menu);
				}
				else
				{
					createSetImage();
				}
			}
			
			buttom.setRedraw(true);
		}
		
		private void cleanupButton()
		{
			{
				Image image = buttom.getImage();
				buttom.setImage(null);
				if (image != null)
					image.dispose();
			}
			
			Menu menu = buttom.getMenu();
			MenuItem[] items = menu.getItems();
			if (items != null)
			{
				for (MenuItem item : items)
				{
					Image image = item.getImage();
					item.setImage(null);
					if (image != null)
						image.dispose();
					
					item.dispose();
				}
			}
			
			buttom.removeListener(SWT.Selection, showWebcamListener);
			buttom.removeListener(SWT.Selection, takeSnapshotListener);
			buttom.removeListener(SWT.Selection, setImageListener);
			buttom.removeListener(SWT.Selection, clearListener);
		}
		
		private void createTakeImage()
		{
			buttom.setText(data.textTranslator.translate("Take picture"));
			buttom.setImage(new Image(buttom.getDisplay(), "icons/webcam.png"));
			buttom.addListener(SWT.Selection, takeSnapshotListener);
		}
		
		private void createWebcam()
		{
			buttom.setText(data.textTranslator.translate("Webcam"));
			buttom.setImage(new Image(buttom.getDisplay(), "icons/webcam.png"));
			buttom.addListener(SWT.Selection, showWebcamListener);
		}
		
		private void createSetImage()
		{
			buttom.setText(data.textTranslator.translate("Set image"));
			buttom.setImage(new Image(buttom.getDisplay(), "icons/picture.png"));
			buttom.addListener(SWT.Selection, setImageListener);
		}
		
		private void createClear(Menu menu)
		{
			MenuItem clear = new MenuItem(menu, SWT.PUSH);
			clear.setText(data.textTranslator.translate("Clear"));
			clear.addListener(SWT.Selection, clearListener);
		}
		
		private void createSetImage(Menu menu)
		{
			MenuItem setImage = new MenuItem(menu, SWT.PUSH);
			setImage.setText(data.textTranslator.translate("Set image"));
			setImage.setImage(new Image(buttom.getDisplay(), "icons/picture.png"));
			setImage.addListener(SWT.Selection, setImageListener);
		}
		
		private void showWebcam()
		{
			if (!webcam.showWebcam())
				return;
			
			updateButton();
		}
		
		private void takeSnapshot()
		{
			webcam.stopWebcam();
			
			if (webcam.isEmpty())
				webcam.showImage(snapshot);
			else
				replaceSnapshot(webcam.takeSnapshot(), false);
			
			updateButton();
		}
		
		private void setImage()
		{
			Image newImage = openImage();
			if (newImage == null)
				return;
			
			replaceSnapshot(newImage, false);
			
			updateButton();
		}
		
		private Image openImage()
		{
			FileDialog dialog = new FileDialog(webcam.getShell(), SWT.OPEN);
			dialog.setFilterNames(new String[] {
					data.textTranslator.translate("Image Files") + " (*.jpg;*.png;*.bmp;*.gif)",
					data.textTranslator.translate("All Files") + " (*.*)" });
			dialog.setFilterExtensions(new String[] { "*.jpg;*.png;*.bmp;*.gif", "*.*" });
			String filename = dialog.open();
			if (filename == null)
				return null;
			
			try
			{
				return new Image(webcam.getDisplay(), filename);
			}
			catch (SWTException e)
			{
				return null;
			}
		}
		
		private void clearImage()
		{
			replaceSnapshot(null, false);
			
			updateButton();
		}
		
		private void replaceSnapshot(Image newImage, boolean copyImage)
		{
			if (snapshot != null)
				snapshot.dispose();
			
			if (newImage != null && copyImage)
				snapshot = new Image(webcam.getDisplay(), newImage, SWT.IMAGE_COPY);
			else
				snapshot = newImage;
			
			webcam.showImage(snapshot);
			
			onWidgetModify();
		}
	};
}
