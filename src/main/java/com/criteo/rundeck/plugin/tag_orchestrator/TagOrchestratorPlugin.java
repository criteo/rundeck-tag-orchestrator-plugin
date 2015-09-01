package com.criteo.rundeck.plugin.tag_orchestrator;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.execution.workflow.StepExecutionContext;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.orchestrator.Orchestrator;
import com.dtolabs.rundeck.plugins.orchestrator.OrchestratorPlugin;

import java.util.Collection;

/**
 * Created by g_seux on 8/31/15.
 */
@Plugin(name = TagOrchestrator.SERVICE_PROVIDER_TYPE, service = "Orchestrator")
public class TagOrchestratorPlugin implements OrchestratorPlugin {

    @PluginProperty(title="Tag Name", description = "Tag(s) used to group nodes. Multiple tags can be separated by commas or spaces", required = true)
    String tagsName;

    @PluginProperty(title="MaxPerGroup", description = "Maximum number of simultaneous updated node per group")
    int maxPerGroup;

    public  TagOrchestratorPlugin() {}

    public  TagOrchestratorPlugin(String tagNames, int maxPerGroup) {
        this.tagsName = tagNames;
        this.maxPerGroup = maxPerGroup;
    }

    @Override
    public Orchestrator createOrchestrator(StepExecutionContext context, Collection<INodeEntry> nodes) {
        String[] tags = tagsName.split("( |,)+");
        return new TagOrchestrator(context, nodes, tags, maxPerGroup);
    }
}
