package org.testcontainers.openrewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.ExecutionContext;
import org.openrewrite.HasSourcePath;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.Validated;
import org.openrewrite.internal.lang.NonNull;
import org.openrewrite.properties.PropertiesVisitor;
import org.openrewrite.properties.tree.Properties;
import org.openrewrite.semver.Semver;

public class UpgradeTestcontainersGradlePropertiesVersion extends Recipe {

    private static final String PROPERTY_KEY_TESTCONTAINERS_VERSION = "testcontainersVersion";
    private static final String PROPERTY_KEY_TESTCONTAINERS_DOT_VERSION = "testcontainers.version";
    private static final String FILE_MATCHER = "**/gradle.properties";

    @Option(
        displayName = "New version",
        description = "An exact version number, or node-style semver selector used to select the version number.",
        example = "1.17.x"
    )
    @NonNull
    private final String newVersion;

    @JsonCreator
    public UpgradeTestcontainersGradlePropertiesVersion(@JsonProperty("newVersion") String newVersion) {
        this.newVersion = newVersion;
    }

    @Override
    public Validated validate() {
        Validated validated = super.validate();
        //noinspection ConstantConditions
        if (newVersion != null) {
            validated = validated.and(Semver.validate(newVersion, null));
        }
        return validated;
    }

    @Override
    public String getDisplayName() {
        return "Upgrade gradle.properties Testcontainers version";
    }

    @Override
    public String getDescription() {
        return "Set the gradle.properties version number according to a node-style semver selector or to a specific version number.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new HasSourcePath<>(FILE_MATCHER);
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new ChangePropertyValueVisitor(newVersion);
    }

    private static class ChangePropertyValueVisitor extends PropertiesVisitor<ExecutionContext> {

        private final String newVersion;

        public ChangePropertyValueVisitor(String newVersion) {
            this.newVersion = newVersion;
        }

        @Override
        public Properties visitEntry(Properties.Entry entry, ExecutionContext ctx) {
            if (
                entry.getKey().equals(PROPERTY_KEY_TESTCONTAINERS_VERSION) ||
                entry.getKey().equals(PROPERTY_KEY_TESTCONTAINERS_DOT_VERSION)
            ) {
                String currentVersion = entry.getValue().getText();
                String latestVersion = TestcontainersVersionHelper
                    .getNewerVersion(newVersion, currentVersion, ctx)
                    .orElse(null);
                if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                    entry = entry.withValue(entry.getValue().withText(latestVersion));
                }
            }
            return super.visitEntry(entry, ctx);
        }
    }
}
