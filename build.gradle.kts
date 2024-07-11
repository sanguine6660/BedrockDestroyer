import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import org.cadixdev.gradle.licenser.LicenseExtension
import org.cadixdev.gradle.licenser.Licenser

plugins {
    id("java")
    id("java-library")
    id("maven-publish")
    id("signing")

    alias(libs.plugins.shadow)
    alias(libs.plugins.licenser)

    eclipse
    idea
}

group = "com.github.sanguine6660.bedrock"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    mavenLocal()
}

dependencies {
    compileOnly(libs.bukkit)
}

apply {
    plugin<JavaPlugin>()
    plugin<JavaLibraryPlugin>()
    plugin<MavenPublishPlugin>()
    plugin<ShadowPlugin>()
    plugin<Licenser>()
    plugin<EclipsePlugin>()
    plugin<IdeaPlugin>()
}

tasks.compileJava.configure {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
}

configurations.all {
    attributes.attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 17)
}

configure<LicenseExtension> {
    header(rootProject.file("HEADER"))
    include("**/*.java")
    newLine.set(true)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    //withSourcesJar()
    //withJavadocJar()
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
    skip()
}

tasks {

    withType<ProcessResources> {
        doLast {
            filesMatching("*") {
                expand(project.properties)
                expand() {
                    filter { key ->
                        project.findProperty(key)?.toString() ?: "\${${key}}"
                    }
                }
            }
        }
    }

    compileJava {
        options.compilerArgs.addAll(arrayOf("-Xmaxerrs", "1000"))
        options.compilerArgs.add("-Xlint:all")
        for (disabledLint in arrayOf("processing", "path", "fallthrough", "serial")) options.compilerArgs.add("-Xlint:$disabledLint")
        options.isDeprecation = true
        options.encoding = Charsets.UTF_8.name()
    }

    jar {
        this.archiveClassifier.set(null as String?)
        this.archiveFileName.set("${project.name}-${project.version}-unshaded.${this.archiveExtension.getOrElse("jar")}")
        this.destinationDirectory.set(file("$projectDir/out/unshaded"))
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    named<ShadowJar>("shadowJar") {
        this.archiveClassifier.set(null as String?)
        this.archiveFileName.set("${project.name}-${project.version}.${this.archiveExtension.getOrElse("jar")}")
        this.destinationDirectory.set(file("$projectDir/out"))
    }

    named("build") {
        dependsOn(named("shadowJar"))
    }
}

tasks.named<ShadowJar>("shadowJar") {
    this.archiveClassifier.set(null as String?)
    this.archiveFileName.set("${project.name}-${project.version}.${this.archiveExtension.getOrElse("jar")}")
    this.destinationDirectory.set(file("$projectDir/out"))
    // Get rid of all the libs which are 100% unused.
    minimize()
    mergeServiceFiles()
}
