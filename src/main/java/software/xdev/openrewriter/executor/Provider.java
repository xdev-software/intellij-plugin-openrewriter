package software.xdev.openrewriter.executor;

public interface Provider
{
	String name();
	
	default int orderPriority()
	{
		return 10;
	}
}
