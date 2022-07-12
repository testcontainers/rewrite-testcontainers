package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class PulsarContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.PulsarContainerConstructorMigration
            displayName: Use PulsarContainer(String)
            description: Move from PulsarContainer() to PulsarContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use PulsarContainer(String)
                  fqn: org.testcontainers.containers.PulsarContainer
                  code: new PulsarContainer("apachepulsar/pulsar:2.10.0")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.PulsarContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "pulsar").build())
        },
        java("""
            import org.testcontainers.containers.PulsarContainer;
            class Test {
                void doSomething() {
                    PulsarContainer container = new PulsarContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.PulsarContainer;
            class Test {
                void doSomething() {
                    PulsarContainer container = new PulsarContainer("apachepulsar/pulsar:2.10.0");
                }
            }
        """
        )
    )

}