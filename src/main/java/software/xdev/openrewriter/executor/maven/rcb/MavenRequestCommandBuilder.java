package software.xdev.openrewriter.executor.maven.rcb;

import org.jetbrains.idea.maven.execution.MavenRunnerParameters;
import org.jetbrains.idea.maven.execution.MavenRunnerSettings;
import org.jetbrains.idea.maven.project.MavenGeneralSettings;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RequestCommandBuilder;
import software.xdev.openrewriter.executor.request.ExecutionRequest;


public interface MavenRequestCommandBuilder extends RequestCommandBuilder
{
	void apply(
		final Project project,
		final ExecutionRequest request,
		final MavenRunnerParameters runnerParameters,
		final MavenGeneralSettings generalSettings,
		final MavenRunnerSettings runnerSettings);
}
