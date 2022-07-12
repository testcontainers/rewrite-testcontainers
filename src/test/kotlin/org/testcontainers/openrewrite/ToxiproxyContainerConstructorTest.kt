package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class ToxiproxyContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.ToxiproxyContainerConstructorMigration
            displayName: Use ToxiproxyContainer(String)
            description: Move from ToxiproxyContainer() to ToxiproxyContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use ToxiproxyContainer(String)
                  fqn: org.testcontainers.containers.ToxiproxyContainer
                  code: new ToxiproxyContainer("shopify/toxiproxy:2.1.0")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.ToxiproxyContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "toxiproxy").build())
        },
        java("""
            import org.testcontainers.containers.ToxiproxyContainer;
            class Test {
                void doSomething() {
                    ToxiproxyContainer container = new ToxiproxyContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.ToxiproxyContainer;
            class Test {
                void doSomething() {
                    ToxiproxyContainer container = new ToxiproxyContainer("shopify/toxiproxy:2.1.0");
                }
            }
        """
        )
    )

}