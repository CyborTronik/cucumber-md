package com.github.open_fun.specs.beans;

import cucumber.api.Result;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StepDescription {
    private String title;
    private String keyword;
    private String pattern;
    private List<gherkin.pickles.Argument> params;
    private Result status;

    public boolean isFailed() {
        if (status == null)
            return false;
        return status.is(Result.Type.FAILED) || status.is(Result.Type.AMBIGUOUS);
    }

    public String getErrorMessage() {
        return status.getErrorMessage();
    }

    public void merge(StepDescription stepDescription) {
        if (stepDescription.status != null)
            this.status = stepDescription.status;
        if (stepDescription.params != null)
            this.params = stepDescription.params;
    }
}
