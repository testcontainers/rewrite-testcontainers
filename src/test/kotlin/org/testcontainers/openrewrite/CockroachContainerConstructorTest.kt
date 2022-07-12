package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class CockroachContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.CockroachContainerConstructorMigration
            displayName: Use CockroachContainer(String)
            description: Move from CockroachContainer() to CockroachContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use CockroachContainer(String)
                  fqn: org.testcontainers.containers.CockroachContainer
                  code: new CockroachContainer("cockroachdb/cockroach:v19.2.11")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.CockroachContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "cockroachdb").build())
        },
        java("""
            import org.testcontainers.containers.CockroachContainer;
            class Test {
                void doSomething() {
                    CockroachContainer container = new CockroachContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.CockroachContainer;
            class Test {
                void doSomething() {
                    CockroachContainer container = new CockroachContainer("cockroachdb/cockroach:v19.2.11");
                }
            }
        """
        )
    )

}