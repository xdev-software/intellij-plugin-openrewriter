package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.awt.Component;
import java.util.Collection;
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
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.components.panels.VerticalLayout;

import software.xdev.openrewriter.executor.PresentableProvider;


@SuppressWarnings({"unchecked"})
public class PresentableProviderPanel<P extends PresentableProvider<? extends T>, T, R>
	extends ExecuteRecipeConfigPanel<R>
{
	protected final ComboBox<P> cbProvider = new ComboBox<>();
	
	protected final JPanel targetConfigContainerPanel = new JPanel();
	protected ExecuteRecipeConfigPanel<T> targetConfigPanel;
	
	protected final Map<Class<? extends T>, P> clazzAndProvider = new HashMap<>();
	
	protected final Supplier<Project> getProjectSupplier;
	protected final Function<R, T> getFromRootFunc;
	
	public PresentableProviderPanel(
		final Supplier<Project> getProjectSupplier,
		final String lblText,
		final Function<R, T> getFromRootFunc,
		final BiConsumer<R, T> setIntoRootFunc)
	{
		this.getProjectSupplier = getProjectSupplier;
		this.getFromRootFunc = getFromRootFunc;
		
		this.setLayout(new VerticalLayout(0));
		
		// UI
		this.cbProvider.setRenderer(this::cbProviderRenderer);
		this.targetConfigContainerPanel.setLayout(this.createVerticalFlowLayout(0));
		
		this.addWithVerticalLayout(
			new JLabel(lblText),
			this.cbProvider,
			this.targetConfigContainerPanel);
		
		this.cbProvider.addItemListener(e -> this.changeValueOnlyOnSelect(e, i -> (P)i, (r, provider) -> {
			this.createNewTargetConfigPanel(provider);
			
			final T newData = provider.createDefault(getProjectSupplier.get());
			setIntoRootFunc.accept(r, newData);
			if(this.targetConfigPanel != null)
			{
				this.targetConfigPanel.updateFromAndBind(newData);
			}
		}));
	}
	
	protected Component cbProviderRenderer(
		final JList<? extends P> list,
		final P value,
		final int index,
		final boolean isSelected,
		final boolean cellHasFocus)
	{
		return new JLabel(value.name(), value.icon(), SwingConstants.LEADING);
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
		if(this.targetConfigPanel != null)
		{
			this.targetConfigPanel.setLayout(this.createVerticalFlowLayout(5));
			this.targetConfigPanel.setValueChangeCallback(this.getValueChangeCallback());
			
			this.targetConfigContainerPanel.add(this.targetConfigPanel);
		}
		// Otherwise UI is not updated/rendered correctly
		this.revalidate();
	}
	
	@SuppressWarnings("PMD.UseArrayListInsteadOfVector") // Required by underlying API
	public void setAvailable(final List<? extends P> providers)
	{
		this.clazzAndProvider.clear();
		this.clazzAndProvider.putAll(providers.stream()
			.collect(Collectors.toMap(p -> p.matchingClass(), Function.identity())));
		
		this.cbProvider.setModel(new DefaultComboBoxModel<>(new Vector<>(providers)));
	}
	
	public Collection<P> getAvailable()
	{
		return this.clazzAndProvider.values();
	}
	
	@Override
	protected void updateFrom(final R rootData)
	{
		final T data = this.getData(rootData);
		final P provider = this.clazzAndProvider.get(data.getClass());
		this.cbProvider.setSelectedItem(provider);
		this.createNewTargetConfigPanel(provider);
		if(this.targetConfigPanel != null)
		{
			this.targetConfigPanel.updateFromAndBind(data);
		}
	}
	
	public void select(final P provider)
	{
		this.cbProvider.setSelectedItem(provider);
	}
	
	protected P getCurrentSelected()
	{
		return this.cbProvider.getItem();
	}
	
	protected T getData(final R rootData)
	{
		return this.getFromRootFunc.apply(rootData);
	}
	
	public void refreshTargetConfigPanel()
	{
		if(this.targetConfigPanel != null)
		{
			this.targetConfigPanel.ifData(this.targetConfigPanel::updateFrom);
		}
	}
}
