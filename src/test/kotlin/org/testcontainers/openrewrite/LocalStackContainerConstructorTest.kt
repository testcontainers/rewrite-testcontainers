package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class LocalStackContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.LocalStackContainerConstructorMigration
            displayName: Use LocalStackContainer(String)
            description: Move from LocalStackContainer() to LocalStackContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use LocalStackContainer(String)
                  fqn: org.testcontainers.containers.localstack.LocalStackContainer
                  code: new LocalStackContainer("localstack/localstack:0.11.2")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.LocalStackContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "localstack").build())
        },
        java("""
            import org.testcontainers.containers.localstack.LocalStackContainer;
            class Test {
                void doSomething() {
                    LocalStackContainer container = new LocalStackContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.localstack.LocalStackContainer;
            class Test {
                void doSomething() {
                    LocalStackContainer container = new LocalStackContainer("localstack/localstack:0.11.2");
                }
            }
        """
        )
    )

}