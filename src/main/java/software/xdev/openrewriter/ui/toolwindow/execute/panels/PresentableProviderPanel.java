package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;

import software.xdev.openrewriter.executor.PresentableProvider;


@SuppressWarnings({"unchecked"})
public class PresentableProviderPanel<P extends PresentableProvider<? extends T>, T, R>
	extends ExecuteRecipeConfigPanel<R>
{
	private final ComboBox<P> cbProvider = new ComboBox<>();
	
	private final JPanel targetConfigContainerPanel = new JPanel();
	private ExecuteRecipeConfigPanel<T> targetConfigPanel;
	
	private final Map<Class<? extends T>, P> clazzAndProvider = new HashMap<>();
	
	private final Supplier<Project> getProjectSupplier;
	private final Function<R, T> getFromRootFunc;
	
	public PresentableProviderPanel(
		final Supplier<Project> getProjectSupplier,
		final String lblText,
		final Function<R, T> getFromRootFunc,
		final BiConsumer<R, T> setIntoRootFunc)
	{
		this.getProjectSupplier = getProjectSupplier;
		this.getFromRootFunc = getFromRootFunc;
		
		this.setLayout(new VerticalFlowLayout(0, 0));
		
		// UI
		this.cbProvider.setRenderer((list, value, index, isSelected, cellHasFocus) ->
			new JLabel(value.name(), value.icon(), SwingConstants.LEADING));
		this.targetConfigContainerPanel.setLayout(this.createVerticalFlowLayout(0));
		
		this.addWithVerticalLayout(
			new JLabel(lblText),
			this.cbProvider,
			this.targetConfigContainerPanel);
		
		this.cbProvider.addItemListener(v -> this.changeValue(r -> {
			final P provider = (P)v.getItem();
			
			this.createNewTargetConfigPanel(provider);
			
			final T newData = provider.createDefault(getProjectSupplier.get());
			setIntoRootFunc.accept(r, newData);
			this.targetConfigPanel.updateFromAndBind(newData);
		}));
	}
	
	protected VerticalFlowLayout createVerticalFlowLayout(final int vGap)
	{
		return new VerticalFlowLayout(0, vGap);
	}
	
	protected void createNewTargetConfigPanel(final P provider)
	{
		this.targetConfigContainerPanel.removeAll();
		
		// Dispose old panel to free up resources if possible
		if(this.targetConfigPanel != null)
		{
			this.targetConfigPanel.dispose();
		}
		
		// Create a new panel
		this.targetConfigPanel =
			(ExecuteRecipeConfigPanel<T>)provider.createConfigPanel(this.getProjectSupplier.get());
		this.targetConfigPanel.setLayout(this.createVerticalFlowLayout(5));
		this.targetConfigPanel.setValueChangeCallback(this.getValueChangeCallback());
		
		this.targetConfigContainerPanel.add(this.targetConfigPanel);
	}
	
	public void setAvailable(final List<? extends P> providers)
	{
		this.clazzAndProvider.clear();
		this.clazzAndProvider.putAll(providers.stream()
			.collect(Collectors.toMap(p -> p.matchingClass(), Function.identity())));
		
		this.cbProvider.setModel(new DefaultComboBoxModel<>(new Vector<>(providers)));
	}
	
	@Override
	protected void updateFrom(final R rootData)
	{
		final T data = this.getFromRootFunc.apply(rootData);
		final P provider = this.clazzAndProvider.get(data.getClass());
		this.cbProvider.setSelectedItem(provider);
		this.createNewTargetConfigPanel(provider);
		this.targetConfigPanel.updateFromAndBind(data);
	}
	
	public void refreshTargetConfigPanel()
	{
		this.targetConfigPanel.ifData(this.targetConfigPanel::updateFrom);
	}
}
