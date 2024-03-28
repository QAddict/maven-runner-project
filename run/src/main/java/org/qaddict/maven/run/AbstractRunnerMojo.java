package org.qaddict.maven.run;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.repository.RepositorySystem;
import org.qaddict.starter.Jvm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;

public abstract class AbstractRunnerMojo extends AbstractMojo {

    @Component
    RepositorySystem system;

    @Parameter(defaultValue = "${session}", readonly = true)
    MavenSession session;

    @Parameter(property = "in", readonly = true, required = true)
    String coordinates;

    @Parameter(property = "args")
    String args;

    @Parameter(property = "argFile")
    String argFile;

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {

        if (coordinates == null)
            throw new MojoFailureException("Artifact coordinates with code to execute not provided. Use the system property -Din=<coordinates>");

        String[] parts = coordinates.split(":");

        if (parts.length != 3)
            throw new MojoExecutionException("Invalid artifact coordinates: " + coordinates);

        Dependency dependency = new Dependency();
        dependency.setGroupId(parts[0]);
        dependency.setArtifactId(parts[1]);
        dependency.setVersion(parts[2]);

        Artifact artifact = system.createDependencyArtifact(dependency);

        ArtifactResolutionResult result = system.resolve(new ArtifactResolutionRequest().setArtifact(artifact)
                .setResolveTransitively(true).setLocalRepository(session.getLocalRepository())
                .setRemoteRepositories(session.getRequest().getRemoteRepositories())
                .setManagedVersionMap(session.getCurrentProject().getManagedVersionMap())
                .setResolveRoot(true));

        if (result.hasMissingArtifacts())
            throw new MojoExecutionException("Unable to resolve dependencies: " + result.getMissingArtifacts());

        if (result.hasExceptions())
            throw new MojoExecutionException("Errors occurred: " + result.getExceptions());

        Set<Artifact> artifacts = result.getArtifacts();

        List<String> jars = artifacts.stream().map(a -> a.getFile().getPath()).toList();

        Map<String, String> artifactJars = artifacts.stream().flatMap(JarKey::keys).collect(toMap(JarKey::key, JarKey::jar));


        List<String> resolvedArgs;

        try {
            resolvedArgs = (argFile == null ? ArgParser.parse(args) : Files.lines(Paths.get(argFile))).map(a -> artifactJars.getOrDefault(a, a)).toList();
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to parse file with arguments: " + argFile);
        }

        execute(Jvm.ofCurrent().classPath(jars), artifactJars.getOrDefault(coordinates, coordinates), new ArrayList<>(resolvedArgs));

    }

    abstract protected void execute(Jvm jvm, String jar, List<String> resolvedArgs) throws MojoExecutionException;

}
