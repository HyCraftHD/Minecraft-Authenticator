# Minecraft Authenticator

#### If you need mojang authentication or java 8 support please have a look at branch [1.x](https://github.com/HyCraftHD/Minecraft-Authenticator/tree/1.x) and [2.x](https://github.com/HyCraftHD/Minecraft-Authenticator/tree/2.x)

A minecraft authentication library that allows microsoft ([xbox live](https://wiki.vg/Microsoft_Authentication_Scheme)) accounts to be logged in and returns a minecraft profile with an access token as well as xbox profile settings.
This library also allows for storage of the authentication data and therefore sessions can be refreshed without a new login on the users side.

# Building

To build just run ``./gradlew build``. You will find the jars in the build/libs directory.
This project requires gson and java 17 as dependency.

# Include in your own project

To include this project you can use the maven build of this project which will resolve all required dependencies automatically.
The latest version is the latest tag in github.

## Gradle
```gradle
repositories {
	maven {
		url = "https://repo.u-team.info"
	}
}

dependencies {
	implementation "net.hycrafthd:minecraft_authenticator:XYZ"
}
```
## Maven
```xml
<repositories>
  <repository>
    <id>u-team-repo</id>
    <url>https://repo.u-team.info/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>net.hycrafthd</groupId>
    <artifactId>minecraft_authenticator</artifactId>
    <version>3.0.2</version>
  </dependency>
</dependencies>
```

# Usage

The main public facing api is the [Authenticator](src/main/java/net/hycrafthd/minecraft_authenticator/login/Authenticator.java) class. 
This class is documented and you should have a look here about more information. The following code snippets are just some simple usage demonstrations.

### Here is a simple login with microsoft

```java
// Build authenticator
final Authenticator authenticator = Authenticator.ofMicrosoft(authorizationCode).shouldAuthenticate().build();
try {
	// Run authentication
	authenticator.run();
} catch (final AuthenticationException ex) {
	// Always check if result file is present when an exception is thrown
	final AuthenticationFile file = authenticator.getResultFile();
	if (file != null) {
		// Save authentication file
		file.writeCompressed(outputStream);
	}
	
	// Show user error or rethrow
	throw ex;
}

// Save authentication file
final AuthenticationFile file = authenticator.getResultFile();
file.writeCompressed(outputStream);

// Get user
final Optional<User> user = authenticator.getUser();
```

### Here is an login with an existing authentication file to refresh the session

```java
// Build authenticator
final Authenticator authenticator = Authenticator.of(existingAuthFile).shouldAuthenticate().build();
try {
	// Run authentication
	authenticator.run();
} catch (final AuthenticationException ex) {
	// Always check if result file is present when an exception is thrown
	final AuthenticationFile file = authenticator.getResultFile();
	if (file != null) {
		// Save authentication file
		file.writeCompressed(outputStream);
	}
	
	// Show user error or rethrow
	throw ex;
}

// Save authentication file
final AuthenticationFile file = authenticator.getResultFile();
file.writeCompressed(outputStream);

// Get user
final Optional<User> user = authenticator.getUser();
```

### Here is a login with a custom azure application and the retrieval of xbox profile settings

```java
// Build authenticator
final Authenticator authenticator = Authenticator.ofMicrosoft(authorizationCode)
	.customAzureApplication(clientId, redirectUrl)
	.shouldRetrieveXBoxProfile()
	.shouldAuthenticate()
	.build();
try {
	// Run authentication
	authenticator.run();
} catch (final AuthenticationException ex) {
	// Always check if result file is present when an exception is thrown
	final AuthenticationFile file = authenticator.getResultFile();
	if (file != null) {
		// Save authentication file
		file.writeCompressed(outputStream);
	}
	
	// Show user error or rethrow
	throw ex;
}

// Save authentication file
final AuthenticationFile file = authenticator.getResultFile();
file.writeCompressed(outputStream);

// Get user
final Optional<User> user = authenticator.getUser();

// Get XBox profile
final Optional<XBoxProfile> xBoxProfile = authenticator.getXBoxProfile();
```

# License

This project is licensed under apache 2 license. For more information see [here](LICENSE).
