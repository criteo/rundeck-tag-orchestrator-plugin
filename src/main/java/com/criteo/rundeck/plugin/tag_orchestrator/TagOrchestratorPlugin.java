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

    @PluginProperty(title="Tag Name", description = "Tag used to group nodes", required = true)
    String tagName;

    @PluginProperty(title="MaxPerGroup", description = "Maximum number of simultenous updated node per group")
    int maxPerGroup;

    @Override
    public Orchestrator createOrchestrator(StepExecutionContext context, Collection<INodeEntry> nodes) {
        return new TagOrchestrator(context, nodes, tagName, maxPerGroup);
    }
}
