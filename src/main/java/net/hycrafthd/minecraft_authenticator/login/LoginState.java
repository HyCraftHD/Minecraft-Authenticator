package net.hycrafthd.minecraft_authenticator.login;

/**
 * Login states used for callback in {@link Authenticator}
 */
public enum LoginState {
	
	/**
	 * Resolve inital authentication file or get refresh token when file does not exists
	 */
	INITAL_FILE,
	/**
	 * Login to microsoft
	 */
	LOGIN_MICOSOFT,
	/**
	 * Authenticate to xbox live
	 */
	XBL_TOKEN,
	/**
	 * Authorize to minecraft services
	 */
	XSTS_TOKEN,
	/**
	 * Retrieve access token for minecraft
	 */
	ACCESS_TOKEN,
	/**
	 * Check for entitlement of account
	 */
	ENTITLEMENT,
	/**
	 * Retrieve minecraft profile
	 */
	PROFILE,
	/**
	 * Retrieve xbox profile
	 */
	XBOX_PROFILE
}
