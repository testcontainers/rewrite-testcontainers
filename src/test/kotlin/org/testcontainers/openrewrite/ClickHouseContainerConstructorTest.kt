package org.testcontainers.openrewrite

import org.junit.jupiter.api.Test
import org.openrewrite.java.JavaParser
import org.openrewrite.test.RewriteTest

class ClickHouseContainerConstructorTest : RewriteTest {

    @Test
    fun convertConstructor() = rewriteRun(
        {
        spec -> spec.recipe(
            """
            ---
            type: specs.openrewrite.org/v1beta/recipe
            name: org.testcontainers.openrewrite.ClickHouseContainerConstructorMigration
            displayName: Use ClickHouseContainer(String)
            description: Move from ClickHouseContainer() to ClickHouseContainer(String)
            recipeList:
              - org.testcontainers.openrewrite.MigrateContainerConstructor:
                  displayName: Use ClickHouseContainer(String)
                  fqn: org.testcontainers.containers.ClickHouseContainer
                  code: new ClickHouseContainer("yandex/clickhouse-server:18.10.3")
            """.trimIndent().byteInputStream(), "org.testcontainers.openrewrite.ClickHouseContainerConstructorMigration")
            .parser(JavaParser.fromJavaVersion().classpath("junit", "testcontainers", "clickhouse").build())
        },
        java("""
            import org.testcontainers.containers.ClickHouseContainer;
            class Test {
                void doSomething() {
                    ClickHouseContainer container = new ClickHouseContainer();
                }
            }
        """,
        """
            import org.testcontainers.containers.ClickHouseContainer;
            class Test {
                void doSomething() {
                    ClickHouseContainer container = new ClickHouseContainer("yandex/clickhouse-server:18.10.3");
                }
            }
        """
        )
    )

}