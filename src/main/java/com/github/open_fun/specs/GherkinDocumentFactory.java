package com.github.open_fun.specs;

import gherkin.AstBuilder;
import gherkin.Parser;
import gherkin.TokenMatcher;
import gherkin.ast.GherkinDocument;

public class GherkinDocumentFactory {
    public static GherkinDocument create(String source) {
        Parser<GherkinDocument> parser = new Parser<>(new AstBuilder());
        TokenMatcher matcher = new TokenMatcher();
        return parser.parse(source, matcher);
    }
}
