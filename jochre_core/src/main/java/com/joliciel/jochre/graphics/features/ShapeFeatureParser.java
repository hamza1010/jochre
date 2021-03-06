package com.joliciel.jochre.graphics.features;

import java.util.ArrayList;
import java.util.List;

import com.joliciel.jochre.graphics.ShapeWrapper;
import com.joliciel.talismane.machineLearning.features.AbstractFeatureParser;
import com.joliciel.talismane.machineLearning.features.Feature;
import com.joliciel.talismane.machineLearning.features.FeatureClassContainer;
import com.joliciel.talismane.machineLearning.features.FeatureSyntaxException;
import com.joliciel.talismane.machineLearning.features.FunctionDescriptor;

/**
 * A parser for shape features.
 * 
 * @author Assaf Urieli
 *
 */
public class ShapeFeatureParser extends AbstractFeatureParser<ShapeWrapper> {
	private FeatureClassContainer container;

	public ShapeFeatureParser() {
		super();
	}

	@Override
	public void addFeatureClasses(FeatureClassContainer container) {
		container.addFeatureClass("TouchesBaseLine", TouchesBaseLineFeature.class);
		container.addFeatureClass("TouchesMeanLine", TouchesMeanLineFeature.class);
		container.addFeatureClass("ThinRow", ThinRowFeature.class);
		container.addFeatureClass("VerticalSize", VerticalSizeFeature.class);
		container.addFeatureClass("VerticalElongation", VerticalElongationFeature.class);
		container.addFeatureClass("BaselineDistance", BaselineDistanceFeature.class);
		container.addFeatureClass("SectionRelativeBrightness", SectionRelativeBrightnessFeature.class);
		container.addFeatureClass("SectionRelativeBrightnessNoMargins", SectionRelativeBrightnessNoMarginsFeature.class);
		container.addFeatureClass("LowerRighthandGap", LowerRighthandGapFeature.class);
		container.addFeatureClass("ChupchikLowerRight", ChupchikLowerRightFeature.class);
		container.addFeatureClass("InnerEmptyChupchikLowerLeft", InnerEmptyChupchikLowerLeftFeature.class);
		container.addFeatureClass("LowerRightCornerFull", LowerRightCornerFullFeature.class);
		container.addFeatureClass("SmallChupchikRightNearTop", SmallChupchikRightNearTopFeature.class);
		container.addFeatureClass("UpperLefthandOpening", UpperLefthandOpeningFeature.class);
		container.addFeatureClass("LowerLefthandOpening", LowerLefthandOpeningFeature.class);
		container.addFeatureClass("OpeningOnTop", OpeningOnTopFeature.class);
		container.addFeatureClass("OpeningOnBottom", OpeningOnBottomFeature.class);
		container.addFeatureClass("Height", HeightFeature.class);
		container.addFeatureClass("Width", WidthFeature.class);

		this.container = container;
	}

	@Override
	public List<FunctionDescriptor> getModifiedDescriptors(FunctionDescriptor functionDescriptor) {
		List<FunctionDescriptor> descriptors = new ArrayList<FunctionDescriptor>();
		String functionName = functionDescriptor.getFunctionName();

		@SuppressWarnings("rawtypes")
		List<Class<? extends Feature>> featureClasses = container.getFeatureClasses(functionName);

		@SuppressWarnings("rawtypes")
		Class<? extends Feature> featureClass = null;
		if (featureClasses != null && featureClasses.size() > 0)
			featureClass = featureClasses.get(0);

		if (functionName.equalsIgnoreCase("SectionRelativeBrightnessGrid")) {
			if (!(functionDescriptor.getArguments().get(0).getObject() instanceof Integer))
				throw new FeatureSyntaxException(functionName + " argument 1 must be a whole number", functionDescriptor, functionDescriptor);
			if (!(functionDescriptor.getArguments().get(1).getObject() instanceof Integer))
				throw new FeatureSyntaxException(functionName + " argument 2 must be a whole number", functionDescriptor, functionDescriptor);
			if (!(functionDescriptor.getArguments().get(2).getObject() instanceof Double))
				throw new FeatureSyntaxException(functionName + " argument 3 must be a decimal number", functionDescriptor, functionDescriptor);
			if (!(functionDescriptor.getArguments().get(3).getObject() instanceof Double))
				throw new FeatureSyntaxException(functionName + " argument 4 must be a decimal number", functionDescriptor, functionDescriptor);

			int verticalSections = ((Integer) functionDescriptor.getArguments().get(0).getObject()).intValue();
			int horizontalSections = ((Integer) functionDescriptor.getArguments().get(1).getObject()).intValue();
			String newFunctionName = "SectionRelativeBrightness";
			for (int x = 0; x < verticalSections; x++) {
				for (int y = 0; y < horizontalSections; y++) {
					FunctionDescriptor descriptor = new FunctionDescriptor(newFunctionName);
					descriptor.addArgument(x);
					descriptor.addArgument(y);
					descriptor.addArgument(verticalSections);
					descriptor.addArgument(horizontalSections);
					descriptor.addArgument(functionDescriptor.getArguments().get(2));
					descriptor.addArgument(functionDescriptor.getArguments().get(3));
					descriptors.add(descriptor);
				}
			}
		} else if (functionName.equalsIgnoreCase("SectionRelativeBrightnessNoMarginsGrid")) {
			if (!(functionDescriptor.getArguments().get(0).getObject() instanceof Integer))
				throw new FeatureSyntaxException(functionName + " argument 1 must be a whole number", functionDescriptor, functionDescriptor);
			if (!(functionDescriptor.getArguments().get(1).getObject() instanceof Integer))
				throw new FeatureSyntaxException(functionName + " argument 2 must be a whole number", functionDescriptor, functionDescriptor);

			int verticalSections = ((Integer) functionDescriptor.getArguments().get(0).getObject()).intValue();
			int horizontalSections = ((Integer) functionDescriptor.getArguments().get(1).getObject()).intValue();
			String newFunctionName = "SectionRelativeBrightnessNoMargins";
			for (int x = 0; x < verticalSections; x++) {
				for (int y = 0; y < horizontalSections; y++) {
					FunctionDescriptor descriptor = new FunctionDescriptor(newFunctionName);
					descriptor.addArgument(x);
					descriptor.addArgument(y);
					descriptor.addArgument(verticalSections);
					descriptor.addArgument(horizontalSections);
					descriptors.add(descriptor);
				}
			}
		} else if (featureClass == null) {
			// do nothing
		}

		return descriptors;
	}

	@Override
	public void injectDependencies(@SuppressWarnings("rawtypes") Feature feature) {
	}

	@Override
	protected boolean canConvert(Class<?> parameterType, Class<?> originalArgumentType) {
		return false;
	}

	@Override
	protected Feature<ShapeWrapper, ?> convertArgument(Class<?> parameterType, Feature<ShapeWrapper, ?> originalArgument) {
		return null;
	}

	@Override
	public Feature<ShapeWrapper, ?> convertFeatureCustomType(Feature<ShapeWrapper, ?> feature) {
		return null;
	}

}
