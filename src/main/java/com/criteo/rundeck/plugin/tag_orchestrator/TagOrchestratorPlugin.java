package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.workflow.StepExecutionContext;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;
import com.dtolabs.rundeck.plugins.orchestrator.OrchestratorPlugin;

import java.util.Collection;

@Plugin(name = TagOrchestrator.SERVICE_PROVIDER_TYPE, service = "Orchestrator")
public class TagOrchestratorPlugin implements OrchestratorPlugin {

    @PluginProperty(title="Tag Name", description = "Tag(s) used to group nodes. Multiple tags can be separated by commas or spaces", required = true)
    String tagsName;

    // FIXME: properties annotation supports only integer/string/boolean
    @PluginProperty(title="MaxPerGroup", description = "If value is integer >=1, this is the maximum number of simultaneous updated node per group." +
            "If value is a float between 0 and 1, this is a ratio of the number of host per group.")
    String maxPerGroup;

    @PluginProperty(title="StopProcessingGroupAfterTooManyFailure", description = "If set to true, nodes won't be processed in a group where too many nodes have already failed." +
            "The maximum number of nodes allowed to fail is MaxPerGroup", defaultValue=true)
    boolean stopProcessingGroupAfterTooManyFailure;

    public  TagOrchestratorPlugin() {}

    public  TagOrchestratorPlugin(String tagNames, double maxPerGroup) {
        this.tagsName = tagNames;
        this.maxPerGroup = Double.toString(maxPerGroup);
    }

    @Override
    public Orchestrator createOrchestrator(StepExecutionContext context, Collection<INodeEntry> nodes) {
        String[] tags = tagsName.split("( |,)+");
        OptionsBuilder options = (new OptionsBuilder()).
                maxPerGroup(Double.valueOf(maxPerGroup)).
                tagNames(tags).
                stopProcessingGroupAfterTooManyFailure(stopProcessingGroupAfterTooManyFailure);
        return new TagOrchestrator(context, nodes, options.build());
    }
}
