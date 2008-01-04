/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.pde.ui.tests.util.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.pde.internal.core.XMLDefaultHandler;
import org.eclipse.pde.internal.core.util.DOMParserWrapper;
import org.eclipse.pde.internal.core.util.SAXParserWrapper;
import org.eclipse.pde.internal.ui.tests.macro.MacroPlugin;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * SAXParserWrapperTestCase
 *
 */
public class ParserWrapperTestCase extends TestCase {

	protected static final int FTHREADCOUNT = 5;
	protected static final int FSAX = 0;
	protected static final int FDOM = 1;
	protected static File fXMLFile;
	protected static final String FFILENAME = "/plugin.xml"; //$NON-NLS-1$

	public static Test suite() {
		return new TestSuite(ParserWrapperTestCase.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		MacroPlugin plugin = MacroPlugin.getDefault();
		if (plugin == null)
			throw new Exception("ERROR:  Macro plug-in uninitialized"); //$NON-NLS-1$
		Bundle bundle = plugin.getBundle();
		if (bundle == null)
			throw new Exception("ERROR:  Bundle uninitialized"); //$NON-NLS-1$
		URL url = bundle.getEntry(FFILENAME);
		if (url == null)
			throw new Exception("ERROR:  URL not found:  " + FFILENAME); //$NON-NLS-1$
		String path = FileLocator.resolve(url).getPath();
		if ("".equals(path)) //$NON-NLS-1$
			throw new Exception("ERROR:  URL unresolved:  " + FFILENAME); //$NON-NLS-1$
		fXMLFile = new File(path);
	}

	public void testSAXParserWrapperConcurrency() throws Exception {

		ParserThread[] threads = new ParserThread[FTHREADCOUNT];

		for (int x = 0; x < FTHREADCOUNT; x++) {
			threads[x] = new ParserThread(FSAX, fXMLFile);
			threads[x].start();
		}

		for (int x = 0; x < FTHREADCOUNT; x++) {
			threads[x].join();
			assertFalse(threads[x].getError());
		}

	}

	public void testDOMParserWrapperConcurrency() throws Exception {

		ParserThread[] threads = new ParserThread[FTHREADCOUNT];

		for (int x = 0; x < FTHREADCOUNT; x++) {
			threads[x] = new ParserThread(FDOM, fXMLFile); //$NON-NLS-1$
			threads[x].start();
		}

		for (int x = 0; x < FTHREADCOUNT; x++) {
			threads[x].join();
			assertFalse(threads[x].getError());
		}

	}

	public class ParserThread extends Thread {

		protected final int FITERATIONS = 100;
		protected File fXMLFile;
		protected boolean fError;
		protected int fParserType;

		public ParserThread(int parserType, File file) {
			fError = false;
			fParserType = parserType;
			fXMLFile = file;
		}

		public void run() {

			if (fParserType == ParserWrapperTestCase.FSAX) {
				runSAX();
			} else {
				runDOM();
			}

		}

		public void runSAX() {

			for (int x = 0; x < FITERATIONS; x++) {

				try {
					XMLDefaultHandler handler = new XMLDefaultHandler();
					SAXParserWrapper parser = new SAXParserWrapper();
					parser.parse(fXMLFile, handler);
					parser.dispose();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					fError = true;
				} catch (SAXException e) {
					e.printStackTrace();
					fError = true;
				} catch (FactoryConfigurationError e) {
					e.printStackTrace();
					fError = true;
				} catch (IOException e) {
					e.printStackTrace();
					fError = true;
				}
				// If an error was encountered abort the thread
				// Any type of exception experienced is bad
				if (fError)
					return;

			}

		}

		public void runDOM() {

			for (int x = 0; x < FITERATIONS; x++) {

				try {
					DOMParserWrapper parser = new DOMParserWrapper();
					parser.parse(fXMLFile);
					parser.dispose();
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					fError = true;
				} catch (SAXException e) {
					e.printStackTrace();
					fError = true;
				} catch (FactoryConfigurationError e) {
					e.printStackTrace();
					fError = true;
				} catch (IOException e) {
					e.printStackTrace();
					fError = true;
				}
				// If an error was encountered abort the thread
				// Any type of exception experienced is bad
				if (fError)
					return;

			}
		}

		public boolean getError() {
			return fError;
		}

	}

}
