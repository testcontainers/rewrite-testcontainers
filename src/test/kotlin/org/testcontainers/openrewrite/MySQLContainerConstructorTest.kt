package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class MySQLContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.MySQLContainerConstructorMigration
            displayName: Use MySQLContainer(String)
            description: Move from MySQLContainer() to MySQLContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use MySQLContainer(String)
                  fqn: org.testcontainers.containers.MySQLContainer
                  code: new MySQLContainer<>("mysql:5.7.34")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.MySQLContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "mysql").build())
        },
        java("""
            import org.testcontainers.containers.MySQLContainer;
            class Test {
                void doSomething() {
                    MySQLContainer container = new MySQLContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.MySQLContainer;
            class Test {
                void doSomething() {
                    MySQLContainer container = new MySQLContainer<>("mysql:5.7.34");
                }
            }
        """
        )
    )

}