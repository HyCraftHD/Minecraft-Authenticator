package net.hycrafthd.minecraft_authenticator.microsoft.service;

import java.util.Optional;

public class MicrosoftResponse<T, E> {
	
	public static <T, E> MicrosoftResponse<T, E> ofResponse(T response) {
		return new MicrosoftResponse<>(Optional.ofNullable(response), Optional.empty(), Optional.empty());
	}
	
	public static <T, E> MicrosoftResponse<T, E> ofError(E error) {
		return new MicrosoftResponse<>(Optional.empty(), Optional.ofNullable(error), Optional.empty());
	}
	
	public static <T, E> MicrosoftResponse<T, E> ofException(Throwable exception) {
		return new MicrosoftResponse<>(Optional.empty(), Optional.empty(), Optional.ofNullable(exception));
	}
	
	private final Optional<T> response;
	private final Optional<E> errorResponse;
	private final Optional<Throwable> exception;
	
	private MicrosoftResponse(Optional<T> response, Optional<E> errorResponse, Optional<Throwable> exception) {
		this.response = response;
		this.errorResponse = errorResponse;
		this.exception = exception;
	}
	
	public boolean hasSucessfulResponse() {
		return response.isPresent();
	}
	
	public Optional<T> getResponse() {
		return response;
	}
	
	public boolean hasErrorResponse() {
		return errorResponse.isPresent();
	}
	
	public Optional<E> getErrorResponse() {
		return errorResponse;
	}
	
	public boolean hasException() {
		return exception.isPresent();
	}
	
	public Optional<Throwable> getException() {
		return exception;
	}
	
}
