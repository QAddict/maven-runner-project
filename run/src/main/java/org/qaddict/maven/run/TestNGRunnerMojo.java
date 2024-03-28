package org.qaddict.maven.run;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.qaddict.starter.Jvm;

import java.io.IOException;
import java.util.List;

@Mojo(name = "testng", requiresProject = false)
public class TestNGRunnerMojo extends AbstractRunnerMojo {

    @Override
    protected void execute(Jvm jvm, String jar, List<String> resolvedArgs) throws MojoExecutionException {
        try {
            if(!resolvedArgs.contains("-testjar"))
                resolvedArgs.addAll(List.of("-testjar", jar));
            int status = jvm.mainClass("org.testng.TestNG").args(resolvedArgs).startAndWaitFor();
            switch (status) {
                case 1: throw new MojoExecutionException("There were failed tests.");
                case 2: throw new MojoExecutionException("There were skipped tests.");
                default: throw new MojoExecutionException("There were failed and skipped tests.");
                case 0: getLog().info("All tests passed.");
            }
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException(e);
        }
    }

}
