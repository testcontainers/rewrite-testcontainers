package org.testcontainers.openrewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.maven.internal.MavenPomDownloader;
import org.openrewrite.maven.tree.GroupArtifact;
import org.openrewrite.maven.tree.MavenMetadata;
import org.openrewrite.semver.LatestRelease;
import org.openrewrite.semver.Semver;
import org.openrewrite.semver.VersionComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class TestcontainersVersionHelper {

    private static final String GROUP_ID = "org.testcontainers";
    private static final String ARTIFACT_ID = "testcontainers-bom";
    private static final LatestRelease LATEST_RELEASE = new LatestRelease(null);

    public static Optional<String> getNewerVersion(String versionPattern, String currentVersion, ExecutionContext ctx) {
        VersionComparator versionComparator = Semver.validate(versionPattern, null).getValue();
        assert versionComparator != null;

        MavenMetadata mavenMetadata = new MavenPomDownloader(emptyMap(), ctx)
            .downloadMetadata(new GroupArtifact(GROUP_ID, ARTIFACT_ID), null, emptyList());

        Collection<String> availableVersions = new ArrayList<>();
        for (String v : mavenMetadata.getVersioning().getVersions()) {
            if (versionComparator.isValid(null, v)) {
                availableVersions.add(v);
            }
        }

        return availableVersions
            .stream()
            .filter(v -> LATEST_RELEASE.compare(null, currentVersion, v) < 0)
            .max(LATEST_RELEASE);
    }

    private TestcontainersVersionHelper() {}
}
