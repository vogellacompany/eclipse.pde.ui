/*******************************************************************************
 * Copyright (c) 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

import org.eclipse.pde.api.tools.annotations.NoExtend;

/**
 * Test unsupported @NoExtend tag on a field in an annotation in the default package
 */
public @interface test2 {
	/**
	 */
	@NoExtend
	public Object f1 = null;
}
