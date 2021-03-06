/*******************************************************************************
 * Copyright (c) 2016 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.fluentmark;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Nodeclipse Log Util
 * 
 * @author Lamb Gao, Paul Verest
 */
public class Log {

	public static IStatus createStatus(int severity, int code, String message, Throwable exception) {
		return new Status(severity, FluentMkUI.PLUGIN_ID, code, message, exception);
	}

	public static void info(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}

	public static void error(Throwable exception) {
		error("Unexpected Exception", exception);
	}

	public static void error(String message) {
		error(message, null);
	}

	public static void error(String message, Throwable exception) {
		log(IStatus.ERROR, IStatus.ERROR, message, exception);
	}

	public static void error(int code, String message, Throwable exception) {
		log(createStatus(IStatus.ERROR, code, message, exception));
	}

	public static void log(int severity, int code, String message, Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}

	public static void log(IStatus status) {
		ILog log = FluentMkUI.getDefault().getLog();
		log.log(status);
	}
}
