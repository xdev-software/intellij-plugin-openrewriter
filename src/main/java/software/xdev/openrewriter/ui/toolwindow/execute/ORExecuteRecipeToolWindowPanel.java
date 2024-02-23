package software.xdev.openrewriter.ui.toolwindow.execute;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

import software.xdev.openrewriter.executor.RecipesExecutorEPManager;
import software.xdev.openrewriter.executor.RecipesExecutorService;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.target.module.ModuleExecutionTarget;
import software.xdev.openrewriter.executor.request.target.module.ModuleExecutionTargetProvider;
import software.xdev.openrewriter.ui.NotificationService;
import software.xdev.openrewriter.ui.UIThreadUtil;
import software.xdev.openrewriter.ui.toolwindow.ORSimpleToolWindowPanel;


public class ORExecuteRecipeToolWindowPanel extends ORSimpleToolWindowPanel
{
	public static final String TITLE = "Execute Recipe";
	
	// Components
	private final SimpleExecuteRecipeAction actionExec = new SimpleExecuteRecipeAction(
		TITLE,
		AllIcons.Actions.Execute,
		this::canExecute,
		this::execInvoked);
	
	private final SimpleExecuteRecipeAction actionReset = new SimpleExecuteRecipeAction(
		"Restore Defaults",
		AllIcons.Actions.Back,
		this::canReset,
		ev -> this.setDefaultExecutionRequest());
	
	private final ExecuteRecipeDataPanel dataPanel = new ExecuteRecipeDataPanel();
	
	private final ExecuteRecipeWithTargetPanel withTargetPanel = new ExecuteRecipeWithTargetPanel();
	
	// State
	private final AtomicBoolean isExecuting = new AtomicBoolean(false);
	
	private ExecutionRequest executionRequest;
	
	public ORExecuteRecipeToolWindowPanel(final Project project)
	{
		super(false, true);
		
		this.setProject(project);
		
		this.initUI();
		
		this.setDefaultExecutionRequest();
	}
	
	protected void initUI()
	{
		final DefaultActionGroup actionGroup = new DefaultActionGroup();
		actionGroup.add(this.actionExec);
		actionGroup.addSeparator();
		actionGroup.add(this.actionReset);
		this.setToolbar("OR_EXECUTE_RECIPE", actionGroup);
		
		this.withTargetPanel.setAvailableData(
			RecipesExecutorEPManager.executors(),
			RecipesExecutorEPManager.executionTargetProviders());
		
		this.rootConfigPanelsStream().forEach(p -> p.setValueChangeCallback(this::refreshToolbar));
		
		super.setContent(this.createSplitter(
			this.dataPanel,
			this.withTargetPanel,
			"OR_EXECUTE_RECIPE_PROPORTION_PROPERTY"));
	}
	
	protected boolean canExecute()
	{
		return !this.isExecuting.get() && this.executionRequest != null && this.executionRequest.canExecute();
	}
	
	protected boolean canReset()
	{
		return !this.isExecuting.get();
	}
	
	protected void execInvoked(final AnActionEvent event)
	{
		event.getPresentation().setEnabled(false);
		this.isExecuting.set(true);
		
		final NotificationService notificationService = this.getService(NotificationService.class);
		this.getService(RecipesExecutorService.class).executeRewrite(
			this.executionRequest,
			() -> notificationService.builder()
				.withGroupId(NotificationService.GroupId.REWRITE_SUCCESS)
				.withContent("Rewrite executed successfully")
				.show(),
			ex -> notificationService.builder()
				.withGroupId(NotificationService.GroupId.REWRITE_FAILED)
				.withTitle("Failed to rewrite")
				.withContent(ex.toString())
				.withType(NotificationType.ERROR)
				.show(),
			() -> {
				this.isExecuting.set(false);
				UIThreadUtil.run(this.getProject(), this::refreshToolbar);
			}
		);
	}
	
	protected void setDefaultExecutionRequest()
	{
		this.setExecutionRequest(this.getService(RecipesExecutorService.class).createDefaultRequest());
	}
	
	protected void setExecutionRequest(final ExecutionRequest executionRequest)
	{
		this.executionRequest = Objects.requireNonNull(executionRequest);
		
		this.rootConfigPanelsStream().forEach(p -> p.updateFromAndBind(this.executionRequest));
		
		this.refreshToolbar();
	}
	
	protected Stream<ExecuteRecipeConfigPanel<ExecutionRequest>> rootConfigPanelsStream()
	{
		return Stream.of(this.dataPanel, this.withTargetPanel);
	}
	
	@SuppressWarnings("java:S2589") // Is null in constructor!
	@Override
	public void setProject(final Project project)
	{
		super.setProject(project);
		if(this.withTargetPanel != null)
		{
			this.withTargetPanel.setProject(project);
		}
	}
	
	public void requestedWith(
		final Project project,
		@Nullable final Module module)
	{
		this.setProject(project);
		
		if(module != null)
		{
			RecipesExecutorEPManager.executionTargetProviders().stream()
				.filter(ModuleExecutionTargetProvider.class::isInstance)
				.map(ModuleExecutionTargetProvider.class::cast)
				.findFirst()
				.ifPresent(p -> {
					Runnable updateTask = null;
					if(!Objects.equals(this.executionRequest.getTarget().getClass(), p.matchingClass()))
					{
						this.executionRequest.setTarget(p.createDefault());
						updateTask = () -> this.withTargetPanel.updateFromAndBind(this.executionRequest);
					}
					if(this.executionRequest.getTarget() instanceof final ModuleExecutionTarget moduleExecutionTarget)
					{
						moduleExecutionTarget.setModule(module);
						// Prev update has higher prio
						if(updateTask == null)
						{
							updateTask = this.withTargetPanel::refreshTargetConfigPanel;
						}
					}
					
					Optional.ofNullable(updateTask).ifPresent(Runnable::run);
				});
		}
	}
}
