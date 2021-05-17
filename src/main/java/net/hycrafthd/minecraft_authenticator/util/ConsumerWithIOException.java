package net.hycrafthd.minecraft_authenticator.util;

import java.io.IOException;

@FunctionalInterface
public interface ConsumerWithIOException<T> {
	
	void accept(T t) throws IOException;
}
