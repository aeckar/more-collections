import javax.xml.parsers.DocumentBuilderFactory

plugins {
    kotlin("jvm") version "2.0.21"
    id("dev.adamko.dokkatoo-html") version "2.3.1"
    id("org.jetbrains.kotlinx.kover") version "0.8.3"
    `maven-publish`
}

val buildDir = layout.buildDirectory.get()

group = "io.github.aeckar"
version = "1.0.0"

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    explicitApi()
    jvmToolchain(21)
}

dokkatoo {
    dokkatooPublications.configureEach {
        moduleName = "more.collections"
        includes.from("README.md")
    }
    dokkatooSourceSets.configureEach {
        moduleName = "more.collections"
        includes.from("Module.md")
        sourceLink {
            localDirectory = rootDir
            remoteUrl("https://github.com/aeckar/${rootProject.name}/tree/main")
            remoteLineSuffix = "#L"
        }
        reportUndocumented = true
    }
    pluginsConfiguration.html {
        footerMessage = "\u00A9 Angel Eckardt"
        separateInheritedMembers = true
    }
}

kover {
    reports {
        filters.excludes {
            annotatedBy("io.github.aeckar.utils.KoverExclude")
            inheritedFrom(
                "io.github.aeckar.collections.PrimitiveList",
                "io.github.aeckar.collections.View"
            )

        }
        verify.rule {
            minBound(70)
        }
    }
}

tasks.register<Sync>("ensureGradleWrapperIsExecutable") {
    Runtime.getRuntime().exec(arrayOf("git", "update-index", "--chmod=+x", "gradlew"))
}

tasks.register<Sync>("generateDocumentation") {
    group = "documentation"
    description = "Generates HTML documentation from KDoc comments and moves them to /docs"
    dependsOn("dokkatooGenerate")

    doLast {
        copy {
            from("$buildDir/dokka/html")
            into("docs")
        }
    }
}

tasks.register<Sync>("updateReadmeVersion") {
    group = "documentation"
    description = "Updates the library version as shown in README.md"

    doLast {
        val readme = file("README.md")
        readme.writeText(readme.readText().replace("(?<=v)\\d+(\\.\\d+)+".toRegex(), version.toString()))
    }
}

// Original author: David Herman
// https://bitspittle.dev/blog/2022/kover-badge
tasks.register("printLineCoverage") {
    group = "verification"
    description = "Prints line coverage to the command line"
    dependsOn("koverXmlReport")

    doLast {
        val report = file("$buildDir/reports/kover/report.xml")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
        val rootNode = doc.firstChild
        var childNode = rootNode.firstChild
        var coveragePercent = 0.0

        while (childNode != null) {
            if (childNode.nodeName == "counter") {
                val typeAttr = childNode.attributes.getNamedItem("type")

                if (typeAttr.textContent == "LINE") {
                    val missed = childNode.attributes.getNamedItem("missed").textContent.toLong()
                    val covered = childNode.attributes.getNamedItem("covered").textContent.toLong()

                    coveragePercent = (covered * 100.0) / (missed + covered)
                    break
                }
            }
            childNode = childNode.nextSibling
        }
        println("%.1f".format(coveragePercent))
    }
}

tasks["build"].dependsOn(
    tasks["ensureGradleWrapperIsExecutable"],
    tasks["generateDocumentation"],
    tasks["updateReadmeVersion"],
    tasks["koverVerify"]
)