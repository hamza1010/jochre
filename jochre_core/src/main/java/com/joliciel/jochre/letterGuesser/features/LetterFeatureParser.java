///////////////////////////////////////////////////////////////////////////////
//Copyright (C) 2012 Assaf Urieli
//
//This file is part of Jochre.
//
//Jochre is free software: you can redistribute it and/or modify
//it under the terms of the GNU Affero General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//Jochre is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU Affero General Public License for more details.
//
//You should have received a copy of the GNU Affero General Public License
//along with Jochre.  If not, see <http://www.gnu.org/licenses/>.
//////////////////////////////////////////////////////////////////////////////
package com.joliciel.jochre.letterGuesser.features;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joliciel.jochre.boundaries.features.ShapeInSequenceFeatureParser;
import com.joliciel.jochre.graphics.features.ShapeFeatureParser;
import com.joliciel.jochre.letterGuesser.LetterGuesserContext;
import com.joliciel.talismane.machineLearning.features.AbstractFeature;
import com.joliciel.talismane.machineLearning.features.AbstractFeatureParser;
import com.joliciel.talismane.machineLearning.features.BooleanFeature;
import com.joliciel.talismane.machineLearning.features.DoubleFeature;
import com.joliciel.talismane.machineLearning.features.Feature;
import com.joliciel.talismane.machineLearning.features.FeatureClassContainer;
import com.joliciel.talismane.machineLearning.features.FeatureResult;
import com.joliciel.talismane.machineLearning.features.FeatureWrapper;
import com.joliciel.talismane.machineLearning.features.FunctionDescriptor;
import com.joliciel.talismane.machineLearning.features.FunctionDescriptorParser;
import com.joliciel.talismane.machineLearning.features.IntegerFeature;
import com.joliciel.talismane.machineLearning.features.RuntimeEnvironment;
import com.joliciel.talismane.machineLearning.features.StringFeature;

public class LetterFeatureParser extends AbstractFeatureParser<LetterGuesserContext> {
	private static final Logger LOG = LoggerFactory.getLogger(LetterFeatureParser.class);

	private final ShapeFeatureParser shapeFeatureParser;
	private final ShapeInSequenceFeatureParser shapeInSequenceFeatureParser;

	public LetterFeatureParser() {
		super();
		this.shapeFeatureParser = new ShapeFeatureParser();
		this.shapeInSequenceFeatureParser = new ShapeInSequenceFeatureParser();
	}

	public Set<LetterFeature<?>> getLetterFeatureSet(List<String> featureDescriptors) {
		Set<LetterFeature<?>> features = new TreeSet<LetterFeature<?>>();
		FunctionDescriptorParser descriptorParser = new FunctionDescriptorParser();

		for (String featureDescriptor : featureDescriptors) {
			LOG.trace(featureDescriptor);
			if (featureDescriptor.length() > 0 && !featureDescriptor.startsWith("#")) {
				FunctionDescriptor functionDescriptor = descriptorParser.parseDescriptor(featureDescriptor);
				List<LetterFeature<?>> myFeatures = this.parseDescriptor(functionDescriptor);
				features.addAll(myFeatures);

			}
		}
		return features;
	}

