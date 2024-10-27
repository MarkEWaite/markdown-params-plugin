package io.jenkins.plugins.markdownparams;

import hudson.model.Label;
import hudson.slaves.DumbSlave;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class MarkdownParamsStepTest {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testMarkdownParamsStepExecution() throws Exception {
        String agentLabel = "test-agent";
        DumbSlave node = jenkins.createOnlineSlave(Label.get(agentLabel));
        WorkflowJob job = jenkins.createProject(WorkflowJob.class, "test-job");
        String pipelineScript = "def md = markdownParams('#### Header\\n- [x] Item1\\n')\n"
                + "assert md instanceof io.jenkins.plugins.markdownparams.Parser";
        job.setDefinition(new CpsFlowDefinition(pipelineScript, true));
        WorkflowRun run = jenkins.buildAndAssertSuccess(job);
        System.out.println(run.getLog());
        jenkins.assertLogContains("markdownParams", run);
    }
}
