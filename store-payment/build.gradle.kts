dependencies {
    implementation(project(":store-core"))

    implementation("org.projectlombok:lombok:1.18.22") // Use the latest version
    annotationProcessor("org.projectlombok:lombok:1.18.22")

    implementation("com.google.zxing:core:3.3.0")
    implementation("com.google.zxing:javase:3.3.0")

    implementation("io.github.momo-wallet:momopayment:1.0") {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }


//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    runtimeOnly("org.postgresql:postgresql")

//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//
//    implementation("org.jetbrains.kotlin:kotlin-reflect")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
//    implementation("org.postgresql:postgresql")


}