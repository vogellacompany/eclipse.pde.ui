/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Code 9 Corporation - ongoing enhancements
 *******************************************************************************/
package org.eclipse.pde.internal.ui.preferences;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.*;
import org.eclipse.pde.core.plugin.IPluginExtensionPoint;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.ui.*;
import org.eclipse.pde.internal.ui.launcher.OSGiFrameworkManager;
import org.eclipse.pde.internal.ui.search.ShowDescriptionAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;

/**
 * Provides the preference page for managing the default OSGi framework to use.
 * 
 * @since 3.3
 */
public class OSGiFrameworkPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Label provider for the table viewer. Annotates the default framework with bold text 
	 */
	class FrameworkLabelProvider extends LabelProvider implements IFontProvider {
		private Font font = null;

		public Image getImage(Object element) {
			return PDEPluginImages.get(PDEPluginImages.OBJ_DESC_BUNDLE);
		}

		public String getText(Object element) {
			if (element instanceof IConfigurationElement) {
				String name = ((IConfigurationElement) element).getAttribute(OSGiFrameworkManager.ATT_NAME);
				String id = ((IConfigurationElement) element).getAttribute(OSGiFrameworkManager.ATT_ID);
				return fDefaultFramework.equals(id) ? name + " " + PDEUIMessages.OSGiFrameworkPreferencePage_default : name; //$NON-NLS-1$
			}
			return super.getText(element);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
		 */
		public Font getFont(Object element) {
			if (element instanceof IConfigurationElement) {
				String id = ((IConfigurationElement) element).getAttribute(OSGiFrameworkManager.ATT_ID);
				if (fDefaultFramework.equals(id)) {
					if (this.font == null) {
						Font dialogFont = JFaceResources.getDialogFont();
						FontData[] fontData = dialogFont.getFontData();
						for (int i = 0; i < fontData.length; i++) {
							FontData data = fontData[i];
							data.setStyle(SWT.BOLD);
						}
						Display display = getControl().getShell().getDisplay();
						this.font = new Font(display, fontData);
					}
					return this.font;
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
		 */
		public void dispose() {
			if (this.font != null) {
				this.font.dispose();
			}
			super.dispose();
		}
	}

	private CheckboxTableViewer fTableViewer;
	private String fDefaultFramework;

	/**
	 * Constructor
	 */
	public OSGiFrameworkPreferencePage() {
		setDefaultFramework();
	}

	/**
	 * Restores the default framework setting from the PDE preferences
	 */
	private void setDefaultFramework() {
		IPreferenceStore store = PDEPlugin.getDefault().getPreferenceStore();
		fDefaultFramework = store.getString(IPreferenceConstants.DEFAULT_OSGI_FRAMEOWRK);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite comp = SWTFactory.createComposite(parent, 2, 1, GridData.FILL_BOTH);

		Link text = new Link(comp, SWT.WRAP);
		text.setText(PDEUIMessages.OSGiFrameworkPreferencePage_installed);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		text.setLayoutData(gd);
		text.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IPluginExtensionPoint point = PDECore.getDefault().getExtensionsRegistry().findExtensionPoint(OSGiFrameworkManager.POINT_ID);
				if (point != null) {
					new ShowDescriptionAction(point, true).run();
				} else {
					Display.getDefault().beep();
				}
			}
		});

		fTableViewer = new CheckboxTableViewer(new Table(comp, SWT.CHECK | SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION));
		fTableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		fTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		fTableViewer.setLabelProvider(new FrameworkLabelProvider());
		fTableViewer.setInput(PDEPlugin.getDefault().getOSGiFrameworkManager().getSortedFrameworks());
		fTableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				IConfigurationElement element = (IConfigurationElement) event.getElement();
				fTableViewer.setCheckedElements(new Object[] {element});
				fDefaultFramework = element.getAttribute(OSGiFrameworkManager.ATT_ID);
				fTableViewer.refresh();
			}
		});
		if (fDefaultFramework != null) {
			IConfigurationElement element = PDEPlugin.getDefault().getOSGiFrameworkManager().getFramework(fDefaultFramework);
			if (element != null) {
				fTableViewer.setCheckedElements(new Object[] {element});
			}
		}
		Dialog.applyDialogFont(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IHelpContextIds.OSGI_PREFERENCE_PAGE);
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore store = PDEPlugin.getDefault().getPreferenceStore();
		store.setValue(IPreferenceConstants.DEFAULT_OSGI_FRAMEOWRK, fDefaultFramework);
		PDEPlugin.getDefault().getPreferenceManager().savePluginPreferences();
		return super.performOk();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		setDefaultFramework();
		fTableViewer.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}