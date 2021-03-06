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
 * Default implementation of ProcessInfo.
 */
public class SubProcessInfo extends DefaultProcessInfo {

	private final ProcessInfo superProcessInfo;

	public SubProcessInfo(final ProcessInfo superProcessInfo, final String shortName, final String description) {
		super(shortName, description);

		this.superProcessInfo = superProcessInfo;
	}

	public ProcessInfo getSuperProcessInfo() {
		return superProcessInfo;
	}

	@Override
	public String toString() {
		return super.toString() + " (child of '" + getSuperProcessInfo().toString() + ")";
	}
}
