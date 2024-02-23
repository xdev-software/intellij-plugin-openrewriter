package software.xdev.openrewriter.ui.toolwindow.execute;

import java.awt.Font;
import java.util.Optional;
import java.util.function.Consumer;

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
	}
	
	@Override
	public void dispose()
	{
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
