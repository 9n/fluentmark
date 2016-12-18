package net.certiv.fluentmark.editor;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

public class AnnotationIterator implements Iterator<Annotation> {

	private Iterator<Annotation> fIterator;
	private Annotation fNext;
	private boolean fSkipIrrelevants;
	private boolean fReturnAllAnnotations;

	public AnnotationIterator(IAnnotationModel model, boolean skipIrrelevants) {
		this(model, skipIrrelevants, false);
	}

	/**
	 * Returns a new AnnotationIterator.
	 * 
	 * @param parent the parent iterator to iterate over annotations
	 * @param returnAllAnnotations whether to return all annotations or just problem annotations
	 */
	public AnnotationIterator(Iterator<Annotation> parent, boolean returnAllAnnotations) {
		fReturnAllAnnotations = returnAllAnnotations;
		fIterator = parent;
		fSkipIrrelevants = true;
		skip();
	}

	/**
	 * Returns a new AnnotationIterator.
	 * 
	 * @param parent the parent iterator to iterate over annotations
	 * @param returnAllAnnotations whether to return all annotations or just problem annotations
	 */
	@SuppressWarnings("unchecked")
	public AnnotationIterator(IAnnotationModel model, boolean skipIrrelevants, boolean returnAllAnnotations) {
		fReturnAllAnnotations = returnAllAnnotations;
		if (model != null) {
			fIterator = model.getAnnotationIterator();
		} else {
			fIterator = Collections.EMPTY_LIST.iterator();
		}
		fSkipIrrelevants = skipIrrelevants;
		skip();
	}

	private void skip() {
		while (fIterator.hasNext()) {
			Annotation next = fIterator.next();
			if (next instanceof IAnnotation) {
				if (fSkipIrrelevants) {
					if (!next.isMarkedDeleted()) {
						fNext = next;
						return;
					}
				} else {
					fNext = next;
					return;
				}
			} else if (fReturnAllAnnotations) {
				fNext = next;
				return;
			}
		}
		fNext = null;
	}

	public boolean hasNext() {
		return fNext != null;
	}

	public Annotation next() {
		try {
			return fNext;
		} finally {
			skip();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}