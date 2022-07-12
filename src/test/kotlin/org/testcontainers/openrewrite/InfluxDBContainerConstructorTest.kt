package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class InfluxDBContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.InfluxDBContainerConstructorMigration
            displayName: Use InfluxDBContainer(String)
            description: Move from InfluxDBContainer() to InfluxDBContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use InfluxDBContainer(String)
                  fqn: org.testcontainers.containers.InfluxDBContainer
                  code: new InfluxDBContainer("influxdb:1.4.3")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.InfluxDBContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "influxdb").build())
        },
        java("""
            import org.testcontainers.containers.InfluxDBContainer;
            class Test {
                void doSomething() {
                    InfluxDBContainer container = new InfluxDBContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.InfluxDBContainer;
            class Test {
                void doSomething() {
                    InfluxDBContainer container = new InfluxDBContainer("influxdb:1.4.3");
                }
            }
        """
        )
    )

}