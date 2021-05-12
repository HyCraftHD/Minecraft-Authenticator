package net.hycrafthd.minecraft_authenticator.yggdrasil;

import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.yggdrasil.api.ErrorResponse;

public class YggdrasilResponse<T> {
	
	private final Optional<T> response;
	private final Optional<ErrorResponse> errorResponse;
	private final Optional<Throwable> exception;
	
	public YggdrasilResponse(T response) {
		this.response = Optional.of(response);
		errorResponse = Optional.empty();
		exception = Optional.empty();
	}
	
	public YggdrasilResponse(ErrorResponse error) {
		response = Optional.empty();
		this.errorResponse = Optional.of(error);
		exception = Optional.empty();
	}
	
	public YggdrasilResponse(Throwable exception) {
		response = Optional.empty();
		errorResponse = Optional.empty();
		this.exception = Optional.of(exception);
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
