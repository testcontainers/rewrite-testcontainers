package org.testcontainers.openrewrite;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.JavaParser;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.MethodMatcher;
import org.openrewrite.java.search.UsesMethod;
import org.openrewrite.java.tree.J;

public class MigrateWithCopyToContainer extends Recipe {

    private static final MethodMatcher methodMatcher = new MethodMatcher(
        "org.testcontainers.containers.Container withCopyFileToContainer(org.testcontainers.utility.MountableFile, String)",
        true
    );

    @Override
    public String getDisplayName() {
        return "Use `withCopyFileToContainer`";
    }

    @Override
    protected @Nullable TreeVisitor<?, ExecutionContext> getSingleSourceApplicableTest() {
        return new UsesMethod<>(methodMatcher);
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.MethodInvocation visitMethodInvocation(
                J.MethodInvocation method,
                ExecutionContext executionContext
            ) {
                J.MethodInvocation mI = super.visitMethodInvocation(method, executionContext);
                if (methodMatcher.matches(mI)) {
                    String transferableFqn = "org.testcontainers.images.builder.Transferable";
                    maybeAddImport(transferableFqn);
                    mI =
                        mI.withTemplate(
                            JavaTemplate
                                .builder(
                                    this::getCursor,
                                    "#{any(org.testcontainers.containers.Container)}.withCopyToContainer(Transferable.of(#{any(java.lang.String)}), #{any(java.lang.String)})"
                                )
                                .javaParser(() -> JavaParser.fromJavaVersion().classpath("testcontainers").build())
                                .imports(transferableFqn)
                                .build(),
                            mI.getCoordinates().replace(),
                            mI.getSelect(),
                            ((J.MethodInvocation) method.getArguments().get(0)).getArguments().get(0),
                            mI.getArguments().get(1)
                        );
                    maybeRemoveImport("org.testcontainers.utility.MountableFile");
                }
                return mI;
            }
        };
    }
}
