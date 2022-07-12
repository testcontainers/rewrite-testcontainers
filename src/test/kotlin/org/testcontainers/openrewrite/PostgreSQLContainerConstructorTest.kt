package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class PostgreSQLContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.PostgreSQLContainerConstructorMigration
            displayName: Use PostgreSQLContainer(String)
            description: Move from PostgreSQLContainer() to PostgreSQLContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use PostgreSQLContainer(String)
                  fqn: org.testcontainers.containers.PostgreSQLContainer
                  code: new PostgreSQLContainer<>("postgres:9.6.12")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.PostgreSQLContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "postgresql").build())
        },
        java("""
            import org.testcontainers.containers.PostgreSQLContainer;
            class Test {
                void doSomething() {
                    PostgreSQLContainer container = new PostgreSQLContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.PostgreSQLContainer;
            class Test {
                void doSomething() {
                    PostgreSQLContainer container = new PostgreSQLContainer<>("postgres:9.6.12");
                }
            }
        """
        )
    )

}