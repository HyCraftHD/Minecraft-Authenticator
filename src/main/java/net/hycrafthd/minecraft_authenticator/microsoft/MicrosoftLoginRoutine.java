package net.hycrafthd.minecraft_authenticator.microsoft;

import java.util.Arrays;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
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
	
	public static MicrosoftLoginResponse loginWithAuthCode(String authCode) throws AuthenticationException {
		return login(MicrosoftService.oAuthTokenFromCode(authCode));
	}
	
	public static MicrosoftLoginResponse loginWithRefreshToken(String refreshToken) throws AuthenticationException {
		return login(MicrosoftService.oAuthTokenFromRefreshToken(refreshToken));
	}
	
	private static MicrosoftLoginResponse login(MicrosoftResponse<OAuthTokenResponse, OAuthErrorResponse> oAuthResponse) throws AuthenticationException {
		if (oAuthResponse.hasException()) {
			throw new AuthenticationException("Cannot get oAuth token", oAuthResponse.getException().get());
		} else if (oAuthResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot get oAuth token because: " + oAuthResponse.getErrorResponse().get());
		}
		
		final MicrosoftResponse<XBLAuthenticateResponse, Integer> xblResponse = MicrosoftService.xblAuthenticate(new XBLAuthenticatePayload(new XBLAuthenticatePayload.Properties("RPS", "user.auth.xboxlive.com", "d=" + oAuthResponse.getResponse().get().getAccessToken()), "http://auth.xboxlive.com", "JWT"));
		if (xblResponse.hasException()) {
			throw new AuthenticationException("Cannot authenticate with xbl", xblResponse.getException().get());
		} else if (xblResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot authenticate with xbl because the service returned http code " + xblResponse.getErrorResponse().get());
		}
		
		final MicrosoftResponse<XSTSAuthorizeResponse, XSTSAuthorizeErrorResponse> xstsResponse = MicrosoftService.xstsAuthorize(new XSTSAuthorizePayload(new XSTSAuthorizePayload.Properties("RETAIL", Arrays.asList(xblResponse.getResponse().get().getToken())), "rp://api.minecraftservices.com/", "JWT"));
		if (xstsResponse.hasException()) {
			throw new AuthenticationException("Cannot authorize with xsts", xstsResponse.getException().get());
		} else if (xstsResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot authorize with xsts because: " + xstsResponse.getErrorResponse().get());
		}
		
		final MicrosoftResponse<MinecraftLoginWithXBoxResponse, Integer> minecraftLoginResponse = MicrosoftService.minecraftLoginWithXsts(new MinecraftLoginWithXBoxPayload("XBL3.0 x=" + xstsResponse.getResponse().get().getDisplayClaims().getXui().get(0).getUhs() + ";" + xstsResponse.getResponse().get().getToken()));
		if (minecraftLoginResponse.hasException()) {
			throw new AuthenticationException("Cannot login into minecraft with xbox", minecraftLoginResponse.getException().get());
		} else if (minecraftLoginResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot login into minecraft with xbox because the service returned http code " + minecraftLoginResponse.getErrorResponse().get());
		}
		
		final MicrosoftResponse<MinecraftHasPurchasedResponse, Integer> minecraftHasPurchasedResponse = MicrosoftService.minecraftHasPurchased(minecraftLoginResponse.getResponse().get().getAccessToken());
		if (minecraftHasPurchasedResponse.hasException()) {
			throw new AuthenticationException("Cannot get purchase data for minecraft", minecraftHasPurchasedResponse.getException().get());
		} else if (minecraftHasPurchasedResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot get purchase data for minecraft because the service returned http code " + minecraftHasPurchasedResponse.getErrorResponse().get());
		}
		
		// Check if minecraft has been bought
		if (minecraftHasPurchasedResponse.getResponse().get().getItems().size() == 0) {
			throw new AuthenticationException("This account does not have bought minecraft");
		}
		
		final MicrosoftResponse<MinecraftProfileResponse, Integer> minecraftProfileResponse = MicrosoftService.minecraftProfile(minecraftLoginResponse.getResponse().get().getAccessToken());
		if (minecraftProfileResponse.hasException()) {
			throw new AuthenticationException("Cannot get minecraft profile data", minecraftProfileResponse.getException().get());
		} else if (minecraftProfileResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot get minecraft profile data because the service returned http code " + minecraftProfileResponse.getErrorResponse().get());
		}
		
		return new MicrosoftLoginResponse(new User(minecraftProfileResponse.getResponse().get().getId(), minecraftProfileResponse.getResponse().get().getName(), minecraftLoginResponse.getResponse().get().getAccessToken(), "msa"), oAuthResponse.getResponse().get().getRefreshToken());
	}
	
}
