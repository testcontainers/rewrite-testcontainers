package org.testcontainers.openrewrite;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openrewrite.ExecutionContext;
import org.openrewrite.Option;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.search.UsesType;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.TypeUtils;

public class MigrateContainerConstructor extends Recipe {

    @Option(displayName = "", description = "", example = "")
    private final String displayName;

    @Option(displayName = "", description = "", example = "org.testcontainers.containers.CassandraContainer")
    private final String fqn;

    @Option(displayName = "", description = "", example = "new CassandraContainer<>(\"cassandra:3.11.2\")")
    private final String code;

    @JsonCreator
    public MigrateContainerConstructor(
        @JsonProperty("displayName") String displayName,
        @JsonProperty("fqn") String fqn,
        @JsonProperty("code") String code
    ) {
        this.displayName = displayName;
        this.fqn = fqn;
        this.code = code;
    }

    @Override
    public String getDisplayName() {
        return "Use `" + this.displayName + "`";
    }

    @Override
    protected @Nullable TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new UsesType<>(this.fqn);
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaVisitor<ExecutionContext>() {
            @Override
            public J visitNewClass(J.NewClass newClass, ExecutionContext executionContext) {
                if (
                    TypeUtils.isOfClassType(newClass.getType(), fqn) &&
                    newClass.getConstructorType() != null &&
                    newClass.getConstructorType().getParameterTypes().isEmpty()
                ) {
                    return newClass.withTemplate(
                        JavaTemplate
                            .builder(this::getCursor, code)
                            .javaParser(() -> JavaParser.fromJavaVersion().classpath("testcontainers").build())
                            .build(),
                        newClass.getCoordinates().replace()
                    );
                }
                return super.visitNewClass(newClass, executionContext);
            }
        };
    }
}
