package software.xdev.openrewriter.executor;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.annotations.RequiresBackgroundThread;

import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.recipedata.artifact.ArtifactRecipesData;


@Service(Service.Level.PROJECT)
public final class RecipesExecutorService
{
	private static final Logger LOG = Logger.getInstance(RecipesExecutorService.class);
	private final Project project;
	
	public RecipesExecutorService(final Project project)
	{
		this.project = project;
	}
	
	public ExecutionRequest createDefaultRequest()
	{
		return new ExecutionRequest(
			new ArtifactRecipesData(),
			// Can be null when neither Maven nor Gradle is supported by the IDE
			RecipesExecutorEPManager.executors().stream().findFirst().orElse(null),
			RecipesExecutorEPManager.executionTargetProviders().stream().findFirst().orElseThrow().createDefault()
		);
	}
	
	public void executeRewrite(
		final ExecutionRequest request,
		final Runnable onSuccess,
		final Consumer<Exception> onException,
		final Runnable onFinally)
	{
		final RecipesExecutor recipesExecutor = request.getExecutor();
		
		final Runnable runnable = () ->
		{
			try
			{
				recipesExecutor.execute(RecipesExecutorService.this.project, request);
				onSuccess.run();
			}
			catch(final Exception ex)
			{
				LOG.warn("Failed to execute rewrite", ex);
				onException.accept(ex);
			}
			finally
			{
				onFinally.run();
			}
		};
		
		if(recipesExecutor.isAsync())
		{
			runnable.run();
			return;
		}
		
		ProgressManager.getInstance().run(
			new Task.Backgroundable(this.project, "Executing rewrite...", false)
			{
				@Override
				@RequiresBackgroundThread
				public void run(@NotNull final ProgressIndicator indicator)
				{
					indicator.setIndeterminate(true);
					runnable.run();
				}
			}
		);
	}
}
