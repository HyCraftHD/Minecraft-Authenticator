package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Optional;
import java.util.UUID;

import net.hycrafthd.minecraft_authenticator.login.LoginState;
import net.hycrafthd.minecraft_authenticator.login.Authenticator.LoginStateCallback;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;

public class MicrosoftAuthentication {
	
	public static MicrosoftAuthenticationFile createAuthenticationFile(Optional<AzureApplication> customAzureApplication, String authorizationCode, TimeoutValues timeoutValues) throws MicrosoftAuthenticationException {
		final MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> microsoftResponse;
		if (customAzureApplication.isPresent()) {
			final AzureApplication azureApplication = customAzureApplication.get();
			final String clientId = azureApplication.clientId();
			final String redirectUrl = azureApplication.redirectUrl();
			final String clientSecret = azureApplication.clientSecret();
			
			microsoftResponse = MicrosoftService.oAuthTokenFromCode(clientId, redirectUrl, clientSecret, authorizationCode, timeoutValues);
		} else {
			microsoftResponse = MicrosoftService.oAuthTokenFromCode(authorizationCode, timeoutValues);
		}
		
		if (microsoftResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token", microsoftResponse.getException().get());
		} else if (microsoftResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token because: " + microsoftResponse.getErrorResponse().get());
		}
		final OAuthTokenResponse response = microsoftResponse.getResponse().get();
		return new MicrosoftAuthenticationFile(UUID.randomUUID(), response.getRefreshToken());
	}
	
	public static MicrosoftLoginResponse authenticate(Optional<AzureApplication> customAzureApplication, boolean retrieveXBoxProfile, MicrosoftAuthenticationFile file, TimeoutValues timeoutValues, LoginStateCallback callback) {
		callback.call(LoginState.LOGIN_MICOSOFT);
		if (customAzureApplication.isPresent()) {
			final AzureApplication azureApplication = customAzureApplication.get();
			final String clientId = azureApplication.clientId();
			final String redirectUrl = azureApplication.redirectUrl();
			final String clientSecret = azureApplication.clientSecret();
			
			return MicrosoftLoginRoutine.loginWithRefreshToken(clientId, redirectUrl, clientSecret, retrieveXBoxProfile, file.getRefreshToken(), file.getClientId(), timeoutValues, callback);
		} else {
			return MicrosoftLoginRoutine.loginWithRefreshToken(retrieveXBoxProfile, file.getRefreshToken(), file.getClientId(), timeoutValues, callback);
		}
	}
	
}
