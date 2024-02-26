package software.xdev.openrewriter.ui.toolwindow.execute;

import java.awt.event.ActionEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nullable;

import com.intellij.icons.AllIcons;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.panels.HorizontalLayout;

import software.xdev.openrewriter.executor.RecipesExecutorEPManager;
import software.xdev.openrewriter.executor.RecipesExecutorService;
import software.xdev.openrewriter.executor.request.ExecutionRequest;
import software.xdev.openrewriter.executor.request.recipedata.RecipesData;
import software.xdev.openrewriter.executor.request.recipedata.RecipesDataProvider;
import software.xdev.openrewriter.executor.request.target.module.ModuleExecutionTarget;
import software.xdev.openrewriter.executor.request.target.module.ModuleExecutionTargetProvider;
import software.xdev.openrewriter.ui.NotificationService;
import software.xdev.openrewriter.ui.UIThreadUtil;
import software.xdev.openrewriter.ui.toolwindow.ORSimpleToolWindowPanel;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeConfigPanel;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.ExecuteRecipeWithAndTargetPanel;
import software.xdev.openrewriter.ui.toolwindow.execute.panels.PresentableProviderPanel;


public class ORExecuteRecipeToolWindowPanel extends ORSimpleToolWindowPanel
{
	public static final String TITLE = "Execute Recipe";
	
	// Components
	private final SimpleExecuteRecipeAction actionExec = new SimpleExecuteRecipeAction(
		TITLE,
		AllIcons.Actions.Execute,
		this::canExecute,
		this::execInvokedAction);
	
	private final SimpleExecuteRecipeAction actionReset = new SimpleExecuteRecipeAction(
		"Restore Defaults",
		AllIcons.Actions.Back,
		this::canReset,
		ev -> this.setDefaultExecutionRequest());
	
	private final PresentableProviderPanel<RecipesDataProvider<?>, RecipesData, ExecutionRequest> dataPanel =
		new PresentableProviderPanel<>(
			this::getProject,
			"Run recipe from",
			ExecutionRequest::getRecipesData,
			ExecutionRequest::setRecipesData);
	
	private final JPanel withAndTargetContainerPanel = new JPanel();
	
	private final ExecuteRecipeWithAndTargetPanel withAndTargetPanel =
		new ExecuteRecipeWithAndTargetPanel(this::getProject);
	
	private final JButton btnRewrite = new JButton("Rewrite", AllIcons.Actions.Execute);
	
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
		
		this.dataPanel.setLayout(new VerticalFlowLayout(true, true));
		this.dataPanel.setAvailable(RecipesExecutorEPManager.recipesDataProviders());
		
		this.withAndTargetContainerPanel.setLayout(
			new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, true));
		this.withAndTargetContainerPanel.add(this.withAndTargetPanel);
		
		this.btnRewrite.addActionListener(this::execInvokedButton);
		
		final JPanel buttonBarHl = new JPanel();
		buttonBarHl.setLayout(new HorizontalLayout(0));
		buttonBarHl.add(this.btnRewrite, HorizontalLayout.RIGHT);
		
		final JPanel buttonBarVl = new JPanel();
		buttonBarVl.setLayout(new VerticalFlowLayout(VerticalFlowLayout.BOTTOM));
		buttonBarVl.add(buttonBarHl);
		
		this.withAndTargetContainerPanel.add(buttonBarVl);
		
		this.withAndTargetPanel.setAvailableData(
			RecipesExecutorEPManager.executors(),
			RecipesExecutorEPManager.executionTargetProviders());
		
		this.rootConfigPanelsStream().forEach(p -> p.setValueChangeCallback(this::updateUIState));
		
		super.setContent(this.createSplitter(
			this.dataPanel,
			this.withAndTargetContainerPanel,
			"OR_EXECUTE_RECIPE_PROPORTION_PROPERTY"));
	}
	
	protected void updateBtnRewriteEnabled()
	{
		this.btnRewrite.setEnabled(this.canExecute());
	}
	
	protected void updateUIState()
	{
		this.refreshToolbar();
		UIThreadUtil.run(this.getProject(), this::updateBtnRewriteEnabled);
	}
	
	protected boolean canExecute()
	{
		return !this.isExecuting.get() && this.executionRequest != null && this.executionRequest.canExecute();
	}
	
	protected boolean canReset()
	{
		return !this.isExecuting.get();
	}
	
	protected void execInvokedAction(final AnActionEvent event)
	{
		event.getPresentation().setEnabled(false);
		this.btnRewrite.setEnabled(false);
		
		this.execInvoked();
	}
	
	protected void execInvokedButton(final ActionEvent event)
	{
		this.btnRewrite.setEnabled(false);
		this.refreshToolbar();
		
		this.execInvoked();
	}
	
	protected void execInvoked()
	{
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
				this.updateUIState();
			}
		);
	}
	
	protected void setDefaultExecutionRequest()
	{
		this.setExecutionRequest(this.getService(RecipesExecutorService.class).createDefaultRequest(this.getProject()));
	}
	
	protected void setExecutionRequest(final ExecutionRequest executionRequest)
	{
		this.executionRequest = Objects.requireNonNull(executionRequest);
		
		this.rootConfigPanelsStream().forEach(p -> p.updateFromAndBind(this.executionRequest));
		
		this.updateUIState();
	}
	
	protected Stream<ExecuteRecipeConfigPanel<ExecutionRequest>> rootConfigPanelsStream()
	{
		return Stream.of(this.dataPanel, this.withAndTargetPanel);
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
						this.executionRequest.setTarget(p.createDefault(project));
						updateTask = () -> this.withAndTargetPanel.updateFromAndBind(this.executionRequest);
					}
					if(this.executionRequest.getTarget() instanceof final ModuleExecutionTarget moduleExecutionTarget)
					{
						moduleExecutionTarget.setModule(module);
						// Prev update has higher prio
						if(updateTask == null)
						{
							updateTask = this.withAndTargetPanel::refreshTargetConfigPanel;
						}
					}
					
					Optional.ofNullable(updateTask).ifPresent(Runnable::run);
				});
		}
	}
}