	@Override
	public void addFeatureClasses(FeatureClassContainer container) {
		container.addFeatureClass("Ngram", NgramFeature.class);

		shapeFeatureParser.addFeatureClasses(container);
		shapeInSequenceFeatureParser.addFeatureClasses(container);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<LetterFeature<?>> parseDescriptor(FunctionDescriptor functionDescriptor) {
		List<Feature<LetterGuesserContext, ?>> letterFeatures = this.parse(functionDescriptor);
		List<LetterFeature<?>> wrappedFeatures = new ArrayList<LetterFeature<?>>();
		for (Feature<LetterGuesserContext, ?> letterFeature : letterFeatures) {
			LetterFeature<?> wrappedFeature = null;
			if (letterFeature instanceof LetterFeature) {
				wrappedFeature = (LetterFeature<?>) letterFeature;
			} else if (letterFeature instanceof BooleanFeature) {
				wrappedFeature = new LetterBooleanFeatureWrapper((Feature<LetterGuesserContext, Boolean>) letterFeature);
			} else if (letterFeature instanceof StringFeature) {
				wrappedFeature = new LetterStringFeatureWrapper((Feature<LetterGuesserContext, String>) letterFeature);
			} else if (letterFeature instanceof IntegerFeature) {
				wrappedFeature = new LetterIntegerFeatureWrapper((Feature<LetterGuesserContext, Integer>) letterFeature);
			} else if (letterFeature instanceof DoubleFeature) {
				wrappedFeature = new LetterDoubleFeatureWrapper((Feature<LetterGuesserContext, Double>) letterFeature);
			} else {
				wrappedFeature = new LetterFeatureWrapper(letterFeature);
			}
			wrappedFeatures.add(wrappedFeature);
		}
		return wrappedFeatures;
	}

	@Override
	public List<FunctionDescriptor> getModifiedDescriptors(FunctionDescriptor functionDescriptor) {
		List<FunctionDescriptor> modifiedDescriptors = this.shapeFeatureParser.getModifiedDescriptors(functionDescriptor);
		if (modifiedDescriptors == null || modifiedDescriptors.size() == 0)
			modifiedDescriptors = this.shapeInSequenceFeatureParser.getModifiedDescriptors(functionDescriptor);
		return modifiedDescriptors;
	}

	private static class LetterFeatureWrapper<T> extends AbstractFeature<LetterGuesserContext, T>
			implements LetterFeature<T>, FeatureWrapper<LetterGuesserContext, T> {
		private Feature<LetterGuesserContext, T> wrappedFeature = null;

		public LetterFeatureWrapper(Feature<LetterGuesserContext, T> wrappedFeature) {
			super();
			this.wrappedFeature = wrappedFeature;
			this.setName(wrappedFeature.getName());
		}

		@Override
		public FeatureResult<T> check(LetterGuesserContext context, RuntimeEnvironment env) {
			return wrappedFeature.check(context, env);
		}

		@Override
		public Feature<LetterGuesserContext, T> getWrappedFeature() {
			return this.wrappedFeature;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Class<? extends Feature> getFeatureType() {
			return wrappedFeature.getFeatureType();
		}
	}

	private class LetterBooleanFeatureWrapper extends LetterFeatureWrapper<Boolean>implements BooleanFeature<LetterGuesserContext> {
		public LetterBooleanFeatureWrapper(Feature<LetterGuesserContext, Boolean> wrappedFeature) {
			super(wrappedFeature);
		}
	}

	private class LetterStringFeatureWrapper extends LetterFeatureWrapper<String>implements StringFeature<LetterGuesserContext> {
		public LetterStringFeatureWrapper(Feature<LetterGuesserContext, String> wrappedFeature) {
			super(wrappedFeature);
		}
	}

	private class LetterDoubleFeatureWrapper extends LetterFeatureWrapper<Double>implements DoubleFeature<LetterGuesserContext> {
		public LetterDoubleFeatureWrapper(Feature<LetterGuesserContext, Double> wrappedFeature) {
			super(wrappedFeature);
		}
	}

	private class LetterIntegerFeatureWrapper extends LetterFeatureWrapper<Integer>implements IntegerFeature<LetterGuesserContext> {
		public LetterIntegerFeatureWrapper(Feature<LetterGuesserContext, Integer> wrappedFeature) {
			super(wrappedFeature);
		}
	}

	@Override
	public void injectDependencies(@SuppressWarnings("rawtypes") Feature feature) {
		this.shapeFeatureParser.injectDependencies(feature);
		this.shapeInSequenceFeatureParser.injectDependencies(feature);
	}

	@Override
	protected boolean canConvert(Class<?> parameterType, Class<?> originalArgumentType) {
		return false;
	}

	@Override
	protected Feature<LetterGuesserContext, ?> convertArgument(Class<?> parameterType, Feature<LetterGuesserContext, ?> originalArgument) {
		return null;
	}

	@Override
	public Feature<LetterGuesserContext, ?> convertFeatureCustomType(Feature<LetterGuesserContext, ?> feature) {
		return null;
	}
}
