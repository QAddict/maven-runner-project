package org.qaddict.maven.run;

import org.apache.maven.artifact.Artifact;

import java.util.stream.Stream;

public record JarKey(String key, String jar) {

    public static JarKey of(String jar, String... id) {
        return new JarKey(String.join(":", id), jar);
    }

    public static Stream<JarKey> keys(Artifact artifact) {
        String absolutePath = artifact.getFile().getAbsolutePath();
        String groupId = artifact.getGroupId();
        String artifactId = artifact.getArtifactId();
        String version = artifact.getVersion();
        String type = artifact.getType();
        String classifier = artifact.getClassifier();
        return classifier == null ? Stream.of(
                of(absolutePath, groupId, artifactId, version, type),
                of(absolutePath, groupId, artifactId, version),
                of(absolutePath, groupId, artifactId),
                of(absolutePath, groupId, artifactId, type)
        ) : Stream.of(
                of(absolutePath, groupId, artifactId, classifier, version, type),
                of(absolutePath, groupId, artifactId, classifier, type)
        );
    }

}
