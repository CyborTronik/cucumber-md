package com.github.open_fun;

import gherkin.ast.TableCell;
import gherkin.ast.TableRow;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class MarkdownBuilder {
    private StringBuilder builder = new StringBuilder();

    public MarkdownBuilder withHeader(String title) {
        return withLine("# " + title);
    }

    private MarkdownBuilder withLine(String text) {
        if (isNotBlank(text)) {
            builder.append(text);
            builder.append(System.lineSeparator());
        }
        return this;
    }

    private MarkdownBuilder withLine() {
        builder.append(System.lineSeparator());
        return this;
    }

    public MarkdownBuilder withTitle(String title) {
        return withLine("## " + title);
    }

    public MarkdownBuilder withSecondaryTitle(String title) {
        return withLine("##### " + title);
    }

    public MarkdownBuilder withParagraph(String description) {
        return withLine().withLine(description);
    }

    public MarkdownBuilder withStories(Stream<MarkdownBuilder> stories) {
        stories.forEachOrdered(x -> withText(x.toString()));
        return this;
    }

    public MarkdownBuilder withBlockquotes(Stream<MarkdownBuilder> blockquotes) {
        blockquotes.forEachOrdered(this::withBlockquotes);
        return this;
    }

    public MarkdownBuilder withBlockquotes(MarkdownBuilder markdownBuilder) {
        return withBlockquote(markdownBuilder.toString());
    }

    public MarkdownBuilder withText(String text) {
        builder.append(text);
        return this;
    }

    public MarkdownBuilder withError(String errorMessage) {
        return withErrorSign().withText(errorMessage);
    }

    public MarkdownBuilder withErrorSign() {
        return withText(":warning:");
    }

    public MarkdownBuilder withTable(TableRow tableHeader, List<TableRow> tableBody) {
        withLine();
        withLine();
        if (tableHeader != null) {
            String row = tableRow(tableHeader);
            String headerSeparator = tableHeader.getCells().stream().map(x -> ":---:").collect(Collectors.joining(" | "));
            withLine(row);
            withLine("| " + headerSeparator + " |");
        }
        tableBody.stream().map(this::tableRow).forEach(this::withLine);
        return this;
    }

    private String tableRow(TableRow tableRow) {
        String row = tableRow.getCells().stream().map(TableCell::getValue).collect(Collectors.joining(" | "));
        return "| " + row + " |";
    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public MarkdownBuilder withBlockquote(String description) {
        return withLine("> " + description);
    }

    public MarkdownBuilder withBlockText(Stream<MarkdownBuilder> markdownBuilderStream) {
        markdownBuilderStream.forEach(x -> withLine("       " + x));
        return this;
    }

    public MarkdownBuilder withText(Stream<MarkdownBuilder> markdownBuilderStream) {
        markdownBuilderStream.forEach(x -> withText(x.toString()));
        return this;
    }
}
