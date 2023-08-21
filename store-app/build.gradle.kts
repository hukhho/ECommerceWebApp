plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

dependencies {
    implementation(project(":store-core"))
    implementation(project(":store-details"))
    implementation(project(":store-payment"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")

    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")


    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")


    implementation(libs.jedis)
    implementation(platform("software.amazon.awssdk:bom:2.20.26"))
    implementation("software.amazon.awssdk:dynamodb")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect")

//    implementation("org.slf4j:slf4j-api:1.7.32")

//    implementation("com.google.zxing:core:3.3.0")
//    implementation("com.google.zxing:javase:3.3.0")
//    implementation("io.github.momo-wallet:momopayment:1.0")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-junit-jupiter:3.11.2")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("ch.qos.logback:logback-classic:1.2.6")




//    testImplementation ("com.amazonaws:aws-java-sdk-s3:1.12.470")

}