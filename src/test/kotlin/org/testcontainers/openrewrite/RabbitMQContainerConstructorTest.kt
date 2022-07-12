package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class RabbitMQContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.RabbitMQContainerConstructorMigration
            displayName: Use RabbitMQContainer(String)
            description: Move from RabbitMQContainer() to RabbitMQContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use RabbitMQContainer(String)
                  fqn: org.testcontainers.containers.RabbitMQContainer
                  code: new RabbitMQContainer("rabbitmq:3.7.25-management-alpine")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.RabbitMQContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "rabbitmq").build())
        },
        java("""
            import org.testcontainers.containers.RabbitMQContainer;
            class Test {
                void doSomething() {
                    RabbitMQContainer container = new RabbitMQContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.RabbitMQContainer;
            class Test {
                void doSomething() {
                    RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");
                }
            }
        """
        )
    )

}