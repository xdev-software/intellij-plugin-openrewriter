package software.xdev.openrewriter.executor.gradle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.Icon;

import org.gradle.cli.CommandLineArgumentException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.util.GradleBundle;
import org.jetbrains.plugins.gradle.util.GradleCommandLine;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.model.execution.ExternalTaskExecutionInfo;
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode;
import com.intellij.openapi.externalSystem.service.notification.ExternalSystemNotificationManager;
import com.intellij.openapi.externalSystem.service.notification.NotificationCategory;
import com.intellij.openapi.externalSystem.service.notification.NotificationData;
import com.intellij.openapi.externalSystem.service.notification.NotificationSource;
import com.intellij.openapi.externalSystem.task.TaskCallback;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.project.Project;

import icons.GradleIcons;
import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.RequestCommandBuilder;
import software.xdev.openrewriter.executor.gradle.rcb.GradleRequestCommandBuilder;
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
		final GradleRunArguments gradleRunArguments = new GradleRunArguments();
		
		RequestCommandBuilder.getAllFor(GradleRequestCommandBuilder.class)
			.forEach(rcb -> rcb.apply(project, request, gradleRunArguments));
		
		if(Objects.requireNonNull(gradleRunArguments.getWorkDir()).isBlank())
		{
			throw new IllegalStateException("No working directory path specified");
		}
		
		final Path tempInitFile = this.createTempGradleInitFile(gradleRunArguments.getWorkDir());
		
		this.writeGradleInitFile(tempInitFile, gradleRunArguments.getDependenciesForRewrite());
		
		final Runnable cleanUp = () -> this.delete(tempInitFile);
		
		runGradle(
			project,
			DefaultRunExecutor.getRunExecutorInstance(),
			gradleRunArguments.getWorkDir(),
			"gradle rewriteRun --init-script "
				+ tempInitFile.getFileName().toString()
				+ " "
				+ gradleRunArguments.getGradleCommandLine(),
			new TaskCallback()
			{
				@Override
				public void onSuccess()
				{
					cleanUp.run();
				}
				
				@Override
				public void onFailure()
				{
					cleanUp.run();
				}
			});
	}
	
	protected Path createTempGradleInitFile(final String directory)
	{
		try
		{
			return Files.createTempFile(Paths.get(directory), "init", ".openrewriter-plugin.gradle");
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	
	@SuppressWarnings("java:S1192")
	protected void writeGradleInitFile(final Path file, final Set<String> dependenciesForRewrite)
	{
		try
		{
			Files.writeString(file, "initscript {\n"
				+ "    repositories {\n"
				+ "        maven { url \"https://plugins.gradle.org/m2\" }\n"
				+ "    }\n"
				+ "    dependencies {\n"
				+ "        classpath(\"org.openrewrite:plugin:latest.release\")\n"
				+ "    }\n"
				+ "}\n"
				+ "\n"
				+ "rootProject {\n"
				+ "    plugins.apply(org.openrewrite.gradle.RewritePlugin)\n"
				+ "    dependencies {\n"
				+ dependenciesForRewrite.stream()
				.map(d -> "        rewrite(\"" + d + "\")\n")
				.collect(Collectors.joining())
				+ "    }\n"
				+ "\n"
				+ "    afterEvaluate {\n"
				+ "        if (repositories.isEmpty()) {\n"
				+ "            repositories {\n"
				+ "                mavenCentral()\n"
				+ "            }\n"
				+ "        }\n"
				+ "    }\n"
				+ "}");
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	
	protected void delete(final Path file)
	{
		try
		{
			Files.delete(file);
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	
	public static class GradleRunArguments
	{
		private String workDir = "";
		private String gradleCommandLine = "";
		private final Set<String> dependenciesForRewrite = new HashSet<>();
		
		public String getWorkDir()
		{
			return this.workDir;
		}
		
		public void setWorkDir(final String workDir)
		{
			this.workDir = workDir;
		}
		
		public String getGradleCommandLine()
		{
			return this.gradleCommandLine;
		}
		
		public void setGradleCommandLine(final String gradleCommandLine)
		{
			this.gradleCommandLine = gradleCommandLine;
		}
		
		public Set<String> getDependenciesForRewrite()
		{
			return this.dependenciesForRewrite;
		}
	}
	
	// region Forked from GradleExecuteTaskAction to add callback functionality + removed configuration registration
	public static void runGradle(
		@NotNull final Project project,
		@Nullable final Executor executor,
		@NotNull final String workDirectory,
		@NotNull final String fullCommandLine,
		@NotNull final TaskCallback callback)
	{
		final ExternalTaskExecutionInfo taskExecutionInfo;
		try
		{
			taskExecutionInfo = buildTaskInfo(workDirectory, fullCommandLine, executor);
		}
		catch(final CommandLineArgumentException ex)
		{
			final String italicFormat = "<i>%s</i> \n";
			final NotificationData notificationData = new NotificationData(
				GradleBundle.message("gradle.command.line.parse.error.invalid.arguments"),
				String.format(italicFormat, fullCommandLine) + ex.getMessage(),
				NotificationCategory.WARNING, NotificationSource.TASK_EXECUTION
			);
			notificationData.setBalloonNotification(true);
			ExternalSystemNotificationManager.getInstance(project)
				.showNotification(GradleConstants.SYSTEM_ID, notificationData);
			return;
		}
		
		ExternalSystemUtil.runTask(
			taskExecutionInfo.getSettings(),
			taskExecutionInfo.getExecutorId(),
			project,
			GradleConstants.SYSTEM_ID,
			callback,
			ProgressExecutionMode.IN_BACKGROUND_ASYNC);
	}
	
	@SuppressWarnings("all")
	private static ExternalTaskExecutionInfo buildTaskInfo(
		@NotNull final String projectPath,
		@NotNull final String fullCommandLine,
		@Nullable final Executor executor
	) throws CommandLineArgumentException
	{
		final GradleCommandLine commandLine = GradleCommandLine.parse(fullCommandLine);
		final ExternalSystemTaskExecutionSettings settings = new ExternalSystemTaskExecutionSettings();
		settings.setExternalProjectPath(projectPath);
		settings.setTaskNames(commandLine.getTasksAndArguments().toList());
		settings.setScriptParameters(commandLine.getScriptParameters().toString());
		settings.setExternalSystemIdString(GradleConstants.SYSTEM_ID.toString());
		return new ExternalTaskExecutionInfo(
			settings,
			executor == null ? DefaultRunExecutor.EXECUTOR_ID : executor.getId());
	}
	// endregion
}
