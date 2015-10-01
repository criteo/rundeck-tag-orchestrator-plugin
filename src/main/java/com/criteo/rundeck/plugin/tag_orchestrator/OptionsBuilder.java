package com.criteo.rundeck.plugin.tag_orchestrator;

public class OptionsBuilder {

    private double maxPerGroup;
    private String[] tagNames;
    private boolean stopProcessingGroupAfterTooManyFailure = true;

    public Options build() {
        return new Options(maxPerGroup, tagNames);
    }

    public OptionsBuilder maxPerGroup(double max) {
        this.maxPerGroup = max;
        return this;
    }

    public OptionsBuilder tagNames(String[] tagNames) {
        this.tagNames = tagNames;
        return this;
    }

    public OptionsBuilder stopProcessingGroupAfterTooManyFailure(boolean value) {
        this.stopProcessingGroupAfterTooManyFailure = value;
        return this;
    }
}
