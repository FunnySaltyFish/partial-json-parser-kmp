//tasks.findByPath(":partial-json-parser:publishAndroidReleasePublicationToSonatypeRepository")!!.run {
//    dependsOn(":partial-json-parser:signAndroidDebugPublication")
//    dependsOnSign()
//}
//
//tasks.findByPath(":partial-json-parser:publishAndroidDebugPublicationToSonatypeRepository")!!.run {
//    dependsOn(":partial-json-parser:signAndroidReleasePublication")
//    dependsOnSign()
//}
//
//fun Task.dependsOnSign() {
//    dependsOn(":partial-json-parser:signJsPublication")
//}

