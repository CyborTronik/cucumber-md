package com.github.open_fun.specs.beans;

import java.util.LinkedList;
import java.util.List;

public class Specs {
    private final List<FeatureDescription> features = new LinkedList<>();

    public void addFeature(FeatureDescription featureDescription) {
        features.add(featureDescription);
    }

    public void mergeScenarioDescription(ScenarioDescription scenarioDescription) {
        for (FeatureDescription feature : features) {
            feature.mergeScenarioDescription(scenarioDescription);
        }
    }

    public void mergeStepToScenario(String scenarioTitle, StepDescription stepDescription) {
        for (FeatureDescription feature : features) {
            feature.mergeStepToScenario(scenarioTitle, stepDescription);
        }
    }

    public List<FeatureDescription> getFeatures() {
        return features;
    }
}
