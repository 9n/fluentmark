/*******************************************************************************
 * Copyright (c) 2016 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.fluentmark.outline.operations;

import org.eclipse.core.runtime.CoreException;

/**
 * @author David Green
 */
public interface CommandManager {

	/**
	 * perform the given command
	 * 
	 * @param command
	 *            the command to perform
	 * @throws CoreException
	 */
	public void perform(AbstractDocumentCommand command) throws CoreException;
}