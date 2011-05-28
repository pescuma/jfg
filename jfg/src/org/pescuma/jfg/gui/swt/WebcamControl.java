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

public class WebcamControl extends Canvas
{
	private static final LTIManager lti = LTIManagerBuilder.createLTIManager();
	
	private Image image;
	
	private boolean ltiInitialized;
	private int webcamIndex = -1;
	private LTIManager.Image pendingImage;
	private boolean cropImage = true;
	private boolean mirrorImage = false;
	
	private LTIManager.CaptureObserver captureObserver = new LTIManager.CaptureObserver() {
		@Override
		public void onNewImage(LTIManager.Image image)
		{
			setPendingImage(image);
		}
		
		@Override
		public void onError(Exception e)
		{
		}
	};
	
	public WebcamControl(Composite parent, int style)
	{
		super(parent, (style | SWT.DOUBLE_BUFFERED | SWT.NO_BACKGROUND));
		
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
	
	public boolean isMirrorImage()
	{
		return mirrorImage;
	}
	
	public void setMirrorImage(boolean mirrorImage)
	{
		this.mirrorImage = mirrorImage;
	}
	
	public int getWebcamCount()
	{
		return lti.getNumDevices();
	}
	
	public String getWebcamName(int index)
	{
		if (index < 0 || index >= lti.getNumDevices())
			return null;
		
		return lti.getDeviceDescription(index);
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
		
		if (index < 0 || index >= lti.getNumDevices())
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
	
	private synchronized void setPendingImage(LTIManager.Image image)
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
	
	private synchronized void processPendingImage()
	{
		if (pendingImage == null)
			return;
		
		try
		{
			ImageData imageData = lti.convertToSWTImageData(pendingImage);
			if (mirrorImage)
				imageData = flip(imageData);
			
			Image processed = new Image(getDisplay(), imageData);
			disposeImage();
			image = processed;
		}
		finally
		{
			pendingImage = null;
		}
	}
	
	private static ImageData flip(ImageData srcData)
	{
		int bytesPerPixel = srcData.bytesPerLine / srcData.width;
		int destBytesPerLine = srcData.width * bytesPerPixel;
		byte[] newData = new byte[srcData.data.length];
		for (int srcY = 0; srcY < srcData.height; srcY++)
		{
			for (int srcX = 0; srcX < srcData.width; srcX++)
			{
				int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
				destX = srcData.width - srcX - 1;
				destY = srcY;
				
				destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
				srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
				System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
			}
		}
		// destBytesPerLine is used as scanlinePad to ensure that no padding is required
		return new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette, srcData.scanlinePad,
				newData);
	}
	
	private static class LTIManagerBuilder
	{
		private static LTIManager createLTIManager()
		{
			if (hasLTI())
				return new RealLTIManager();
			else
				return new DummyLTIManager();
		}
		
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
	}
	
	private static interface LTIManager
	{
		static interface Image
		{
		}
		
		static interface CaptureObserver
		{
			void onNewImage(Image image);
			
			void onError(Exception e);
		}
		
		boolean init(Shell shell);
		
		boolean startCapture(Shell shell, int index, CaptureObserver captureObserver);
		
		void stopCapture(int index, CaptureObserver captureObserver);
		
		void dispose();
		
		int getNumDevices();
		
		String getDeviceDescription(int index);
		
		ImageData convertToSWTImageData(Image image);
	}
	
	private static class DummyLTIManager implements LTIManager
	{
		@Override
		public int getNumDevices()
		{
			return 0;
		}
		
		@Override
		public String getDeviceDescription(int index)
		{
			return null;
		}
		
		@Override
		public boolean init(Shell shell)
		{
			return false;
		}
		
		@Override
		public boolean startCapture(Shell shell, int index, CaptureObserver captureObserver)
		{
			return false;
		}
		
		@Override
		public void stopCapture(int index, CaptureObserver captureObserver)
		{
		}
		
		@Override
		public void dispose()
		{
		}
		
		@Override
		public ImageData convertToSWTImageData(LTIManager.Image image)
		{
			return null;
		}
	}
	
	private static class RealLTIManager implements LTIManager
	{
		private long clients = 0;
		private com.lti.civil.CaptureSystem system;
		
		private static class ImageAdapter implements LTIManager.Image
		{
			final com.lti.civil.Image image;
			
			public ImageAdapter(com.lti.civil.Image image)
			{
				this.image = image;
			}
		}
		
		private static class CaptureObserverAdapter implements com.lti.civil.CaptureObserver
		{
			final LTIManager.CaptureObserver observer;
			
			public CaptureObserverAdapter(LTIManager.CaptureObserver observer)
			{
				this.observer = observer;
			}
			
			@Override
			public void onNewImage(com.lti.civil.CaptureStream stream, com.lti.civil.Image image)
			{
				observer.onNewImage(new ImageAdapter(image));
			}
			
			@Override
			public void onError(com.lti.civil.CaptureStream stream, com.lti.civil.CaptureException e)
			{
				observer.onError(e);
			}
			
			@Override
			public int hashCode()
			{
				return observer.hashCode();
			}
			
			@Override
			public boolean equals(Object obj)
			{
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				CaptureObserverAdapter other = (CaptureObserverAdapter) obj;
				return observer.equals(other.observer);
			}
		}
		
		private static class WebcamData
		{
			long clients = 0;
			com.lti.civil.CaptureStream captureStream;
			final List<CaptureObserverAdapter> observers = new ArrayList<CaptureObserverAdapter>();
			
			public void addClient(CaptureObserver captureObserver)
			{
				synchronized (observers)
				{
					clients++;
					observers.add(new CaptureObserverAdapter(captureObserver));
				}
			}
			
			public void removeClient(CaptureObserver captureObserver)
			{
				synchronized (observers)
				{
					clients--;
					observers.remove(new CaptureObserverAdapter(captureObserver));
				}
			}
		}
		
		private Map<Integer, WebcamData> webcams = new HashMap<Integer, WebcamData>();
		
		@Override
		public boolean init(Shell shell)
		{
			clients++;
			
			if (system != null)
				return true;
			
			try
			{
				system = com.lti.civil.DefaultCaptureSystemFactorySingleton.instance().createCaptureSystem();
				system.init();
				return true;
			}
			catch (com.lti.civil.CaptureException e)
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
		
		@Override
		public boolean startCapture(Shell shell, int index, LTIManager.CaptureObserver captureObserver)
		{
			if (system == null)
				return false;
			
			List<com.lti.civil.CaptureDeviceInfo> devices = getDevices();
			if (index < 0 || index >= devices.size())
				return false;
			
			{
				WebcamData data = webcams.get(index);
				if (data != null)
				{
					data.addClient(captureObserver);
					return true;
				}
			}
			
			final WebcamData data = new WebcamData();
			data.addClient(captureObserver);
			
			try
			{
				com.lti.civil.CaptureDeviceInfo info = devices.get(index);
				data.captureStream = system.openCaptureDeviceStream(info.getDeviceID());
				data.captureStream.setObserver(new com.lti.civil.CaptureObserver() {
					@Override
					public void onNewImage(com.lti.civil.CaptureStream sender, com.lti.civil.Image image)
					{
						synchronized (data.observers)
						{
							for (com.lti.civil.CaptureObserver observer : data.observers)
								observer.onNewImage(sender, image);
						}
					}
					
					@Override
					public void onError(com.lti.civil.CaptureStream sender, com.lti.civil.CaptureException e)
					{
						System.err.println("Error loading image from " + sender);
						e.printStackTrace();
						
						synchronized (data.observers)
						{
							for (com.lti.civil.CaptureObserver observer : data.observers)
								observer.onError(sender, e);
						}
					}
				});
				
				data.captureStream.start();
				
				webcams.put(index, data);
				
				return true;
			}
			catch (com.lti.civil.CaptureException e)
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
		
		@Override
		public void stopCapture(int index, LTIManager.CaptureObserver captureObserver)
		{
			if (system == null)
				return;
			
			WebcamData data = webcams.get(index);
			if (data == null)
				return;
			
			data.removeClient(captureObserver);
			if (data.clients > 0)
				return;
			
			stop(data.captureStream);
			webcams.remove(index);
		}
		
		private void stop(com.lti.civil.CaptureStream captureStream)
		{
			if (captureStream != null)
			{
				try
				{
					captureStream.stop();
				}
				catch (com.lti.civil.CaptureException e)
				{
					e.printStackTrace();
				}
				
				try
				{
					captureStream.dispose();
				}
				catch (com.lti.civil.CaptureException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void dispose()
		{
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
				catch (com.lti.civil.CaptureException e)
				{
					e.printStackTrace();
				}
				system = null;
			}
		}
		
		@SuppressWarnings("unchecked")
		private List<com.lti.civil.CaptureDeviceInfo> getDevices()
		{
			if (system == null)
				return new ArrayList<com.lti.civil.CaptureDeviceInfo>();
			
			try
			{
				return system.getCaptureDeviceInfoList();
			}
			catch (com.lti.civil.CaptureException e)
			{
				e.printStackTrace();
				return new ArrayList<com.lti.civil.CaptureDeviceInfo>();
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
			final com.lti.civil.VideoFormat format = image.getFormat();
			PaletteData palette = new PaletteData(0xff, 0xff00, 0xff0000);
			return new ImageData(format.getWidth(), format.getHeight(), getBitsPerPixel(format.getFormatType()),
					palette, 1, image.getBytes());
		}
		
		@Override
		public ImageData convertToSWTImageData(LTIManager.Image image)
		{
			ImageAdapter imageAdapter = (ImageAdapter) image;
			return convertToSWTImageData(imageAdapter.image);
		}
		
		@Override
		public int getNumDevices()
		{
			return getDevices().size();
		}
		
		@Override
		public String getDeviceDescription(int index)
		{
			return getDevices().get(index).getDescription();
		}
	}
}
