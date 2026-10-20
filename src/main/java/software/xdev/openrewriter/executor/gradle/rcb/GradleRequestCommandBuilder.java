package software.xdev.openrewriter.executor.gradle.rcb;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RequestCommandBuilder;
import software.xdev.openrewriter.executor.gradle.GradleRecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;


public interface GradleRequestCommandBuilder extends RequestCommandBuilder
{
	void apply(
		final Project project,
		final ExecutionRequest request,
		final GradleRecipesExecutor.GradleRunArguments gradleRunArguments);
}
