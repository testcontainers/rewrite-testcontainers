package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class KafkaContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.KafkaContainerConstructorMigration
            displayName: Use KafkaContainer(String)
            description: Move from KafkaContainer() to KafkaContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use KafkaContainer(String)
                  fqn: org.testcontainers.containers.KafkaContainer
                  code: new KafkaContainer("confluentinc/cp-kafka:5.4.3")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.KafkaContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "kafka").build())
        },
        java("""
            import org.testcontainers.containers.KafkaContainer;
            class Test {
                void doSomething() {
                    KafkaContainer container = new KafkaContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.KafkaContainer;
            class Test {
                void doSomething() {
                    KafkaContainer container = new KafkaContainer("confluentinc/cp-kafka:5.4.3");
                }
            }
        """
        )
    )

}