package com.github.open_fun.specs;

import com.github.open_fun.specs.beans.FeatureDescription;
import com.github.open_fun.specs.beans.ScenarioDescription;
import com.github.open_fun.specs.beans.Specs;
import com.github.open_fun.specs.beans.StepDescription;
import cucumber.api.PickleStepTestStep;
import cucumber.api.TestCase;
import cucumber.api.TestStep;
import cucumber.api.event.*;
import gherkin.ast.*;
import gherkin.pickles.PickleTag;

import java.util.List;
import java.util.stream.Collectors;

public abstract class SpecsPlugin implements ConcurrentEventListener {
    private Specs specs = new Specs();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestRunStarted.class, x -> specs = new Specs());
        publisher.registerHandlerFor(TestSourceRead.class, x -> handleSource(x.source));
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleScenario);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleStep);
        publisher.registerHandlerFor(TestRunFinished.class, x -> handleSpecs(specs));
    }

    public abstract void handleSpecs(Specs specs);

    private void handleStep(TestStepFinished testStepFinished) {
        TestStep testStep = testStepFinished.testStep;
        String scenarioName = testStepFinished.getTestCase().getName();
        if (testStep instanceof PickleStepTestStep) {
            PickleStepTestStep pickleStepTestStep = (PickleStepTestStep) testStep;
            StepDescription stepDescription = StepDescription.builder().status(testStepFinished.result)
                    .title(pickleStepTestStep.getStepText()).pattern(pickleStepTestStep.getPattern())
                    .params(pickleStepTestStep.getStepArgument()).build();
            specs.mergeStepToScenario(scenarioName, stepDescription);
        } else {
            String stepTitle = testStep.toString();
            StepDescription stepDescription = StepDescription.builder().status(testStepFinished.result).title(stepTitle)
                    .build();
            specs.mergeStepToScenario(scenarioName, stepDescription);
        }

    }

    private void handleScenario(TestCaseFinished testCaseFinished) {
        TestCase testCase = testCaseFinished.getTestCase();
        List<String> tags = testCase.getTags().stream().map(PickleTag::getName).collect(Collectors.toList());
        ScenarioDescription scenarioDescription = new ScenarioDescription().withTitle(testCase.getName())
                .withTags(tags);
        specs.mergeScenarioDescription(scenarioDescription);
    }

    private void handleSource(String source) {
        GherkinDocument gherkinDocument = GherkinDocumentFactory.create(source);
        Feature feature = gherkinDocument.getFeature();
        List<String> tags = feature.getTags().stream().map(Tag::getName).collect(Collectors.toList());

        List<ScenarioDescription> scenarios = feature.getChildren().stream().map(this::toScenarioDescription)
                .collect(Collectors.toList());

        FeatureDescription featureDescription = FeatureDescription.builder().title(feature.getName())
                .description(feature.getDescription()).tags(tags).scenarios(scenarios).build();
        specs.addFeature(featureDescription);
    }

    private ScenarioDescription toScenarioDescription(ScenarioDefinition scenarioDefinition) {
        String title = scenarioDefinition instanceof Background
                ? Background.class.getSimpleName() + ": " + scenarioDefinition.getName() : scenarioDefinition.getName();
        ScenarioDescription scenarioDescription = new ScenarioDescription()
                .withDescription(scenarioDefinition.getDescription()).withTitle(title)
                .withSteps(scenarioDefinition.getSteps().stream().map(this::toStepDescription));
        if (scenarioDefinition instanceof ScenarioOutline) {
            List<Examples> examples = ((ScenarioOutline) scenarioDefinition).getExamples();
            scenarioDescription.withExamples(examples);
        }
        return scenarioDescription;
    }

    private StepDescription toStepDescription(Step step) {
        return StepDescription.builder().title(step.getText()).keyword(step.getKeyword()).build();
    }
}
