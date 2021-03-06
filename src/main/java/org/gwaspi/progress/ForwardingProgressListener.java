/*
 * Copyright (C) 2014 Universitat Pompeu Fabra
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gwaspi.progress;

/**
 * Forwards all events to the inner progress listener.
 * It makes no sense to use this as-is.
 * Only classes derived from this one should actually be used.
 * @param <S> the status type
 */
public class ForwardingProgressListener<S> implements ProgressListener<S> {

	private final ProgressListener<S> innerListener;

	protected ForwardingProgressListener(final ProgressListener<S> innerListener) {

		this.innerListener = innerListener;
	}

	protected ProgressListener<S> getInnerListener() {
		return innerListener;
	}

	@Override
	public void processDetailsChanged(ProcessDetailsChangeEvent evt) {
		innerListener.processDetailsChanged(evt);
	}

	@Override
	public void statusChanged(ProcessStatusChangeEvent evt) {
		innerListener.statusChanged(evt);
	}

	@Override
	public void progressHappened(ProgressEvent<S> evt) {
		innerListener.progressHappened(evt);
	}
}
