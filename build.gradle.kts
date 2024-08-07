import io.izzel.taboolib.gradle.*

plugins {
    `java-library`
    id("io.izzel.taboolib") version "2.0.13"
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
}

taboolib {
    description {
        contributors {
            name("ItsFlicker")
        }
        dependencies {
            name("ItemsAdder")
        }
    }
    env {
        install(UNIVERSAL, BUKKIT_ALL)
        install(DATABASE, LANG)
    }
    version {
        taboolib = "6.1.2-beta12"
        coroutines = null
    }
    relocate("de.tr7zw.changeme.nbtapi", "$group.nbtapi")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
}

dependencies {
    taboo("de.tr7zw:item-nbt-api:2.13.1")
    taboo("com.alibaba.fastjson2:fastjson2-kotlin:2.0.50")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.2.5")
//    compileOnly("ink.ptms:nms-all:1.0.0")
    compileOnly("ink.ptms.core:v12100:12100:mapped")
    compileOnly("ink.ptms.core:v12100:12100:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}