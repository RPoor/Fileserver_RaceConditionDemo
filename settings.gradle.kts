rootProject.name = "bit_fileserver"

for (proj in arrayOf("fileserver", "attacker")) {
    include(proj)
    project(":$proj").projectDir = file("projects/$proj")
}
