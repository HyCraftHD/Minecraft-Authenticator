package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Arrays;
import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftHasPurchasedResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftLoginWithXBoxPayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftLoginWithXBoxResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.MinecraftProfileResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.OAuthTokenResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XBLAuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XBLAuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizeErrorResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizePayload;
import net.hycrafthd.minecraft_authenticator.microsoft.api.XSTSAuthorizeResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;

public class MicrosoftLoginRoutine {
	
	public static MicrosoftLoginResponse loginWithAuthCode(String authCode) {
		return login(MicrosoftService.oAuthTokenFromCode(authCode));
	}
	
	public static MicrosoftLoginResponse loginWithAuthCode(String clientId, String redirectUrl, String authCode) {
		return login(MicrosoftService.oAuthTokenFromCode(clientId, redirectUrl, authCode));
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String refreshToken) {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(refreshToken));
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String clientId, String redirectUrl, String refreshToken) {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(clientId, redirectUrl, refreshToken));
	}
	
	private static MicrosoftLoginResponse login(MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponse) {
		if (oAuthResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get oAuth token", oAuthResponse.getException().get()), Optional.empty());
		} else if (oAuthResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get oAuth token because: " + oAuthResponse.getErrorResponse().get()), Optional.empty());
		}
		
		final MicrosoftResponse<XBLAuthenticateResponse, Integer> xblResponse = MicrosoftService.xblAuthenticate(new XBLAuthenticatePayload(new XBLAuthenticatePayload.Properties("RPS", "user.auth.xboxlive.com", "d=" + oAuthResponse.getResponse().get().getAccessToken()), "http://auth.xboxlive.com", "JWT"));
		if (xblResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authenticate with xbl", xblResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		} else if (xblResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authenticate with xbl because the service returned http code " + xblResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		}
		
		final MicrosoftResponse<XSTSAuthorizeResponse, XSTSAuthorizeErrorResponse> xstsResponse = MicrosoftService.xstsAuthorize(new XSTSAuthorizePayload(new XSTSAuthorizePayload.Properties("RETAIL", Arrays.asList(xblResponse.getResponse().get().getToken())), "rp://api.minecraftservices.com/", "JWT"));
		if (xstsResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authorize with xsts", xstsResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		} else if (xstsResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot authorize with xsts because: " + xstsResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		}
		
		final MicrosoftResponse<MinecraftLoginWithXBoxResponse, Integer> minecraftLoginResponse = MicrosoftService.minecraftLoginWithXsts(new MinecraftLoginWithXBoxPayload("XBL3.0 x=" + xstsResponse.getResponse().get().getDisplayClaims().getXui().get(0).getUhs() + ";" + xstsResponse.getResponse().get().getToken()));
		if (minecraftLoginResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot login into minecraft with xbox", minecraftLoginResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		} else if (minecraftLoginResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot login into minecraft with xbox because the service returned http code " + minecraftLoginResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		}
		
		final MicrosoftResponse<MinecraftHasPurchasedResponse, Integer> minecraftHasPurchasedResponse = MicrosoftService.minecraftHasPurchased(minecraftLoginResponse.getResponse().get().getAccessToken());
		if (minecraftHasPurchasedResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get purchase data for minecraft", minecraftHasPurchasedResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		} else if (minecraftHasPurchasedResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get purchase data for minecraft because the service returned http code " + minecraftHasPurchasedResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		}
		
		// Check if minecraft has been bought
		if (minecraftHasPurchasedResponse.getResponse().get().getItems().size() == 0) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("This account does not have bought minecraft"), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		}
		
		final MicrosoftResponse<MinecraftProfileResponse, Integer> minecraftProfileResponse = MicrosoftService.minecraftProfile(minecraftLoginResponse.getResponse().get().getAccessToken());
		if (minecraftProfileResponse.hasException()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get minecraft profile data", minecraftProfileResponse.getException().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		} else if (minecraftProfileResponse.hasErrorResponse()) {
			return MicrosoftLoginResponse.ofError(new MicrosoftAuthenticationException("Cannot get minecraft profile data because the service returned http code " + minecraftProfileResponse.getErrorResponse().get()), oAuthResponse.getResponse().map(OAuthTokenResponse::getRefreshToken));
		}
		
		return MicrosoftLoginResponse.ofSuccess(new User(minecraftProfileResponse.getResponse().get().getId(), minecraftProfileResponse.getResponse().get().getName(), minecraftLoginResponse.getResponse().get().getAccessToken(), "msa"), oAuthResponse.getResponse().get().getRefreshToken());
	}
}
