package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.awt.Font;
import java.awt.event.ItemEvent;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.VerticalFlowLayout;


public abstract class ExecuteRecipeConfigPanel<T> extends JPanel implements Disposable
{
	private Optional<T> optData = Optional.empty();
	
	private Runnable valueChangeCallback = () -> { /* NO OP */ };
	
	protected ExecuteRecipeConfigPanel()
	{
		this.setLayout(new VerticalFlowLayout());
	}
	
	public void setValueChangeCallback(@NotNull final Runnable valueChangeCallback)
	{
		this.valueChangeCallback = valueChangeCallback;
	}
	
	protected Runnable getValueChangeCallback()
	{
		return this.valueChangeCallback;
	}
	
	protected <C> void changeValueOnlyOnSelect(
		final ItemEvent itemEvent,
		final Function<Object, C> caster,
		final BiConsumer<T, C> c)
	{
		if(itemEvent.getStateChange() == ItemEvent.SELECTED)
		{
			this.changeValue(t -> c.accept(t, caster.apply(itemEvent.getItem())));
		}
	}
	
	protected void changeValue(final Consumer<T> c)
	{
		this.ifData(r -> {
			c.accept(r);
			this.fireValueChange();
		});
	}
	
	protected void fireValueChange()
	{
		this.valueChangeCallback.run();
	}
	
	protected void ifData(final Consumer<T> c)
	{
		this.optData.ifPresent(c);
	}
	
	protected abstract void updateFrom(final T data);
	
	public void updateFromAndBind(final T data)
	{
		this.optData = Optional.empty();
		this.updateFrom(data);
		this.optData = Optional.of(data);
		this.afterUpdateFromAndBind(data);
	}
	
	protected void afterUpdateFromAndBind(final T data)
	{
	}
	
	@Override
	public void dispose()
	{
	}
	
	protected JPanel addWithVerticalLayout(final JComponent... components)
	{
		final JPanel panel = wrapInVerticalLayout(VerticalFlowLayout::new, components);
		this.add(panel);
		return panel;
	}
	
	protected static JPanel wrapInVerticalLayout(
		final Supplier<VerticalFlowLayout> verticalFlowLayoutSupplier,
		final JComponent... components)
	{
		final JPanel panel = new JPanel();
		panel.setLayout(verticalFlowLayoutSupplier.get());
		Stream.of(components).forEach(panel::add);
		return panel;
	}
	
	@SuppressWarnings("checkstyle:MagicNumber")
	protected static JLabel header(final String text)
	{
		final JLabel lbl = new JLabel(text);
		Font font = lbl.getFont().deriveFont(lbl.getFont().getSize() * 1.2f);
		// Make bold
		font = font.deriveFont(font.getStyle() | Font.BOLD);
		lbl.setFont(font);
		return lbl;
	}
}
