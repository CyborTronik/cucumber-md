package com.github.open_fun.specs.beans;

import gherkin.ast.Examples;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ScenarioDescription {
    private String title;
    private String description;
    private List<String> tags;
    private List<StepDescription> steps = new LinkedList<>();
    private List<Examples> examples = new LinkedList<>();

    public String getTitle() {
        return title;
    }

    public ScenarioDescription withTitle(String title) {
        this.title = title;
        return this;
    }

    public ScenarioDescription withDescription(String description) {
        this.description = description;
        return this;
    }

    public ScenarioDescription withTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public ScenarioDescription merge(ScenarioDescription scenarioDescription) {
        if (scenarioDescription.hasDescription())
            withDescription(scenarioDescription.getDescription());
        if (scenarioDescription.hasTags())
            withTags(scenarioDescription.getTags());
        return this;
    }

    private boolean hasTags() {
        return !tags.isEmpty();
    }

    private List<String> getTags() {
        return tags;
    }

    private boolean hasDescription() {
        return isNotBlank(description);
    }

    public String getDescription() {
        return description;
    }

    public ScenarioDescription withSteps(Stream<StepDescription> steps) {
        steps.forEachOrdered(x -> this.steps.add(x));
        return this;
    }

    public void mergeStep(StepDescription stepDescription) {
        steps.stream()
                .filter(x -> x.getTitle().equals(stepDescription.getTitle()))
                .findFirst()
                .ifPresent(step -> step.merge(stepDescription));
    }

    public List<StepDescription> getSteps() {
        return steps;
    }

    public boolean isFailed() {
        return steps.stream().anyMatch(StepDescription::isFailed);
    }

    public ScenarioDescription withExamples(List<Examples> examples) {
        this.examples = examples;
        return this;
    }

    public List<Examples> getExamples() {
        return examples;
    }
}
