# Minecraft Authenticator

A minecraft authentication library that allows mojang ([yggdrasil](https://wiki.vg/Authentication)) and microsoft ([xbox live](https://wiki.vg/Microsoft_Authentication_Scheme)) accounts to be logged in and returns a minecraft profile with an access token.
This library also allows for storage of the authentication data and therefore sessions can be refreshed without a new login on the users side.

# Building

To build just run ``./gradlew build``. You will find the jars in the build/libs directory.
This project requires gson, guava and jopt-simple as dependencies.

# Include in your own project

To include this project you can use the maven build of this project which will resolve all required dependencies automatically.

```gradle
repositories {
    maven { url = "https://repo.u-team.info" }
}

dependencies {
  implementation "net.hycrafthd:minecraft_authenticator:XYZ"
}
```

# License

This project is licensed under apache 2 license. For more information see [here](LICENSE).  
