package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Arrays;
import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftLoginWithXBoxPayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XBLAuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizePayload;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;

public class MicrosoftLoginRoutine {
	
	public static MicrosoftLoginResponse loginWithAuthCode(String authCode, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromCode(authCode, timeoutValues), timeoutValues);
	}
	
	public static MicrosoftLoginResponse loginWithAuthCode(String clientId, String redirectUrl, String authCode, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromCode(clientId, redirectUrl, authCode, timeoutValues), timeoutValues);
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String refreshToken, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(refreshToken, timeoutValues), timeoutValues);
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String clientId, String redirectUrl, String refreshToken, TimeoutValues timeoutValues) {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(clientId, redirectUrl, refreshToken, timeoutValues), timeoutValues);
	}
	
	private static MicrosoftLoginResponse login(MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponse, TimeoutValues timeoutValues) {
		if (oAuthResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get oAuth token", oAuthResponse.getException().get()), Optional.empty());
		} else if (oAuthResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get oAuth token because: " + oAuthResponse.getErrorResponse().get()), Optional.empty());
		}
		
		final var xblResponse = MicrosoftService.xblAuthenticate(new XBLAuthenticatePayload(new XBLAuthenticatePayload.Properties("RPS", "user.auth.xboxlive.com", "d=" + oAuthResponse.getResponse().get().accessToken()), "http://auth.xboxlive.com", "JWT"), timeoutValues);
		if (xblResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authenticate with xbl", xblResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		} else if (xblResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authenticate with xbl because the service returned http code " + xblResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		}
		
		final var xstsResponse = MicrosoftService.xstsAuthorize(new XSTSAuthorizePayload(new XSTSAuthorizePayload.Properties("RETAIL", Arrays.asList(xblResponse.getResponse().get().getToken())), "rp://api.minecraftservices.com/", "JWT"), timeoutValues);
		if (xstsResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authorize with xsts", xstsResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		} else if (xstsResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authorize with xsts because: " + xstsResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		}
		
		final var minecraftLoginResponse = MicrosoftService.minecraftLoginWithXsts(new MinecraftLoginWithXBoxPayload("XBL3.0 x=" + xstsResponse.getResponse().get().getDisplayClaims().getXui().get(0).getUhs() + ";" + xstsResponse.getResponse().get().getToken()), timeoutValues);
		if (minecraftLoginResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot login into minecraft with xbox", minecraftLoginResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		} else if (minecraftLoginResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot login into minecraft with xbox because the service returned http code " + minecraftLoginResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		}
		
		final var minecraftHasPurchasedResponse = MicrosoftService.minecraftHasPurchased(minecraftLoginResponse.getResponse().get().getAccessToken(), timeoutValues);
		if (minecraftHasPurchasedResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get purchase data for minecraft", minecraftHasPurchasedResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		} else if (minecraftHasPurchasedResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get purchase data for minecraft because the service returned http code " + minecraftHasPurchasedResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		}
		
		// Check if minecraft has been bought
		if (minecraftHasPurchasedResponse.getResponse().get().getItems().size() == 0) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("This account does not have bought minecraft"), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		}
		
		final var minecraftProfileResponse = MicrosoftService.minecraftProfile(minecraftLoginResponse.getResponse().get().getAccessToken(), timeoutValues);
		if (minecraftProfileResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get minecraft profile data", minecraftProfileResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		} else if (minecraftProfileResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get minecraft profile data because the service returned http code " + minecraftProfileResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::refreshToken));
		}
		
		return MicrosoftLoginResponse.ofSuccess(new User(minecraftProfileResponse.getResponse().get().getId(), minecraftProfileResponse.getResponse().get().getName(), minecraftLoginResponse.getResponse().get().getAccessToken(), "msa"), oAuthResponse.getResponse().get().refreshToken());
	}
}
