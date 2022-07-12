package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class MariaDBContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.MariaDBContainerConstructorMigration
            displayName: Use MariaDBContainer(String)
            description: Move from MariaDBContainer() to MariaDBContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use MariaDBContainer(String)
                  fqn: org.testcontainers.containers.MariaDBContainer
                  code: new MariaDBContainer("mariadb:10.3.6")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.MariaDBContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "mariadb").build())
        },
        java("""
            import org.testcontainers.containers.MariaDBContainer;
            class Test {
                void doSomething() {
                    MariaDBContainer container = new MariaDBContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.MariaDBContainer;
            class Test {
                void doSomething() {
                    MariaDBContainer container = new MariaDBContainer("mariadb:10.3.6");
                }
            }
        """
        )
    )

}