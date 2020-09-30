plugins {
    groovy
    java
    kotlin("jvm") version "1.3.72"
}

group = "io.study"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.codehaus.groovy:groovy-all:2.3.11")
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.3.72")
    testImplementation("org.assertj", "assertj-core", "3.17.2")

    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    //    testCompile group: 'org.assertj', name: 'assertj-core', version: '3.17.2'
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test{
        useJUnitPlatform()
    }
}

