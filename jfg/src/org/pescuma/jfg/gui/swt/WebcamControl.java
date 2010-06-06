package org.pescuma.jfg.gui.swt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.VideoFormat;

public class WebcamControl extends Canvas
{
	private static final LTIManager lti = new LTIManager();
	
	private Image image;
	
	private boolean ltiInitialized;
	private int webcamIndex = -1;
	private com.lti.civil.Image pendingImage;
	private boolean cropImage = true;
	
	private CaptureObserver captureObserver = new CaptureObserver() {
		@Override
		public void onNewImage(CaptureStream sender, com.lti.civil.Image image)
		{
			setPendingImage(image);
		}
		
		@Override
		public void onError(CaptureStream sender, CaptureException e)
		{
		}
	};
	
	public WebcamControl(Composite parent, int style)
	{
		super(parent, style | SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND);
		
		ltiInitialized = lti.init(getShell());
		
		addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e)
			{
				paint(e);
			}
		});
		
		addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event)
			{
				internalDispose();
			}
		});
	}
	
	public boolean isCropImage()
	{
		return cropImage;
	}
	
	public void setCropImage(boolean crop)
	{
		this.cropImage = crop;
	}
	
	private List<CaptureDeviceInfo> getDevices()
	{
		return lti.getDevices();
	}
	
	public int getWebcamCount()
	{
		return getDevices().size();
	}
	
	public String getWebcamName(int index)
	{
		List<CaptureDeviceInfo> devices = getDevices();
		
		if (index < 0 || index >= devices.size())
			return null;
		
		return devices.get(index).getDescription();
	}
	
	public boolean isShowingWebcam()
	{
		return webcamIndex >= 0;
	}
	
	/**
	 * Start showing the first webcam
	 */
	public boolean showWebcam()
	{
		return showWebcam(0);
	}
	
	/**
	 * Start showing the webcam at selected index
	 */
	public boolean showWebcam(int index)
	{
		if (!ltiInitialized)
			return false;
		
		if (index == webcamIndex)
			return true;
		
		stopWebcam();
		
		List<CaptureDeviceInfo> devices = getDevices();
		if (index < 0 || index >= devices.size())
			return false;
		
		if (lti.startCapture(getShell(), index, captureObserver))
		{
			webcamIndex = index;
			invalidate();
			return true;
		}
		else
		{
			webcamIndex = -1;
			return false;
		}
	}
	
	/**
	 * Stop showing the current webcam. The last image available will still be
	 * shown. To remove it call <code>showImage(null)</code>
	 */
	public void stopWebcam()
	{
		processPendingImage();
		
		if (webcamIndex != -1)
			lti.stopCapture(webcamIndex, captureObserver);
		
		pendingImage = null;
		webcamIndex = -1;
		
		invalidate();
	}
	
	private void disposeImage()
	{
		if (image != null)
		{
			image.dispose();
			image = null;
		}
	}
	
	public Image takeSnapshot()
	{
		processPendingImage();
		
		return cloneImage(image);
	}
	
	public boolean isEmpty()
	{
		processPendingImage();
		
		return image == null;
	}
	
	/**
	 * Set a static image to be show. Use null to show no image. A copy of the
	 * image is taken and no pointer is stored to the passed image.
	 */
	public void showImage(Image newImage)
	{
		stopWebcam();
		disposeImage();
		image = cloneImage(newImage);
		invalidate();
	}
	
	private Image cloneImage(Image img)
	{
		if (img == null)
			return null;
		
		return new Image(getDisplay(), img, SWT.IMAGE_COPY);
	}
	
	private void paint(PaintEvent e)
	{
		processPendingImage();
		
		drawBackground(e.gc, e.x, e.y, e.width, e.height);
		
		if (image != null)
		{
			Rectangle imageBounds = image.getBounds();
			Point size = getSize();
			
			if (cropImage)
			{
				double scaleX = imageBounds.width / (double) size.x;
				double scaleY = imageBounds.height / (double) size.y;
				double scale = Math.min(scaleX, scaleY);
				
				Rectangle newImageBounds = new Rectangle(0, 0, (int) (size.x * scale), (int) (size.y * scale));
				
				int widthDif = imageBounds.width - newImageBounds.width;
				if (widthDif > 0)
					newImageBounds.x += widthDif / 2;
				
				int heightDif = imageBounds.height - newImageBounds.height;
				if (heightDif > 0)
					newImageBounds.y += heightDif / 2;
				
				e.gc.drawImage(image, newImageBounds.x, newImageBounds.y, newImageBounds.width, newImageBounds.height,
						0, 0, size.x, size.y);
			}
			else
			{
				double scaleX = (double) size.x / imageBounds.width;
				double scaleY = (double) size.y / imageBounds.height;
				double scale = Math.min(scaleX, scaleY);
				
				Rectangle newFrameBounds = new Rectangle(0, 0, (int) (imageBounds.width * scale),
						(int) (imageBounds.height * scale));
				
				int widthDif = size.x - newFrameBounds.width;
				if (widthDif > 0)
				{
					newFrameBounds.x += widthDif / 2;
					drawBackground(e.gc, 0, 0, newFrameBounds.x, size.y);
					drawBackground(e.gc, newFrameBounds.x + newFrameBounds.width, 0, size.x, size.y);
				}
				
				int heightDif = size.y - newFrameBounds.height;
				if (heightDif > 0)
				{
					newFrameBounds.y += heightDif / 2;
					drawBackground(e.gc, 0, 0, size.x, newFrameBounds.y);
					drawBackground(e.gc, 0, newFrameBounds.y + newFrameBounds.height, size.x, size.y);
				}
				
				e.gc.drawImage(image, 0, 0, imageBounds.width, imageBounds.height, newFrameBounds.x, newFrameBounds.y,
						newFrameBounds.width, newFrameBounds.height);
			}
		}
	}
	
	@Override
	public void dispose()
	{
		internalDispose();
		super.dispose();
	}
	
	private void internalDispose()
	{
		stopWebcam();
		lti.dispose();
		disposeImage();
	}
	
	private synchronized void setPendingImage(com.lti.civil.Image image)
	{
		pendingImage = image;
		invalidate();
	}
	
	private void invalidate()
	{
		try
		{
			getDisplay().asyncExec(new Runnable() {
				@Override
				public void run()
				{
					if (isDisposed())
						return;
					redraw();
				}
			});
		}
		catch (SWTException e)
		{
		}
	}
	
	private static int getBitsPerPixel(int format)
	{
		switch (format)
		{
			case com.lti.civil.VideoFormat.RGB24:
				return 24;
			case com.lti.civil.VideoFormat.RGB32:
				return 32;
			default:
				throw new RuntimeException();
		}
	}
	
	private static ImageData convertToSWTImageData(final com.lti.civil.Image image)
	{
		final VideoFormat format = image.getFormat();
		PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
		return new ImageData(format.getWidth(), format.getHeight(), getBitsPerPixel(format.getFormatType()), palette,
				1, image.getBytes());
	}
	
	private synchronized void processPendingImage()
	{
		if (pendingImage == null)
			return;
		
		try
		{
			Image processed = new Image(getDisplay(), convertToSWTImageData(pendingImage));
			disposeImage();
			image = processed;
		}
		finally
		{
			pendingImage = null;
		}
	}
	
	static class LTIManager
	{
		private static final boolean hasLTI = hasLTI();
		
		private static boolean hasLTI()
		{
			try
			{
				return Class.forName("com.lti.civil.CaptureSystem") != null;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		
		private long clients = 0;
		private CaptureSystem system;
		
		private static class WebcamData
		{
			long clients = 0;
			CaptureStream captureStream;
			final List<CaptureObserver> observers = new ArrayList<CaptureObserver>();
		}
		
		private Map<Integer, WebcamData> webcams = new HashMap<Integer, WebcamData>();
		
		public boolean init(Shell shell)
		{
			if (!hasLTI)
				return false;
			
			clients++;
			
			if (system != null)
				return true;
			
			try
			{
				system = DefaultCaptureSystemFactorySingleton.instance().createCaptureSystem();
				system.init();
				
				// List<CaptureDeviceInfo> devices = getDevices();
				// for (int i = 0; i < devices.size(); ++i)
				// {
				// CaptureDeviceInfo info = devices.get(i);
				// System.out.println("Device ID " + i + ": " +
				// info.getDeviceID());
				// System.out.println("Description " + i + ": " +
				// info.getDescription());
				// }
				
				return true;
			}
			catch (CaptureException e)
			{
				e.printStackTrace();
				
				dispose();
				
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				mb.setText(shell.getText());
				mb.setMessage("Erro inicializando a webcam: " + e.getMessage());
				mb.open();
				
				return false;
			}
		}
		
		public boolean startCapture(Shell shell, int index, CaptureObserver captureObserver)
		{
			if (system == null)
				return false;
			
			List<CaptureDeviceInfo> devices = getDevices();
			if (index < 0 || index >= devices.size())
				return false;
			
			{
				WebcamData data = webcams.get(index);
				if (data != null)
				{
					data.clients++;
					data.observers.add(captureObserver);
					return true;
				}
			}
			
			final WebcamData data = new WebcamData();
			data.clients++;
			data.observers.add(captureObserver);
			
			try
			{
				CaptureDeviceInfo info = devices.get(index);
				data.captureStream = system.openCaptureDeviceStream(info.getDeviceID());
				data.captureStream.setObserver(new CaptureObserver() {
					@Override
					public void onNewImage(CaptureStream sender, com.lti.civil.Image image)
					{
						for (CaptureObserver observer : data.observers)
							observer.onNewImage(sender, image);
					}
					
					@Override
					public void onError(CaptureStream sender, CaptureException e)
					{
						System.err.println("Error loading image from " + sender);
						e.printStackTrace();
						
						for (CaptureObserver observer : data.observers)
							observer.onError(sender, e);
					}
				});
				
				data.captureStream.start();
				
				webcams.put(index, data);
				
				return true;
			}
			catch (CaptureException e)
			{
				e.printStackTrace();
				
				stop(data.captureStream);
				
				MessageBox mb = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
				mb.setText(shell.getText());
				mb.setMessage("Erro inicializando a webcam: " + e.getMessage());
				mb.open();
				
				return false;
			}
		}
		
		public void stopCapture(int index, CaptureObserver captureObserver)
		{
			if (system == null)
				return;
			
			WebcamData data = webcams.get(index);
			if (data == null)
				return;
			
			data.clients--;
			data.observers.remove(captureObserver);
			if (data.clients > 0)
				return;
			
			stop(data.captureStream);
			webcams.remove(index);
		}
		
		private void stop(CaptureStream captureStream)
		{
			if (captureStream != null)
			{
				try
				{
					captureStream.stop();
				}
				catch (CaptureException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					captureStream.dispose();
				}
				catch (CaptureException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		public void dispose()
		{
			if (!hasLTI)
				return;
			
			clients--;
			
			if (clients > 0)
				return;
			
			for (WebcamData data : webcams.values())
				stop(data.captureStream);
			webcams.clear();
			
			if (system != null)
			{
				try
				{
					system.dispose();
				}
				catch (CaptureException e)
				{
					e.printStackTrace();
				}
				system = null;
			}
		}
		
		@SuppressWarnings("unchecked")
		public List<CaptureDeviceInfo> getDevices()
		{
			if (system == null)
				return new ArrayList<CaptureDeviceInfo>();
			
			try
			{
				return system.getCaptureDeviceInfoList();
			}
			catch (CaptureException e)
			{
				e.printStackTrace();
				return new ArrayList<CaptureDeviceInfo>();
			}
		}
	}
}
