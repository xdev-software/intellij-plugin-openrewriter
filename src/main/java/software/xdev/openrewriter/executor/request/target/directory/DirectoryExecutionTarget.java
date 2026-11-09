package software.xdev.openrewriter.executor.request.target.directory;

import java.nio.file.Path;

import software.xdev.openrewriter.executor.request.target.ExecutionTarget;


public class DirectoryExecutionTarget implements ExecutionTarget
{
	private Path path;
	
	public Path getPath()
	{
		return this.path;
	}
	
	public void setPath(final Path path)
	{
		this.path = path;
	}
	
	@Override
	public boolean canExecute()
	{
		return this.path != null && !this.path.toString().isEmpty();
	}
}
