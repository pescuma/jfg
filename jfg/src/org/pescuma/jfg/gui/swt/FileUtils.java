package org.pescuma.jfg.gui.swt;

import java.io.File;
import java.io.IOException;

public class FileUtils
{
	public static String getFullPath(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException e)
		{
			return file.getAbsolutePath();
		}
	}
	
}
