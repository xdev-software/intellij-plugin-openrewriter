package software.xdev.openrewriter.executor.gradle;

import software.xdev.openrewriter.executor.RecipesExecutorConfig;


public class GradleRecipesExecutorConfig implements RecipesExecutorConfig
{
	private boolean cleanUpTemporaryFiles = true;
	private boolean tryPatchSettings;
	
	public boolean isCleanUpTemporaryFiles()
	{
		return this.cleanUpTemporaryFiles;
	}
	
	public void setCleanUpTemporaryFiles(final boolean cleanUpTemporaryFiles)
	{
		this.cleanUpTemporaryFiles = cleanUpTemporaryFiles;
	}
	
	public boolean isTryPatchSettings()
	{
		return this.tryPatchSettings;
	}
	
	public void setTryPatchSettings(final boolean tryPatchSettings)
	{
		this.tryPatchSettings = tryPatchSettings;
	}
}
