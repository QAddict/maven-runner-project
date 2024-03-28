package org.qaddict.maven.run;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.qaddict.starter.JarUtils;
import org.qaddict.starter.Jvm;

import java.io.IOException;
import java.util.List;

@Mojo(name = "main", requiresProject = false)
public class MainRunnerMojo extends AbstractRunnerMojo {

    @Parameter(property = "mainClass")
    String mainClass;

    @Override
    protected void execute(Jvm jvm, String jar, List<String> resolvedArgs) throws MojoExecutionException {
        try {
            if(mainClass == null)
                mainClass = JarUtils.getMainClassFromManifestOfJar(jar);
            if(mainClass == null)
                throw new MojoExecutionException("Main class neither provided, nor present in JAR manifest of " + jar);
            int state = jvm.mainClass(mainClass).args(resolvedArgs).startAndWaitFor();
            if(state != 0)
                throw new MojoExecutionException("Process exited with code " + state);
        } catch (IOException | InterruptedException e) {
            throw new MojoExecutionException(e);
        }
    }

}
