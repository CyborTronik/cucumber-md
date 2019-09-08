package com.github.open_fun.specs.beans;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class FeatureDescription {
    private String title;
    private String description;
    private List<String> tags;
    private List<ScenarioDescription> scenarios;

    public void mergeScenarioDescription(ScenarioDescription scenarioDescription) {
        scenarios.stream().filter(x -> x.getTitle().equals(scenarioDescription.getTitle())).findFirst()
                .ifPresent(s -> s.merge(scenarioDescription));
    }

    public void mergeStepToScenario(String scenarioTitle, StepDescription stepDescription) {
        scenarios.stream().filter(x -> x.getTitle().equals(scenarioTitle)).findFirst()
                .ifPresent(s -> s.mergeStep(stepDescription));
    }
}
