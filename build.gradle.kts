plugins {
    java
    `maven-publish`
    alias(libs.plugins.spring.boot)
}

group = "com.cdyhrj"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    implementation(libs.yhrj.fast.orm)
    implementation(libs.javassist)
    implementation(libs.lombok)
    implementation(libs.commons.lang3)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.guava)
    implementation(libs.com.googlecode.aviator.aviator)
    implementation(libs.spring.jdbc)
    implementation(libs.org.antlr.st4)
    implementation(libs.bundles.fastjson2)

    implementation(libs.jakarta.validation.api)
    compileOnly(libs.spring.boot.autoconfigure)

    annotationProcessor(libs.lombok)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testAnnotationProcessor(libs.lombok)
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-exports", "java.base/sun.security.ssl=ALL-UNNAMED"
    )
}

tasks.bootJar {
    enabled = false
}

tasks {
    register<Jar>("sourcesJar") {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(tasks.named<Jar>("jar")) {
                classifier = ""
            }

            artifact(tasks.named<Jar>("sourcesJar")) {}
        }
    }
}