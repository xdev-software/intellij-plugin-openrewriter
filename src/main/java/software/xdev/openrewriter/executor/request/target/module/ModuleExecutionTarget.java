package software.xdev.openrewriter.executor.request.target.module;

import com.intellij.openapi.module.Module;

import software.xdev.openrewriter.executor.request.target.ExecutionTarget;


public class ModuleExecutionTarget implements ExecutionTarget
{
	private Module module;
	
	public ModuleExecutionTarget(final Module module)
	{
		this.module = module;
	}
	
	public Module getModule()
	{
		return this.module;
	}
	
	public void setModule(final Module module)
	{
		this.module = module;
	}
	
	@Override
	public boolean canExecute()
	{
		return this.module != null;
	}
}
