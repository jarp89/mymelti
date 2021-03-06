package com.chico.esiuclm.melti.gui.dialogs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class QualifyDialog extends Dialog {
	
	private Label label;
	private Button browse_button;
	private FileDialog fdialog;
	private Text input_file, input_grade, input_text;
	
	private String code, file_path;
	private String comments, grade;
	IFile file;
	
	public QualifyDialog(Shell parentShell) {
		super(parentShell);
		fdialog = new FileDialog(parentShell, SWT.OPEN);
		fdialog.setFilterExtensions(new String [] {"*.java"});
		fdialog.setFilterPath("C:\\");
		code = "";
		// Captura el fichero abierto en el editor activo
		if (getActivePart() != null) {
			if (getActiveEditorInput() != null) 
				file = (IFile) getActiveEditorInput().getAdapter(IFile.class);
			else file = null;
		}
	}
	
	protected Control createDialogArea(Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		
		// Situa el dialog en el fichero activo
		if (file!=null) fdialog.setFileName(file.getLocation().toOSString());
		else fdialog.setFileName("C:\\");
		
		GridLayout layout = new GridLayout();
		layout.marginHeight = 20;
		layout.marginWidth = 25;
		layout.verticalSpacing = 10;
		layout.numColumns = 3;
		container.setLayout(layout);
		
		label = new Label(container, SWT.LEFT);
		label.setText("Selecciona un fichero: ");
		input_file = new Text(container, SWT.SINGLE | SWT.BORDER);
		if (file!=null) input_file.setText(file.getLocation().toOSString());
		input_file.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse_button = new Button(container, SWT.PUSH);
		browse_button.setText("Buscar...");
		browse_button.setToolTipText("Selecciona el fichero corregido");
		browse_button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				file_path = fdialog.open();
				BufferedReader br = null;
				if (file_path != null) { // El profesor abrio un fichero para subirlo, recoge el codigo
					try {
						input_file.setText(file_path);
						String currentLine;
						br = new BufferedReader(new FileReader(file_path));
						while ((currentLine = br.readLine()) != null) {
							code += currentLine+"\n";
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						try {
							if (br != null)
								br.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		label = new Label(container, SWT.LEFT);
		label.setText("Calificación: ");
		input_grade = new Text(container, SWT.SINGLE | SWT.BORDER);
		input_grade.setToolTipText("Ingresa un valor entre 0 y 10");
		new Label(container, SWT.NONE);
		
		label = new Label(container, SWT.TOP);
		label.setText("Comentarios: ");
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		input_text = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		input_text.setLayoutData(new GridData(GridData.FILL_BOTH));
		input_text.setToolTipText("Ingresa comentarios sobre el ejercicio resuelto");
		
		return container;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Calificar solución");
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(640, 480);
	}
	
	@Override
	protected void okPressed() {
		grade = input_grade.getText();
		comments = input_text.getText();
		if(code.equals("")) { // Si no capturó el codigo aun..
			if(file!=null) {
				try {
					code = IOUtils.toString(file.getContents(), file.getCharset());
				} catch (IOException | CoreException e) {
					e.printStackTrace();
				}
			}
		}
		this.close();
	}
	
	public String getCode() {
		return code;
	}
	
	public String getComments() {
		return comments;
	}
	
	public String getFilePath() {
		if (file!=null) 
			return file.getLocation().toOSString();
		else 
			return file_path;
	}
	
	public String getGrade() {
		return grade;
	}
	
	private static IWorkbenchPart getActivePart() {
		final IWorkbench bench = PlatformUI.getWorkbench();
		if (bench == null) return null;
		final IWorkbenchWindow window = bench.getActiveWorkbenchWindow();
		if (window == null) return null;
		final IWorkbenchPage page = window.getActivePage();
		if (page == null) return null;
		return page.getActivePart();
	}
	
	private static IEditorInput getActiveEditorInput() {
		final IWorkbenchPartSite  site = getActivePart().getSite();
		if (site == null) return null;
		final IWorkbenchPage page = site.getPage();
		if (page == null) return null;
		final IEditorPart editor = page.getActiveEditor();
		if (editor == null) return null;
		return editor.getEditorInput();
		
	}

}
