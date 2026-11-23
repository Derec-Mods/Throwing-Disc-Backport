plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

version = "${providers.gradleProperty("mod_version").getOrElse("1.2.0.0")}-${providers.gradleProperty("minecraft_version").getOrElse("1.7.10")}-${providers.gradleProperty("modloader").getOrElse("forge")}"

base {
    archivesName.set(providers.gradleProperty("archives_base_name").getOrElse("Throwing-Disc-Backport"))
}

tasks.withType<AbstractArchiveTask>().configureEach {
    archiveBaseName.set(providers.gradleProperty("archives_base_name").getOrElse("Throwing-Disc-Backport"))
    archiveVersion.set("${providers.gradleProperty("mod_version").getOrElse("1.2.0.0")}-${providers.gradleProperty("minecraft_version").getOrElse("1.7.10")}-${providers.gradleProperty("modloader").getOrElse("forge")}")
}

tasks.processResources {
    from("../../shared") {
        exclude("assets/renders/**")
        exclude("assets/screenshots/**")
        exclude("data/**")
    }
    from("../../shared/assets/throwing_disc_backport/textures/item") {
        into("assets/throwing_disc_backport/textures/items")
    }
}

tasks.named<JavaExec>("runClient") {
    classpath(file("build/rfg/mclauncher-1.7.10.jar"))
}
