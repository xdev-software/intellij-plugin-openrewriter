package software.xdev.openrewriter.executor.maven;

import java.util.Objects;
import java.util.Optional;

import javax.swing.Icon;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.execution.MavenRunConfigurationType;
import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import icons.MavenIcons;
import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.RequestCommandBuilder;
import software.xdev.openrewriter.executor.maven.rcb.MavenRequestCommandBuilder;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;


public class MavenRecipesExecutor implements RecipesExecutor<MavenRecipesExecutorConfig>
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
		// Prioritize Maven as it's more widespread and doesn't require creation of a temporary file
		// Anyway: Project is usually selected automatically based on #isMatchingModule
		return 5;
	}
	
	@Override
	public boolean isMatchingModule(final Project project, final Module module)
	{
		return Optional.ofNullable(MavenProjectsManager.getInstanceIfCreated(project))
			.map(mavenProjectsManager -> mavenProjectsManager.isMavenizedModule(module))
			.orElse(false);
	}
	
	@Override
	public void execute(final Project project, final ExecutionRequest request, final MavenRecipesExecutorConfig config)
	{
		final MavenRunnerParameters runnerParameters = new MavenRunnerParameters();
		final MavenGeneralSettings generalSettings = new MavenGeneralSettings();
		final MavenRunnerSettings runnerSettings = new MavenRunnerSettings();
		
		runnerParameters.setCommandLine("-U org.openrewrite.maven:rewrite-maven-plugin:run@openrewriter-plugin");
		
		runnerSettings.setSkipTests(true);
		
		RequestCommandBuilder.getAllFor(MavenRequestCommandBuilder.class)
			.forEach(rcb -> rcb.apply(project, request, runnerParameters, generalSettings, runnerSettings));
		
		if(Objects.requireNonNull(runnerParameters.getWorkingDirPath()).isBlank())
		{
			throw new IllegalStateException("No working directory path specified");
		}
		
		MavenRunConfigurationType.runConfiguration(
			project,
			runnerParameters,
			generalSettings,
			runnerSettings,
			null);
	}
	
	@Override
	public MavenRecipesExecutorConfig createDefault(final Project project)
	{
		return new MavenRecipesExecutorConfig();
	}
	
	@Override
	public Class<MavenRecipesExecutorConfig> matchingClass()
	{
		return MavenRecipesExecutorConfig.class;
	}
	
	@Override
	@Nullable
	public ExecuteRecipeConfigPanel<? extends MavenRecipesExecutorConfig> createConfigPanel(final Project project)
	{
		return null;
	}
}
