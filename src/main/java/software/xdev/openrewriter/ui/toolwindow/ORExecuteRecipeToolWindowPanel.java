package software.xdev.openrewriter.ui.toolwindow;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.util.ui.JBUI;


public class ORExecuteRecipeToolWindowPanel extends JPanel implements DumbAware
{
	public static final String TITLE = "Execute Recipe";
	
	private static final Logger LOG = Logger.getInstance(ORExecuteRecipeToolWindowPanel.class);
	
	public ORExecuteRecipeToolWindowPanel()
	{
		super(new BorderLayout());
		
		this.setBorder(JBUI.Borders.empty(1));
		
		this.add(new JLabel("TEST"));
	}
}
