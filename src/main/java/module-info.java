module net.hycrafthd.minecraft_authenticator {
	
	exports net.hycrafthd.minecraft_authenticator;
	exports net.hycrafthd.minecraft_authenticator.login;
	exports net.hycrafthd.minecraft_authenticator.util.function;
	
	requires transitive com.google.gson;
	
	opens net.hycrafthd.minecraft_authenticator.microsoft.api to com.google.gson;
}
