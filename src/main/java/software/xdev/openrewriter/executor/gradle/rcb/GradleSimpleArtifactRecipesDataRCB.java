package software.xdev.openrewriter.executor.gradle.rcb;

import java.util.stream.Collectors;

import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.gradle.GradleRecipesExecutor;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.recipedata.simpleartifact.Artifact;
import software.xdev.openrewriter.executor.request.recipedata.simpleartifact.Recipe;
import software.xdev.openrewriter.executor.request.recipedata.simpleartifact.SimpleArtifactRecipesData;


public class GradleSimpleArtifactRecipesDataRCB implements GradleRequestCommandBuilder
{
	@Override
	public void apply(
		final Project project,
		final ExecutionRequest request,
		final GradleRecipesExecutor.GradleRunArguments gradleRunArguments)
	{
		if(request.getRecipesData() instanceof final SimpleArtifactRecipesData r)
		{
			r.getArtifacts().stream()
				// https://docs.gradle.org/current/userguide/single_versions.html
				.map(a -> a.toFullMavenArtifact(version -> Artifact.DEFAULT_VERSION.equals(version)
					? "latest.release"
					: version))
				.forEach(gradleRunArguments.getDependenciesForRewrite()::add);
			
			gradleRunArguments.setGradleCommandLine(gradleRunArguments.getGradleCommandLine()
				+ " -Drewrite.activeRecipes="
				+ r.getRecipes().stream().map(Recipe::getId).collect(Collectors.joining(",")));
		}
	}
}
