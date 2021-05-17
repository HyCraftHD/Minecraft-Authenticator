package net.hycrafthd.minecraft_authenticator.yggdrasil;

import net.hycrafthd.minecraft_authenticator.login.AuthenticationException;
import net.hycrafthd.minecraft_authenticator.login.User;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticatePayload.Agent;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.AuthenticateResponse;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.RefreshPayload;
import net.hycrafthd.minecraft_authenticator.yggdrasil.api.RefreshResponse;

public class YggdrasilLoginRoutine {
	
	public static User loginWithUsername(String username, String password, String clientToken) throws AuthenticationException {
		final YggdrasilResponse<AuthenticateResponse> authenticateResponse = YggdrasilService.authenticate(new AuthenticatePayload(new Agent("Minecraft", 1), username, password, clientToken, true));
		if (authenticateResponse.hasException()) {
			throw new AuthenticationException("Cannot authenticate minecraft account", authenticateResponse.getException().get());
		} else if (authenticateResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot authenticate minecraft account because: " + authenticateResponse.getErrorResponse().get());
		}
		
		if (authenticateResponse.getResponse().get().getSelectedProfile() == null) {
			throw new AuthenticationException("This account does not have bought minecraft");
		}
		
		return new User(authenticateResponse.getResponse().get().getSelectedProfile().getId(), authenticateResponse.getResponse().get().getSelectedProfile().getName(), authenticateResponse.getResponse().get().getAccessToken(), "mojang");
	}
	
	public static User loginWithAccessToken(String accessToken, String clientToken) throws AuthenticationException {
		final YggdrasilResponse<RefreshResponse> refreshResponse = YggdrasilService.refresh(new RefreshPayload(accessToken, clientToken, true));
		if (refreshResponse.hasException()) {
			throw new AuthenticationException("Cannot refresh access token", refreshResponse.getException().get());
		} else if (refreshResponse.hasErrorResponse()) {
			throw new AuthenticationException("Cannot refresh access token because: " + refreshResponse.getErrorResponse().get());
		}
		
		if (refreshResponse.getResponse().get().getSelectedProfile() == null) {
			throw new AuthenticationException("This account does not have bought minecraft");
		}
		
		return new User(refreshResponse.getResponse().get().getSelectedProfile().getId(), refreshResponse.getResponse().get().getSelectedProfile().getName(), refreshResponse.getResponse().get().getAccessToken(), "mojang");
	}
}
