package software.xdev.openrewriter.executor.maven.rcb;

import java.util.Objects;

import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;

import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.module.ModuleExecutionTarget;


public class MavenModuleExecutionTargetRCB implements MavenRequestCommandBuilder
{
	@Override
	public void apply(
		final Project project,
		final ExecutionRequest request,
		final MavenRunnerParameters runnerParameters,
		final MavenGeneralSettings generalSettings,
		final MavenRunnerSettings runnerSettings)
	{
		if(request.getTarget() instanceof final ModuleExecutionTarget met)
		{
			runnerParameters.setWorkingDirPath(
				Objects.requireNonNull(ProjectUtil.guessModuleDir(met.getModule())).getPath());
		}
	}
}
