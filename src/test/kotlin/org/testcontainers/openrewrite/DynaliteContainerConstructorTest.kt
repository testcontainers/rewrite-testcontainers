package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class DynaliteContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.DynaliteContainerConstructorMigration
            displayName: Use DynaliteContainer(String)
            description: Move from DynaliteContainer() to DynaliteContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use DynaliteContainer(String)
                  fqn: org.testcontainers.dynamodb.DynaliteContainer
                  code: new DynaliteContainer("quay.io/testcontainers/dynalite:v1.2.1-1")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.DynaliteContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "dynalite").build())
        },
        java("""
            import org.testcontainers.dynamodb.DynaliteContainer;
            class Test {
                void doSomething() {
                    DynaliteContainer container = new DynaliteContainer();
                }
            }
        """,
        """
            import org.testcontainers.dynamodb.DynaliteContainer;
            class Test {
                void doSomething() {
                    DynaliteContainer container = new DynaliteContainer("quay.io/testcontainers/dynalite:v1.2.1-1");
                }
            }
        """
        )
    )

}