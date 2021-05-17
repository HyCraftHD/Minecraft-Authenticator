package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Arrays;

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

public class MicrosoftLoginRoutine {
	
	public static MicrosoftLoginResponse loginWithAuthCode(String authCode) throws MicrosoftAuthenticationException {
		return login(MicrosoftService.oAuthTokenFromCode(authCode));
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String refreshToken) throws MicrosoftAuthenticationException {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(refreshToken));
	}
	
	private static MicrosoftLoginResponse login(MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponse) throws MicrosoftAuthenticationException {
		if (oAuthResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token", oAuthResponse.getException().get(), null);
		} else if (oAuthResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot get oAuth token because: " + oAuthResponse.getErrorResponse().get(), null);
		}
		
		final MicrosoftResponse<XBLAuthenticateResponse, Integer> xblResponse = MicrosoftService.xblAuthenticate(new XBLAuthenticatePayload(new XBLAuthenticatePayload.Properties("RPS", "user.auth.xboxlive.com", "d=" + oAuthResponse.getResponse().get().getAccessToken()), "http://auth.xboxlive.com", "JWT"));
		if (xblResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot authenticate with xbl", xblResponse.getException().get(), oAuthResponse.getResponse().get().getRefreshToken());
		} else if (xblResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot authenticate with xbl because the service returned http code " + xblResponse.getErrorResponse().get(), oAuthResponse.getResponse().get().getRefreshToken());
		}
		
		final MicrosoftResponse<XSTSAuthorizeResponse, XSTSAuthorizeErrorResponse> xstsResponse = MicrosoftService.xstsAuthorize(new XSTSAuthorizePayload(new XSTSAuthorizePayload.Properties("RETAIL", Arrays.asList(xblResponse.getResponse().get().getToken())), "rp://api.minecraftservices.com/", "JWT"));
		if (xstsResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot authorize with xsts", xstsResponse.getException().get(), oAuthResponse.getResponse().get().getRefreshToken());
		} else if (xstsResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot authorize with xsts because: " + xstsResponse.getErrorResponse().get(), oAuthResponse.getResponse().get().getRefreshToken());
		}
		
		final MicrosoftResponse<MinecraftLoginWithXBoxResponse, Integer> minecraftLoginResponse = MicrosoftService.minecraftLoginWithXsts(new MinecraftLoginWithXBoxPayload("XBL3.0 x=" + xstsResponse.getResponse().get().getDisplayClaims().getXui().get(0).getUhs() + ";" + xstsResponse.getResponse().get().getToken()));
		if (minecraftLoginResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot login into minecraft with xbox", minecraftLoginResponse.getException().get(), oAuthResponse.getResponse().get().getRefreshToken());
		} else if (minecraftLoginResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot login into minecraft with xbox because the service returned http code " + minecraftLoginResponse.getErrorResponse().get(), oAuthResponse.getResponse().get().getRefreshToken());
		}
		
		final MicrosoftResponse<MinecraftHasPurchasedResponse, Integer> minecraftHasPurchasedResponse = MicrosoftService.minecraftHasPurchased(minecraftLoginResponse.getResponse().get().getAccessToken());
		if (minecraftHasPurchasedResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot get purchase data for minecraft", minecraftHasPurchasedResponse.getException().get(), oAuthResponse.getResponse().get().getRefreshToken());
		} else if (minecraftHasPurchasedResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot get purchase data for minecraft because the service returned http code " + minecraftHasPurchasedResponse.getErrorResponse().get(), oAuthResponse.getResponse().get().getRefreshToken());
		}
		
		// Check if minecraft has been bought
		if (minecraftHasPurchasedResponse.getResponse().get().getItems().size() == 0) {
			throw new MicrosoftAuthenticationException("This account does not have bought minecraft", oAuthResponse.getResponse().get().getRefreshToken());
		}
		
		final MicrosoftResponse<MinecraftProfileResponse, Integer> minecraftProfileResponse = MicrosoftService.minecraftProfile(minecraftLoginResponse.getResponse().get().getAccessToken());
		if (minecraftProfileResponse.hasException()) {
			throw new MicrosoftAuthenticationException("Cannot get minecraft profile data", minecraftProfileResponse.getException().get(), oAuthResponse.getResponse().get().getRefreshToken());
		} else if (minecraftProfileResponse.hasErrorResponse()) {
			throw new MicrosoftAuthenticationException("Cannot get minecraft profile data because the service returned http code " + minecraftProfileResponse.getErrorResponse().get(), oAuthResponse.getResponse().get().getRefreshToken());
		}
		
		return new MicrosoftLoginResponse(new User(minecraftProfileResponse.getResponse().get().getId(), minecraftProfileResponse.getResponse().get().getName(), minecraftLoginResponse.getResponse().get().getAccessToken(), "msa"), oAuthResponse.getResponse().get().getRefreshToken());
	}
	
}
