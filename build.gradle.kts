import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`

    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("nebula.release") version "15.3.1"

    id("nebula.maven-manifest") version "17.3.2"
    id("nebula.maven-nebula-publish") version "17.3.2"
    id("nebula.maven-resolved-dependencies") version "17.3.2"

    id("nebula.contacts") version "5.1.0"
    id("nebula.info") version "9.3.0"

    id("nebula.javadoc-jar") version "17.3.2"
    id("nebula.source-jar") version "17.3.2"

    id("com.diffplug.spotless") version "6.8.0"
}

apply(plugin = "nebula.publish-verification")

configure<nebula.plugin.release.git.base.ReleasePluginExtension> {
    defaultVersionStrategy = nebula.plugin.release.NetflixOssStrategies.SNAPSHOT(project)
}

group = "org.openrewrite.recipe"
description = "Rewrite recipes."

repositories {
    mavenLocal()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    mavenCentral()
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, TimeUnit.SECONDS)
        cacheDynamicVersionsFor(0, TimeUnit.SECONDS)
    }
}

val assertjVersion = "3.23.1"
val findbugsJsr305Version = "3.0.2"
val junitJupiterVersion = "5.8.2"
val junitVersion = "4.13.2"
val lombokVersion = "1.18.24"
val rewriteBomVersion = "1.5.0"
val testcontainersBomVersion = "1.17.3"

dependencies {
    compileOnly("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("com.google.code.findbugs:jsr305:${findbugsJsr305Version}")
    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    implementation(platform("org.openrewrite.recipe:rewrite-recipe-bom:${rewriteBomVersion}"))

    implementation("org.openrewrite:rewrite-java")
    implementation("org.openrewrite:rewrite-maven")
    implementation("org.openrewrite:rewrite-properties")
    runtimeOnly("org.openrewrite:rewrite-java-8")

    testImplementation(platform(kotlin("bom", "1.6.21")))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("stdlib"))
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${junitJupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")

    testImplementation("org.openrewrite:rewrite-test")
    testImplementation("org.assertj:assertj-core:${assertjVersion}")

    testImplementation(platform("org.testcontainers:testcontainers-bom:${testcontainersBomVersion}"))
    testRuntimeOnly("org.testcontainers:testcontainers")
    testRuntimeOnly("org.testcontainers:cassandra")
    testRuntimeOnly("org.testcontainers:clickhouse")
    testRuntimeOnly("org.testcontainers:cockroachdb")
    testRuntimeOnly("org.testcontainers:couchbase")
    testRuntimeOnly("org.testcontainers:db2")
    testRuntimeOnly("org.testcontainers:dynalite")
    testRuntimeOnly("org.testcontainers:elasticsearch")
    testRuntimeOnly("org.testcontainers:influxdb")
    testRuntimeOnly("org.testcontainers:kafka")
    testRuntimeOnly("org.testcontainers:localstack")
    testRuntimeOnly("org.testcontainers:mariadb")
    testRuntimeOnly("org.testcontainers:mockserver")
    testRuntimeOnly("org.testcontainers:mssqlserver")
    testRuntimeOnly("org.testcontainers:mongodb")
    testRuntimeOnly("org.testcontainers:mysql")
    testRuntimeOnly("org.testcontainers:neo4j")
    testRuntimeOnly("org.testcontainers:nginx")
    testRuntimeOnly("org.testcontainers:oracle-xe")
    testRuntimeOnly("org.testcontainers:orientdb")
    testRuntimeOnly("org.testcontainers:postgresql")
    testRuntimeOnly("org.testcontainers:pulsar")
    testRuntimeOnly("org.testcontainers:rabbitmq")
    testRuntimeOnly("org.testcontainers:solr")
    testRuntimeOnly("org.testcontainers:toxiproxy")
    testRuntimeOnly("org.testcontainers:vault")
    testRuntimeOnly("junit:junit:${junitVersion}")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    jvmArgs = listOf("-XX:+UnlockDiagnosticVMOptions", "-XX:+ShowHiddenFrames")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType(KotlinCompile::class.java).configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
    }

    doFirst {
        destinationDir.mkdirs()
    }
}

spotless {
    java {
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()

        prettier(mapOf(
            "prettier" to "2.5.1",
            "prettier-plugin-java" to "1.6.2"
        ))
            .config(mapOf(
                "parser" to "java",
                "tabWidth" to 4,
                "printWidth" to 120
            ))

        importOrder("", "java", "javax", "\\#")
    }
}

configure<PublishingExtension> {
    publications {
        named("nebula", MavenPublication::class.java) {
            suppressPomMetadataWarningsFor("runtimeElements")
        }
    }
}

publishing {
  repositories {
      maven {
          name = "moderne"
          url = uri("https://us-west1-maven.pkg.dev/moderne-dev/moderne-recipe")
      }
  }
}
