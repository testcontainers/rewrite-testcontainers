package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class NginxContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.NginxContainerConstructorMigration
            displayName: Use NginxContainer(String)
            description: Move from NginxContainer() to NginxContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use NginxContainer(String)
                  fqn: org.testcontainers.containers.NginxContainer
                  code: new NginxContainer<>("nginx:1.9.4")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.NginxContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "nginx").build())
        },
        java("""
            import org.testcontainers.containers.NginxContainer;
            class Test {
                void doSomething() {
                    NginxContainer container = new NginxContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.NginxContainer;
            class Test {
                void doSomething() {
                    NginxContainer container = new NginxContainer<>("nginx:1.9.4");
                }
            }
        """
        )
    )

}