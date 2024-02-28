package software.xdev.openrewriter.ui;

import javax.swing.Icon;

import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.ScalableIcon;


public final class Icons
{
	public static final Icon PLUGIN = getIcon("/META-INF/pluginIcon.svg");
	
	public static final Icon PLUGIN_13 = getIcon("/META-INF/pluginIcon.svg", 13);
	
	static Icon getIcon(final String path)
	{
		return IconLoader.getIcon(path, Icons.class);
	}
	
	static Icon getIcon(final String path, final float width)
	{
		final Icon icon = getIcon(path);
		if(icon instanceof final ScalableIcon scalableIcon)
		{
			return scaleToWidth(scalableIcon, width);
		}
		return icon;
	}
	
	public static Icon scaleToWidth(final ScalableIcon scalableIcon, final float newWidth)
	{
		if(newWidth != scalableIcon.getIconWidth())
		{
			return scalableIcon.scale(newWidth / scalableIcon.getIconWidth());
		}
		return scalableIcon;
	}
	
	private Icons()
	{
	}
}
