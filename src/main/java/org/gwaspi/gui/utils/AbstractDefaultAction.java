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

package org.gwaspi.gui.utils;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JToggleButton;

/**
 * Allows to reset the value of a component to its default.
 * In case of a toggle-button as actuator, it also buffers the custom value,
 * and later goes back to that value.
 * @param <C> GUI container
 * @param <V> value
 */
public abstract class AbstractDefaultAction<C extends JComponent, V> extends AbstractAction {

	private final C valueComponent;
	private final boolean makeUneditable;
	private V defaultValue;
	private V customValue;

	public AbstractDefaultAction(
			final C valueComponent,
			final V defaultValue,
			final boolean makeUneditable)
	{
		this.valueComponent = valueComponent;
		this.makeUneditable = makeUneditable;
		this.defaultValue = defaultValue;
		this.customValue = getValue();

		final boolean isDefaultValue = this.customValue.equals(this.defaultValue);

		putValue(NAME, "Use default");
		putValue(SELECTED_KEY, isDefaultValue);

		setEditable(!isDefaultValue);
	}

	public AbstractDefaultAction(final C valueComponent, final V defaultValue) {
		this(valueComponent, defaultValue, false);
	}

	protected abstract void setValue(V value);

	protected abstract V getValue();

	protected abstract void setValueComponentEditable(boolean editable);

	protected C getValueComponent() {
		return valueComponent;
	}

	private void setEditable(final boolean editable) {

		if (makeUneditable) {
			setValueComponentEditable(editable);
		}
	}

	public void setDefault(final V defaultValue) {

		if (!this.defaultValue.equals(defaultValue)) {
			// check if we were on the default value ...
			final boolean onDefault = this.defaultValue.equals(getValue());
			this.defaultValue = defaultValue;
			if (onDefault) {
				// .. and if so, set the value to the new defautl value
				setValue(defaultValue);
			} else if (defaultValue.equals(getValue())) {
				// if the new default value coinsides wit the current value,
				// make sure the indicator shows that we are on the default value
				setValue(defaultValue);
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {

		if (evt.getSource() instanceof JToggleButton) {
			// if our source is a JToggleButton, JCheckBox or a JRadioButton ...
			final JToggleButton sourceToggleButton = (JToggleButton) evt.getSource();
			setEditable(!sourceToggleButton.isSelected());
			if (sourceToggleButton.isSelected()) {
				// put the custom value into a buffer,
				// if the user wants to use the default value
				customValue = getValue();
			} else {
				// or restore it from that buffer,
				// if the user wants to use a non default value again
				setValue(customValue);
				return;
			}
		}
		// else (e.g. if our source is a JButton),
		// always restore the default value

		setValue(defaultValue);
	}

	protected void valueChanged() {

		final boolean isDefaultValue = getValue().equals(defaultValue);
		putValue(SELECTED_KEY, isDefaultValue);
	}
}
