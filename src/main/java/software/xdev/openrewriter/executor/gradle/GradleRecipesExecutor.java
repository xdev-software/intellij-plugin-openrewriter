package software.xdev.openrewriter.executor.gradle;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.action.GradleExecuteTaskAction;
import org.jetbrains.plugins.gradle.util.GradleBundle;
import org.jetbrains.plugins.gradle.util.GradleConstants;

import com.intellij.execution.Executor;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.externalSystem.model.execution.ExternalTaskExecutionInfo;
import com.intellij.openapi.externalSystem.service.execution.ProgressExecutionMode;
import com.intellij.openapi.externalSystem.service.notification.ExternalSystemNotificationManager;
import com.intellij.openapi.externalSystem.service.notification.NotificationCategory;
import com.intellij.openapi.externalSystem.service.notification.NotificationData;
import com.intellij.openapi.externalSystem.service.notification.NotificationSource;
import com.intellij.openapi.externalSystem.task.TaskCallback;
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ContextHelpLabel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.panels.HorizontalLayout;

import icons.GradleIcons;
import software.xdev.openrewriter.executor.RecipesExecutor;
import software.xdev.openrewriter.executor.RequestCommandBuilder;
import software.xdev.openrewriter.executor.gradle.rcb.GradleRequestCommandBuilder;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;
import software.xdev.openrewriter.util.ORDebugger;


public class GradleRecipesExecutor implements RecipesExecutor<GradleRecipesExecutorConfig>
{
	public static final String TMP_GRADLE_EXTENSION = ".openrewriter-plugin.gradle";
	
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
	public boolean isMatchingModule(final Project project, final Module module)
	{
		return ExternalSystemApiUtil.isExternalSystemAwareModule(GradleConstants.SYSTEM_ID, module);
	}
	
