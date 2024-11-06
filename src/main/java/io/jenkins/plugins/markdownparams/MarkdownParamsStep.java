package io.jenkins.plugins.markdownparams;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import java.util.Collections;
import java.util.Set;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

public class MarkdownParamsStep extends Step {

    private final String input;

    @DataBoundConstructor
    public MarkdownParamsStep(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    @Override
    public StepExecution start(StepContext context) {
        return new StepExecution(context, input);
    }

    private static class StepExecution extends SynchronousNonBlockingStepExecution<Parser> {

        private final String input;

        protected StepExecution(StepContext context, String input) {
            super(context);
            this.input = input;
        }

        @Override
        protected Parser run() throws Exception {
            return new Parser(input);
        }
    }

    @Symbol("MarkdownParams")
    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "markdownParams";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "MarkdownParams";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return false;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.emptySet();
        }
    }
}
