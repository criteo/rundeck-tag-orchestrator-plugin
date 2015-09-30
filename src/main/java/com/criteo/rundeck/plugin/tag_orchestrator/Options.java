package com.criteo.rundeck.plugin.tag_orchestrator;

public class Options {

    public final double maxPerGroup;
    public final String[] tagNames;

    public Options(double maxPerGroup, String[] tagNames) {
        this.maxPerGroup = maxPerGroup;
        this.tagNames = tagNames;
    }


}
