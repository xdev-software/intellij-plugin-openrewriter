package software.xdev.openrewriter.executor.gradle;

import javax.swing.Icon;

import com.intellij.openapi.project.Project;

import icons.GradleIcons;
import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;


public class GradleRecipesExecutor implements RecipesExecutor
{
	@Override
	public String name()
	{
		return "Gradle";
	}
	
	@Override
	public Icon icon()
	{
		return GradleIcons.ToolWindowGradle;
	}
	
	@Override
	public void execute(final Project project, final ExecutionRequest request)
	{
		// TODO
	}
}
