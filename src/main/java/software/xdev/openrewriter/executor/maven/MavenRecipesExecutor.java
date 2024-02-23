package software.xdev.openrewriter.executor.maven;

import javax.swing.Icon;

import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

import com.intellij.openapi.project.Project;

import icons.MavenIcons;
import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;


public class MavenRecipesExecutor implements RecipesExecutor
{
	@Override
	public String name()
	{
		return "Maven";
	}
	
	@Override
	public Icon icon()
	{
		return MavenIcons.ToolWindowMaven;
	}
	
	@Override
	public int orderPriority()
	{
		return 5;
	}
	
	@Override
	public void execute(final Project project, final ExecutionRequest request)
	{
		final MavenRunnerParameters runnerParameters = new MavenRunnerParameters();
		final MavenGeneralSettings generalSettings = new MavenGeneralSettings();
		final MavenRunnerSettings runnerSettings = new MavenRunnerSettings();
		
		runnerParameters.setCommandLine("--version");
		
		runnerSettings.setSkipTests(true);
		
		MavenRunConfigurationType.runConfiguration(
			project,
			runnerParameters,
			generalSettings,
			runnerSettings,
			null);
	}
}
