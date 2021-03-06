///*
// * Copyright (C) 2014 Universitat Pompeu Fabra
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package org.gwaspi.operations.combi;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * TODO
// */
//class EncodingSamplesFeaturesStorage extends AbstractSamplesFeaturesStorage<byte[]> {
//
//	private final GenotypeEncoder encoder;
//	private final SamplesFeaturesStorage<Float> receiver;
//	private int currentMarkerIndex;
//	private final List<byte[]> currentValues;
//
//	public EncodingSamplesFeaturesStorage(int numSamples, int numFeatures, SamplesFeaturesStorage<Float> receiver, GenotypeEncoder encoder) {
//		super(numSamples, numFeatures);
//
//		this.encoder = encoder;
//		this.receiver = receiver;
//		this.currentValues = new ArrayList<byte[]>(numSamples);
//	}
//
//	private static void throwSampleStorageNotSupported() {
//		throw new UnsupportedOperationException("We need marker by marker; can not deal with sample by sample");
//	}
//
//	@Override
//	public void startStoringSample(int sampleIndex) {
//		throwSampleStorageNotSupported();
//	}
//
//	@Override
//	public void setFeatureValue(int markerIndex, byte[] value) {
//		throwSampleStorageNotSupported();
//	}
//
//	@Override
//	public void endStoringSample() {
//		throwSampleStorageNotSupported();
//	}
//
//	@Override
//	public void startStoringFeature(int markerIndex) {
//
//		currentMarkerIndex = markerIndex;
//		currentValues.clear();
//	}
//
//	@Override
//	public void setSampleValue(int sampleIndex, byte[] value) {
//		currentValues.set(sampleIndex, value);
//	}
//
//	@Override
//	public void endStoringFeature() {
//		encoder.encodeGenotypes(currentValues, receiver, currentMarkerIndex);
//	}
//}
