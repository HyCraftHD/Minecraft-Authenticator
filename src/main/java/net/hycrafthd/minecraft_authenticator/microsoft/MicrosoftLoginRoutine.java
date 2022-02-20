package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;
import net.hycrafthd.minecraft_authenticator.util.ParseUtil;

public class MicrosoftLoginRoutine {
	
	public static MicrosoftLoginResponse loginWithAuthCode(String authCode, UUID launcherClientId, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromCode(authCode, timeoutValues), launcherClientId, timeoutValues);
	}
	
	public static MicrosoftLoginResponse loginWithAuthCode(String clientId, String redirectUrl, String authCode, UUID launcherClientId, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromCode(clientId, redirectUrl, authCode, timeoutValues), launcherClientId, timeoutValues);
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String refreshToken, UUID launcherClientId, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(refreshToken, timeoutValues), launcherClientId, timeoutValues);
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String clientId, String redirectUrl, String refreshToken, UUID launcherClientId, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(clientId, redirectUrl, refreshToken, timeoutValues), launcherClientId, timeoutValues);
	}
	
	private static MicrosoftLoginResponse login(MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponse, UUID launcherClientId, TimeoutValues timeoutValues) {
		if (oAuthResponse.hasException()) {
			return exception("Cannot get oAuth token", oAuthResponse.getException().get(), Optional.empty());
		} else if (oAuthResponse.hasErrorResponse()) {
			return exception("Cannot get oAuth token because: " + oAuthResponse.getErrorResponse().get(), Optional.empty());
		}
		final var oAuth = successResponse(oAuthResponse);
		
		final var xblResponse = MicrosoftService.xblAuthenticate(oAuth.getAccessToken(), timeoutValues);
		if (xblResponse.hasException()) {
			return exception("Cannot authenticate with xbl", xblResponse.getException().get(), oAuth);
		} else if (xblResponse.hasErrorResponse()) {
			return exception("Cannot authenticate with xbl because the service returned http code " + xblResponse.getErrorResponse().get(), oAuth);
		}
		final var xbl = successResponse(xblResponse);
		
		final var xBoxXstsResponse = MicrosoftService.xstsAuthorize(xbl.getToken(), "http://xboxlive.com", xbl.getDisplayClaims(), timeoutValues);
		if (xBoxXstsResponse.hasException()) {
			return exception("Cannot authorize with xsts for xbox services", xBoxXstsResponse.getException().get(), oAuth);
		} else if (xBoxXstsResponse.hasErrorResponse()) {
			return exception("Cannot authorize with xsts for xbox services because: " + xBoxXstsResponse.getErrorResponse().get(), oAuth);
		}
		final var xBoxXsts = successResponse(xBoxXstsResponse);
		
		final var minecraftXstsResponse = MicrosoftService.xstsAuthorize(xbl.getToken(), "rp://api.minecraftservices.com/", xbl.getDisplayClaims(), timeoutValues);
		if (minecraftXstsResponse.hasException()) {
			return exception("Cannot authorize with xsts for minecraft services", minecraftXstsResponse.getException().get(), oAuth);
		} else if (minecraftXstsResponse.hasErrorResponse()) {
			return exception("Cannot authorize with xsts for minecraft services because: " + minecraftXstsResponse.getErrorResponse().get(), oAuth);
		}
		final var minecraftXsts = successResponse(minecraftXstsResponse);
		
		final var minecraftLoginResponse = MicrosoftService.minecraftLaucherLogin(minecraftXsts.getToken(), minecraftXsts.getDisplayClaims(), timeoutValues);
		if (minecraftLoginResponse.hasException()) {
			return exception("Cannot login into minecraft with xbox", minecraftLoginResponse.getException().get(), oAuth);
		} else if (minecraftLoginResponse.hasErrorResponse()) {
			return exception("Cannot login into minecraft with xbox because the service returned http code " + minecraftLoginResponse.getErrorResponse().get(), oAuth);
		}
		final var minecraftLogin = successResponse(minecraftLoginResponse);
		
		final var minecraftHasPurchasedResponse = MicrosoftService.minecraftHasPurchased(minecraftLogin.getAccessToken(), launcherClientId, timeoutValues);
		if (minecraftHasPurchasedResponse.hasException()) {
			return exception("Cannot get purchase data for minecraft", minecraftHasPurchasedResponse.getException().get(), oAuth);
		} else if (minecraftHasPurchasedResponse.hasErrorResponse()) {
			return exception("Cannot get purchase data for minecraft because the service returned http code " + minecraftHasPurchasedResponse.getErrorResponse().get(), oAuth);
		}
		final var minecraftHasPurchased = successResponse(minecraftHasPurchasedResponse);
		
		// Check if minecraft has been bought
		if (minecraftHasPurchased.getItems().size() == 0) {
			return exception("This account does not have bought minecraft", oAuth);
		}
		
		final var minecraftProfileResponse = MicrosoftService.minecraftProfile(minecraftLogin.getAccessToken(), timeoutValues);
		if (minecraftProfileResponse.hasException()) {
			return exception("Cannot get minecraft profile data", minecraftProfileResponse.getException().get(), oAuth);
		} else if (minecraftProfileResponse.hasErrorResponse()) {
			return exception("Cannot get minecraft profile data because the service returned http code " + minecraftProfileResponse.getErrorResponse().get(), oAuth);
		}
		final var minecraftProfile = successResponse(minecraftProfileResponse);
		
		// TODO call this only when requested and save data some where
		MicrosoftService.xboxProfile(xBoxXsts.getToken(), xBoxXsts.getDisplayClaims(), timeoutValues);
		
		// Parse minecraft access token and extract xuid
		final String xuid;
		try {
			final String jwt = ParseUtil.decodeJWT(minecraftLogin.getAccessToken());
			final JsonObject payload = JsonParser.parseString(jwt).getAsJsonObject();
			
			xuid = payload.get("xuid").getAsString();
		} catch (final Exception ex) {
			return exception("Could not parse minecraft access token", oAuth);
		}
		
		// Encode client id
		final String clientId = ParseUtil.encodeBase64(launcherClientId.toString());
		
		return MicrosoftLoginResponse.ofSuccess(new User(minecraftProfile.getId(), minecraftProfile.getName(), minecraftLogin.getAccessToken(), "msa", xuid, clientId), oAuth.getRefreshToken());
	}
	
	// Helper methods
	
	private static <T> T successResponse(MicrosoftResponse<T, ?> response) {
		return response.getResponse().get();
	}
	
	private static MicrosoftLoginResponse exception(String message, Throwable throwable, OAuthTokenResponse oAuth) {
		return exception(message, throwable, Optional.of(oAuth.getRefreshToken()));
	}
	
	private static MicrosoftLoginResponse exception(String message, OAuthTokenResponse oAuth) {
		return exception(message, Optional.of(oAuth.getRefreshToken()));
	}
	
	private static MicrosoftLoginResponse exception(String message, Throwable throwable, Optional<String> refreshToken) {
		return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException(message, throwable), refreshToken);
	}
	
	private static MicrosoftLoginResponse exception(String message, Optional<String> refreshToken) {
		return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException(message), refreshToken);
	}
}
