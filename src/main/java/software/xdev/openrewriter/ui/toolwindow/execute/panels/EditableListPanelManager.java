package software.xdev.openrewriter.ui.toolwindow.execute.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;


public class EditableListPanelManager<T extends Comparable<T>>
{
	private Set<T> data = new HashSet<>();
	private final SortedListModel model = new SortedListModel();
	private final JBList<T> jbList;
	private final JPanel panel;
	private final String titleAdd;
	private final String titleEdit;
	private final String addOrEditMessage;
	private final String addInitialValue;
	private final InputValidator inputValidator;
	private final Function<T, String> toPresentation;
	private final Function<String, T> toModel;
	
	@SuppressWarnings("java:S107")
	public EditableListPanelManager(
		final String title,
		final String emptyText,
		final String titleAdd,
		final String titleEdit,
		final String addOrEditMessage,
		final String addInitialValue,
		final Function<T, String> toPresentation,
		final Function<String, T> toModel,
		final InputValidator inputValidator)
	{
		this.titleAdd = titleAdd;
		this.titleEdit = titleEdit;
		this.addOrEditMessage = addOrEditMessage;
		this.addInitialValue = addInitialValue;
		this.toPresentation = toPresentation;
		this.toModel = toModel;
		this.inputValidator = inputValidator;
		
		this.jbList = new JBList<>(this.model);
		this.jbList.setVisibleRowCount(4);
		this.jbList.setCellRenderer(new DefaultListCellRenderer()
		{
			@SuppressWarnings("unchecked")
			@Override
			public Component getListCellRendererComponent(
				final JList<?> list,
				final Object value,
				final int index,
				final boolean isSelected,
				final boolean cellHasFocus)
			{
				return super.getListCellRendererComponent(
					list,
					toPresentation.apply((T)value),
					index,
					isSelected,
					cellHasFocus);
			}
		});
		Stream.of(emptyText.split("\\n"))
			.forEach(this.jbList.getEmptyText()::appendLine);
		
		this.panel = ToolbarDecorator.createDecorator(this.jbList)
			.setAddAction(this.getAddActionButtonRunnable())
			.setEditAction(this.getEditActionButtonRunnable())
			.setRemoveAction(this.getRemoveActionButtonRunnable())
			.disableUpDownActions()
			.createPanel();
		
		this.panel.setMinimumSize(new Dimension(10, 10));
		this.panel.setBorder(IdeBorderFactory.createTitledBorder(title, false));
	}
	
	private AnActionButtonRunnable getAddActionButtonRunnable()
	{
		return actionButton -> Optional.ofNullable(Messages.showInputDialog(
				this.addOrEditMessage,
				this.titleAdd,
				null,
				this.addInitialValue,
				this.inputValidator))
			.map(this.toModel)
			.filter(this.getData()::add)
			.ifPresent(this.model::addElementSorted);
	}
	
	private AnActionButtonRunnable getEditActionButtonRunnable()
	{
		return actionButton -> {
			final T oldValue = this.jbList.getSelectedValue();
			Optional.ofNullable(Messages.showInputDialog(
					this.addOrEditMessage,
					this.titleEdit,
					null,
					this.toPresentation.apply(oldValue),
					this.inputValidator))
				.map(this.toModel)
				.filter(v -> !v.equals(oldValue))
				.ifPresent(value -> {
					this.getData().remove(oldValue);
					this.model.removeElement(oldValue);
					
					if(this.getData().add(value))
					{
						this.model.addElementSorted(value);
					}
				});
		};
	}
	
	private AnActionButtonRunnable getRemoveActionButtonRunnable()
	{
		return actionButton -> {
			for(final T selectedValue : this.jbList.getSelectedValuesList())
			{
				this.getData().remove(selectedValue);
				this.model.removeElement(selectedValue);
			}
		};
	}
	
	private Set<T> getData()
	{
		return this.data;
	}
	
	public void addValueChangeListener(final Runnable runnable)
	{
		this.model.addListDataListener(new ListDataListener()
		{
			@Override
			public void intervalAdded(final ListDataEvent e)
			{
				runnable.run();
			}
			
			@Override
			public void intervalRemoved(final ListDataEvent e)
			{
				runnable.run();
			}
			
			@Override
			public void contentsChanged(final ListDataEvent e)
			{
				runnable.run();
			}
		});
	}
	
	public JPanel getPanel()
	{
		return this.panel;
	}
	
	public void update(final Set<T> data)
	{
		this.data = data;
		this.model.clear();
		this.model.addAllSorted(data);
	}
	
	class SortedListModel extends DefaultListModel<T>
	{
		@SuppressWarnings("unchecked")
		private void addElementSorted(final T element)
		{
			final Enumeration<?> modelElements = this.elements();
			int index = 0;
			while(modelElements.hasMoreElements())
			{
				final T modelElement = (T)modelElements.nextElement();
				if(0 < modelElement.compareTo(element))
				{
					this.add(index, element);
					return;
				}
				index++;
			}
			this.addElement(element);
		}
		
		private void addAllSorted(final Collection<T> elements)
		{
			for(final T element : elements)
			{
				this.addElementSorted(element);
			}
		}
	}
}
