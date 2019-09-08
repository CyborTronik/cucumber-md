package com.github.open_fun;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = { "pretty", "com.github.open_fun.MarkdownPlugin:docs" })
public class RunCukesTest {
}
