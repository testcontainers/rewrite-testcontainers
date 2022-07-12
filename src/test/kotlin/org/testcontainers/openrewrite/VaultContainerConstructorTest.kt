package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class VaultContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.VaultContainerConstructorMigration
            displayName: Use VaultContainer(String)
            description: Move from VaultContainer() to VaultContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use VaultContainer(String)
                  fqn: org.testcontainers.vault.VaultContainer
                  code: new VaultContainer<>("vault:1.1.3")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.VaultContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "vault").build())
        },
        java("""
            import org.testcontainers.vault.VaultContainer;
            class Test {
                void doSomething() {
                    VaultContainer container = new VaultContainer();
                }
            }
        """,
        """
            import org.testcontainers.vault.VaultContainer;
            class Test {
                void doSomething() {
                    VaultContainer container = new VaultContainer<>("vault:1.1.3");
                }
            }
        """
        )
    )

}