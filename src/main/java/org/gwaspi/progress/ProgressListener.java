/*
 * Copyright (C) 2013 Universitat Pompeu Fabra
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

import java.util.EventListener;

/**
 * Is interested in {@link ProgressEvent}'s of (a) processes(es).
 * @param <S> the status type
 */
public interface ProgressListener<S> extends EventListener {

	/**
	 * Signals that the details of the process changed.
	 * This might happen, in case the total length of the process
	 * was previously unknown, but is now known.
	 * @param evt describes the change in the process details
	 */
	void processDetailsChanged(ProcessDetailsChangeEvent evt);

	/**
	 * Signals that the process status changed.
	 * @param evt describes the status change
	 */
	void statusChanged(ProcessStatusChangeEvent evt);

//	/**
//	 * Signals that the process started with the initialization phase,
//	 * if there is one.
//	 */
//	void processStarted();
//
//	/**
//	 * Signals that the main process phase started.
//	 */
//	void processInitialized();

	/**
	 * Signals that the process advanced.
	 * @param evt contains details about the current state of progress.
	 */
	void progressHappened(ProgressEvent<S> evt);

//	/**
//	 * Signals that the main phase of the process ended.
//	 */
//	void processEnded();
//
//	/**
//	 * Signals that the process finished completely.
//	 */
//	void processFinalized();
}
