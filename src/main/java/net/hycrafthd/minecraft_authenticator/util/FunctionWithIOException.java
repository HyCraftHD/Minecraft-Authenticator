package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;

@FunctionalInterface
public interface FunctionWithIOException<T, R> {
	
	R apply(T t) throws IOException;
}
