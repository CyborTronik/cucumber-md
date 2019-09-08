package com.github.open_fun;

import com.github.open_fun.specs.SpecsPlugin;
import com.github.open_fun.specs.beans.FeatureDescription;
import com.github.open_fun.specs.beans.ScenarioDescription;
import com.github.open_fun.specs.beans.Specs;
import com.github.open_fun.specs.beans.StepDescription;
import gherkin.ast.Examples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

public class MarkdownPlugin extends SpecsPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownPlugin.class);

    private String outputDirectory;

    public MarkdownPlugin(String outputDirectory) throws IOException {
        this.outputDirectory = outputDirectory;
        Path directories = Files.createDirectories(Paths.get(outputDirectory));
        LOGGER.info("Report to be generated into {}", directories);
    }

    @Override
    public void handleSpecs(Specs specs) {
        for (FeatureDescription feature : specs.getFeatures()) {
            try {
                writeMarkdownFile(feature);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeMarkdownFile(FeatureDescription feature) throws IOException {
        MarkdownBuilder markdown = toMarkdown(feature);
        String fileName = toFileName(feature.getTitle());
        Path path = Paths.get(outputDirectory, fileName);
        Files.write(path, Collections.singleton(markdown.toString()));
    }

    private String toFileName(String title) {
        return title.replaceAll("\\W+", "_") + ".md";
    }

    private MarkdownBuilder toMarkdown(FeatureDescription feature) {
        Stream<MarkdownBuilder> stories = feature.getScenarios().stream().map(this::toMarkdown);
        return new MarkdownBuilder().withHeader(escapeHtml4(feature.getTitle()))
                .withParagraph(escapeHtml4(feature.getDescription())).withStories(stories);
    }

    private MarkdownBuilder toMarkdown(ScenarioDescription scenarioDescription) {
        Stream<MarkdownBuilder> steps = scenarioDescription.getSteps().stream().map(this::toMarkdown);
        MarkdownBuilder markdownBuilder = new MarkdownBuilder();
        if (scenarioDescription.isFailed()) {
            String title = new MarkdownBuilder().withText(escapeHtml4(scenarioDescription.getTitle())).withErrorSign()
                    .toString();
            markdownBuilder.withTitle(title);
        } else {
            markdownBuilder.withTitle(escapeHtml4(scenarioDescription.getTitle()));
        }
        Stream<MarkdownBuilder> examples = scenarioDescription.getExamples().stream().map(this::toMarkdown);
        return markdownBuilder.withParagraph(escapeHtml4(scenarioDescription.getDescription())).withBlockText(steps)
                .withText(examples);
    }

    private MarkdownBuilder toMarkdown(Examples examples) {
        if (examples == null)
            return new MarkdownBuilder();
        MarkdownBuilder table = new MarkdownBuilder().withTable(examples.getTableHeader(), examples.getTableBody());
        return new MarkdownBuilder().withParagraph("**" + examples.getKeyword() + "**: " + examples.getName())
                .withParagraph(examples.getDescription()).withParagraph(table.toString());
    }

    private MarkdownBuilder toMarkdown(StepDescription stepDescription) {
        MarkdownBuilder markdownBuilder = new MarkdownBuilder()
                .withText(stepDescription.getKeyword() + stepDescription.getTitle());
        if (stepDescription.isFailed()) {
            markdownBuilder.withError(stepDescription.getErrorMessage());
        }
        return markdownBuilder;
    }
}
