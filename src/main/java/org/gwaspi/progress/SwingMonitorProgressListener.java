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

import javax.swing.ProgressMonitor;

/**
 * A simple (cheap!) way to visualize progress in a GUI environment.
 * It forwards events to {@link javax.swing.ProgressMonitor}.
 * Only supports integer based progress sources.
 * See {@link GeneralSwingProgressListener} for a more general and elegant solution.
 * @param <S> the status type
 */
public class SwingMonitorProgressListener<S> extends AbstractProgressListener<S> {

	private final ProgressMonitor progressMonitor;

	public SwingMonitorProgressListener(IntegerProgressHandler progressSource) {

		this.progressMonitor = new ProgressMonitor(
				null,
				progressSource.getInfo().getShortName(),
				progressSource.getInfo().getDescription(),
				progressSource.getStartState(),
				progressSource.getEndState()
		);
	}

	@Override
	public void progressHappened(ProgressEvent<S> evt) {
		progressMonitor.setProgress((Integer) evt.getCurrentState());
	}
}
