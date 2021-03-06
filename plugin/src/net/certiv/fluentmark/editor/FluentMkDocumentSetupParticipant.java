/*******************************************************************************
 * Copyright (c) 2016 Certiv Analytics and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package net.certiv.fluentmark.editor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.core.filebuffers.IDocumentSetupParticipantExtension;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;

import net.certiv.fluentmark.FluentMkUI;

/** FluentMkDocumentSetupParticipant */
public class FluentMkDocumentSetupParticipant implements IDocumentSetupParticipant, IDocumentSetupParticipantExtension {

	private FluentMkTextTools textTools;

	public FluentMkDocumentSetupParticipant() {
		this(FluentMkUI.getDefault().getTextTools());
	}

	public FluentMkDocumentSetupParticipant(FluentMkTextTools textTools) {
		this.textTools = textTools;
	}

	public void setup(IDocument document) {
		setup(document, null, null);
	}

	public void setup(IDocument document, IPath location, LocationKind locationKind) {
		textTools.setupDocument(document, location, locationKind);
	}
}