	@Override
	public void execute(
		final Project project,
		final ExecutionRequest request,
		final GradleRecipesExecutorConfig config)
	{
		final GradleRunArguments gradleRunArguments = new GradleRunArguments();
		
		// Enrich
		RequestCommandBuilder.getAllFor(GradleRequestCommandBuilder.class)
			.forEach(rcb -> rcb.apply(project, request, gradleRunArguments));
		
		final String workDir = gradleRunArguments.getWorkDir();
		if(Objects.requireNonNull(workDir).isBlank())
		{
			throw new IllegalStateException("No working directory path specified");
		}
		
		final Optional<Path> optSettingsWorkingCopyFile = config.isTryPatchSettings()
			? Stream.of(
				"settings.gradle",
				"settings.gradle.kts")
			.map(s -> Paths.get(workDir, s))
			.filter(Files::exists)
			.findFirst()
			.map(original -> {
				final Path workingCopy =
					GradleRecipesExecutor.this.createTempGradleFile(
						original.getParent().toString(),
						original.getFileName().toString());
				try
				{
					Files.copy(original, workingCopy, StandardCopyOption.REPLACE_EXISTING);
				}
				catch(final IOException ioe)
				{
					throw new UncheckedIOException(ioe);
				}
				return workingCopy;
			})
			: Optional.empty();
		// Patch settings: https://stackoverflow.com/a/69197871
		optSettingsWorkingCopyFile.ifPresent(f ->
			this.replaceContent(f, "FAIL_ON_PROJECT_REPOS", "PREFER_PROJECT"));
		
		final Path tempInitFile = this.createTempGradleFile(workDir, "init");
		this.writeGradleInitFile(tempInitFile, gradleRunArguments.getDependenciesForRewrite());
		
		final Runnable cleanUp = () -> {
			if(config.isCleanUpTemporaryFiles())
			{
				optSettingsWorkingCopyFile.ifPresent(this::delete);
				this.delete(tempInitFile);
			}
		};
		
		runGradle(
			project,
			DefaultRunExecutor.getRunExecutorInstance(),
			workDir,
			"rewriteRun --init-script "
				+ tempInitFile.getFileName().toString()
				+ optSettingsWorkingCopyFile.map(f -> " --settings-file " + f).orElse("")
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
				+ "      classpath(\"org.openrewrite:plugin:latest.release\")\n"
				+ "    }\n"
				+ "}\n"
				+ "rootProject {\n"
				+ "    plugins.apply(org.openrewrite.gradle.RewritePlugin)\n"
				+ "    dependencies {\n"
				+ dependenciesForRewrite.stream()
				.map(d -> "        rewrite(\"" + d + "\")\n")
				.collect(Collectors.joining())
				+ "    }\n"
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
	
	protected Path createTempGradleFile(final String directory, final String prefix)
	{
		try
		{
			return Files.createTempFile(Paths.get(directory), prefix, TMP_GRADLE_EXTENSION);
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	
	protected void replaceContent(final Path file, final String target, final String replacement)
	{
		try
		{
			Files.writeString(file, Files.readString(file).replace(target, replacement));
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
		catch(final RuntimeException ex)
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
	
	@SuppressWarnings("java:S3011")
	private static ExternalTaskExecutionInfo buildTaskInfo(
		@NotNull final String projectPath,
		@NotNull final String fullCommandLine,
		@Nullable final Executor executor)
	{
		try
		{
			final Method mbuildTaskInfo = getMBuildTaskInfo();
			mbuildTaskInfo.setAccessible(true);
			return (ExternalTaskExecutionInfo)mbuildTaskInfo.invoke(null, projectPath, fullCommandLine, executor);
		}
		catch(final NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException("Unable to execute GradleExecuteTaskAction#buildTaskInfo", e);
		}
	}
	
	static Method getMBuildTaskInfo() throws NoSuchMethodException
	{
		return GradleExecuteTaskAction.class.getDeclaredMethod(
			"buildTaskInfo",
			String.class,
			String.class,
			Executor.class);
	}
	// endregion
	
	@Override
	public GradleRecipesExecutorConfig createDefault(final Project project)
	{
		return new GradleRecipesExecutorConfig();
	}
	
	@Override
	public Class<GradleRecipesExecutorConfig> matchingClass()
	{
		return GradleRecipesExecutorConfig.class;
	}
	
	@Override
	@Nullable
	public GradleRecipesExecutorConfigPanel createConfigPanel(final Project project)
	{
		return new GradleRecipesExecutorConfigPanel();
	}
	
	public static class GradleRecipesExecutorConfigPanel extends ExecuteRecipeConfigPanel<GradleRecipesExecutorConfig>
	{
		private final Optional<JBCheckBox> optChbxCleanUpTemporaryFiles =
			ORDebugger.getInstance().isDebugMode()
				? Optional.of(new JBCheckBox(
				"Cleanup generated temporary files after execution (only disable for debugging purposes)"))
				: Optional.empty();
		private final JBCheckBox chbxTryPatchSettings = new JBCheckBox("Try to patch setting.gradle[.kts]");
		
		public GradleRecipesExecutorConfigPanel()
		{
			this.optChbxCleanUpTemporaryFiles.ifPresent(this::add);
			
			this.addComponentWithHelp(
				this.chbxTryPatchSettings,
				ContextHelpLabel.createWithLink(
					null,
					"<html><body>Only required when <code>dependencyResolutionManagement.repositoriesMode= "
						+ "FAIL_ON_PROJECT_REPOS</code> is present</body></html>",
					"Gradle docs",
					true,
					() -> BrowserUtil.browse(
						"https://docs.gradle.org/8.5/userguide/dependency_management.html"
							+ "#sub:centralized-repository-declaration")));
			
			this.optChbxCleanUpTemporaryFiles.ifPresent(chbx -> chbx.addItemListener(e ->
				this.changeValueForCheckbox(e, GradleRecipesExecutorConfig::setCleanUpTemporaryFiles)));
			this.chbxTryPatchSettings.addItemListener(e ->
				this.changeValueForCheckbox(e, GradleRecipesExecutorConfig::setTryPatchSettings));
		}
		
		@Override
		protected void updateFrom(final GradleRecipesExecutorConfig data)
		{
			this.optChbxCleanUpTemporaryFiles.ifPresent(chbx -> chbx.setSelected(data.isCleanUpTemporaryFiles()));
			this.chbxTryPatchSettings.setSelected(data.isTryPatchSettings());
		}
		
		protected JPanel addComponentWithHelp(final JComponent component, final ContextHelpLabel helpLabel)
		{
			final JPanel panel = new JPanel();
			panel.setLayout(new HorizontalLayout(5));
			panel.add(component);
			panel.add(helpLabel);
			
			this.add(panel);
			return panel;
		}
	}
}
