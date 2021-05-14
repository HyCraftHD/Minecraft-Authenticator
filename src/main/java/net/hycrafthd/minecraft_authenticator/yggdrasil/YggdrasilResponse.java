package net.hycrafthd.minecraft_authenticator.yggdrasil;

import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ErrorResponse;

public class YggdrasilResponse<T> {
	
	public static <T> YggdrasilResponse<T> ofResponse(T response) {
		return new YggdrasilResponse<>(Optional.of(response), Optional.empty(), Optional.empty());
	}
	
	public static <T> YggdrasilResponse<T> ofError(ErrorResponse error) {
		return new YggdrasilResponse<>(Optional.empty(), Optional.of(error), Optional.empty());
	}
	
	public static <T, E> YggdrasilResponse<T> ofException(Throwable exception) {
		return new YggdrasilResponse<>(Optional.empty(), Optional.empty(), Optional.of(exception));
	}
	
	private final Optional<T> response;
	private final Optional<ErrorResponse> errorResponse;
	private final Optional<Throwable> exception;
	
	private YggdrasilResponse(Optional<T> response, Optional<ErrorResponse> errorResponse, Optional<Throwable> exception) {
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
	
	public Optional<ErrorResponse> getErrorResponse() {
		return errorResponse;
	}
	
	public boolean hasException() {
		return exception.isPresent();
	}
	
	public Optional<Throwable> getException() {
		return exception;
	}
	
}
