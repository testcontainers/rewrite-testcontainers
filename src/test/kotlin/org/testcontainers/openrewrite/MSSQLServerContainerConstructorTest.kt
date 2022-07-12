package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class MSSQLServerContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.MSSQLServerContainerConstructorMigration
            displayName: Use MSSQLServerContainer(String)
            description: Move from MSSQLServerContainer() to MSSQLServerContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use MSSQLServerContainer(String)
                  fqn: org.testcontainers.containers.MSSQLServerContainer
                  code: new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-CU12")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.MSSQLServerContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "mssqlserver").build())
        },
        java("""
            import org.testcontainers.containers.MSSQLServerContainer;
            class Test {
                void doSomething() {
                    MSSQLServerContainer container = new MSSQLServerContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.MSSQLServerContainer;
            class Test {
                void doSomething() {
                    MSSQLServerContainer container = new MSSQLServerContainer<>("mcr.microsoft.com/mssql/server:2017-CU12");
                }
            }
        """
        )
    )

}