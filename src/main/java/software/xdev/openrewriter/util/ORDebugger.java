package software.xdev.openrewriter.util;

public final class ORDebugger
{
	private static ORDebugger instance;
	
	public static ORDebugger getInstance()
	{
		if(instance == null)
		{
			instance = new ORDebugger();
		}
		return instance;
	}
	
	private final boolean debugMode;
	
	public ORDebugger()
	{
		this.debugMode = Boolean.parseBoolean(System.getProperty("or.debugMode"))
			|| Boolean.parseBoolean(System.getenv("IJ_OR_DEBUG_MODE"));
	}
	
	public boolean isDebugMode()
	{
		return this.debugMode;
	}
}
