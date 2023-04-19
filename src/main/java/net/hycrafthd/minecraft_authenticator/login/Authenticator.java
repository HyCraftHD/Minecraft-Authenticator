package net.hycrafthd.minecraft_authenticator.login;

import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

import net.hycrafthd.minecraft_authenticator.Constants;
import net.hycrafthd.minecraft_authenticator.microsoft.AzureApplication;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthentication;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftAuthenticationFile;
import net.hycrafthd.minecraft_authenticator.microsoft.MicrosoftLoginResponse;
import net.hycrafthd.minecraft_authenticator.microsoft.service.MicrosoftService;
import net.hycrafthd.minecraft_authenticator.util.ConnectionUtil.TimeoutValues;

/**
 * <p>
 * Main class to authenticate a user with minecraft services. <br>
 * First some information about microsoft accounts:
 * </p>
 * <p>
 * Microsoft accounts use oAuth. That is why you need to use a browser to login with your microsoft account. You will
 * only get an authorization code which will be used for authentication. After the first login the
 * {@link MicrosoftAuthenticationFile} contains a refresh token which will be used for refreshing the authentication.
 * </p>
 * <p>
 * Even though the {@link AuthenticationFile} does not contain the real login data it contains sensitive data which can
 * be used to authenticate to certain services including your minecraft account. This file should therefore be kept
 * <b>secret</b> and should not be shared with others.
 * </p>
 * <p>
 * Here is an example to on how to login into a microsoft account: <br>
 * <br>
 * This description only covers the basic oAuth for microsoft with the minecraft launcher id. If you want to customize
 * the login you should create a custom azure application and use the custom azure authentication method
 * {@link Builder#customAzureApplication(String, String)}. <br>
 * To log into a microsoft account you need the authorization code that you get after you log into your microsoft
 * account. First you need to open the {@link #microsoftLogin()} url in a browser (or in an integrated browser like
 * javafx) and let the user login. After that you will be redirected to a page where the authorization code is the code
 * url parameter. The url looks like this: https://login.live.com/oauth20_desktop.srf?code=M.XYZTHISISMYCODE
 * </p>
 *
 * <pre>
 * // Build authenticator
 * final Authenticator authenticator = Authenticator.ofMicrosoft(authorizationCode).shouldAuthenticate().build();
 * try {
 * 	// Run authentication
 * 	authenticator.run();
 * } catch (final AuthenticationException ex) {
 * 	// Always check if result file is present when an exception is thrown
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	if (file != null) {
 * 		// Save authentication file
 * 		file.writeCompressed(outputStream);
 * 	}
 * 	
 * 	// Show user error or rethrow
 * 	throw ex;
 * }
 *
 * // Save authentication file
 * final AuthenticationFile file = authenticator.getResultFile();
 * file.writeCompressed(outputStream);
 *
 * // Get user
 * final Optional user = authenticator.getUser();
 * </pre>
 * <p>
 * You get an {@link AuthenticationFile} and an {@link Optional} with a user if there was no error and you called
 * {@link Builder#shouldAuthenticate()} before. After that the {@link AuthenticationFile} should be stored somewhere for
 * reuse. If an error occurred save the {@link AuthenticationFile} as well if it is not null.
 * </p>
 * <p>
 * To refresh a session you can use the saved {@link AuthenticationFile} and login like that:
 * </p>
 *
 * <pre>
 * // Build authenticator
 * final Authenticator authenticator = Authenticator.of(authFile).shouldAuthenticate().build();
 * try {
 * 	// Run authentication
 * 	authenticator.run();
 * } catch (final AuthenticationException ex) {
 * 	// Always check if result file is present when an exception is thrown
 * 	final AuthenticationFile file = authenticator.getResultFile();
 * 	if (file != null) {
 * 		// Save authentication file
 * 		file.writeCompressed(outputStream);
 * 	}
 * 	
 * 	// Show user error or rethrow
 * 	throw ex;
 * }
 *
 * // Save authentication file
 * final AuthenticationFile file = authenticator.getResultFile();
 * file.writeCompressed(outputStream);
 *
 * // Get user
 * final Optional user = authenticator.getUser();
 * </pre>
 * <p>
 * After that save the returned {@link AuthenticationFile} again. The session should stay for a relative long time, but
 * will be destroyed by certain events e.g. other client token, logout of all sessions, etc. The error message will tell
 * you why the user cannot be authenticated. If a token is not valid anymore the user must relogin.
 * </p>
 * <p>
 * If you need xbox profile settings call {@link Builder#shouldRetrieveXBoxProfile()} and
 * {@link Builder#shouldAuthenticate()} when building the {@link Authenticator}. If the login was successful an
 * {@link XBoxProfile} can be retrieved from {@link #getXBoxProfile()}.
 * </p>
 */
public class Authenticator {
	
	/**
	 * Creates an {@link Authenticator} of a {@link AuthenticationFile}.
	 *
	 * @see Authenticator
	 * @param file The {@link AuthenticationFile}
	 * @return A {@link Builder} to configure the authenticator
	 */
	public static Builder of(AuthenticationFile file) {
		return new Builder(timeoutValues -> file);
	}
	
	/**
	 * Creates a microsoft {@link Authenticator} with a microsoft authorization code. See examples in the class javadoc.
	 *
	 * @see Authenticator
	 * @param authorizationCode Microsoft authorization code of the redirect url
	 * @return A {@link Builder} to configure the authenticator
	 */
	public static Builder ofMicrosoft(String authorizationCode) {
		return new Builder((customAzureApplication, timeoutValues) -> MicrosoftAuthentication.createAuthenticationFile(customAzureApplication, authorizationCode, timeoutValues));
	}
	
	/**
	 * Returns the minecraft launcher oAuth login url for microsoft accounts. After the browser login the authorization code
	 * can be extracted from the redirect url.
	 *
	 * @see Authenticator
	 * @see Authenticator#microsoftLoginRedirect()
	 * @return oAuth microsoft login url
	 */
	public static URL microsoftLogin() {
		return MicrosoftService.oAuthLoginUrl();
	}
	
	/**
	 * Return the minecraft launcher oAuth redirect url. This returns the start of the redirect url and should be used to
	 * match the redirect url and then to extract the authorization code.
	 *
	 * @see Authenticator
	 * @return oAuth microsoft redirect url
	 */
	public static String microsoftLoginRedirect() {
		return Constants.MICROSOFT_OAUTH_REDIRECT_URL;
	}
	
	/**
	 * Returns the oAuth login url for your custom azure application. You need to extract the authorization code of the
	 * redirect url (depends on how you setup your azure application). If you don't want to use a custom azure application
	 * look at {@link Authenticator#microsoftLogin()}.
	 *
	 * @see Authenticator
	 * @param clientId The azure client id
	 * @param redirectUrl The configured redirect url
	 * @return oAuth microsoft login url
	 */
	public static URL microsoftLogin(String clientId, String redirectUrl) {
		return MicrosoftService.oAuthLoginUrl(clientId, redirectUrl);
	}
	
	/**
	 * Internal builder class
	 */
	public static class Builder {
		
		private final AuthenticationFileFunctionWithCustomAzureApplication fileFunction;
		private boolean authenticate;
		private boolean retrieveXBoxProfile;
		private Optional<AzureApplication> customAzureApplication;
		private int serviceConnectTimeout;
		private int serviceReadTimeout;
		
		/**
		 * Accepts a {@link AuthenticationFileFunction} which is just a normal supplier for an {@link AuthenticationFile} which
		 * can throw an {@link AuthenticationException}
		 *
		 * @param fileSupplier Supplier that returns {@link AuthenticationFile} for authentication
		 */
		protected Builder(AuthenticationFileFunction fileSupplier) {
			this((customAzureApplication, timeoutValues) -> fileSupplier.get(timeoutValues));
		}
		
		/**
		 * Accepts a {@link AuthenticationFileFunctionWithCustomAzureApplication} which supplies the custom azure application
		 * values and returns a {@link AuthenticationFile} which can throw an {@link AuthenticationException}
		 *
		 * @param fileFunction Function that returns {@link AuthenticationFile} for authentication
		 */
		protected Builder(AuthenticationFileFunctionWithCustomAzureApplication fileFunction) {
			this.fileFunction = fileFunction;
			authenticate = false;
			customAzureApplication = Optional.empty();
			serviceConnectTimeout = 15000;
			serviceReadTimeout = 15000;
		}
		
		/**
		 * Call this if you want to get a {@link User} object and authenticate to minecraft services
		 *
		 * @return This builder
		 */
		public Builder shouldAuthenticate() {
			authenticate = true;
			return this;
		}
		
		/**
		 * Call this if you want to get a {@link XBoxProfile} object. Only available for microsoft accounts. Only has an effect
		 * if {@link #shouldAuthenticate()} is called.
		 *
		 * @return This builder
		 */
		public Builder shouldRetrieveXBoxProfile() {
			retrieveXBoxProfile = true;
			return this;
		}
		
		/**
		 * Call this if you have a custom azure application that will handle the oauth for microsoft accounts
		 *
		 * @param clientId The azure client id
		 * @param redirectUrl The redirect url
		 * @return This builder
		 */
		public Builder customAzureApplication(String clientId, String redirectUrl) {
			customAzureApplication = Optional.of(new AzureApplication(clientId, redirectUrl));
			return this;
		}
		
		/**
		 * Call this if you have a custom azure application that will handle the oauth for microsoft accounts
		 *
		 * @param clientId The azure client id
		 * @param redirectUrl The redirect url
		 * @param clientSecret The client secret
		 * @return This builder
		 */
		public Builder customAzureApplication(String clientId, String redirectUrl, String clientSecret) {
			customAzureApplication = Optional.of(new AzureApplication(clientId, redirectUrl, clientSecret));
			return this;
		}
		
		/**
		 * Configure the connect timeout of a service request. This timeout configures
		 * {@link URLConnection#setConnectTimeout(int)} to the passed value for each service request
		 *
		 * @param timeout Timeout in milliseconds
		 * @return This builder
		 */
		public Builder serviceConnectTimeout(int timeout) {
			serviceConnectTimeout = timeout;
			return this;
		}
		
		/**
		 * Configure the read timeout of a service request. This timeout configures {@link URLConnection#setReadTimeout(int)} to
		 * the passed value for each service request
		 *
		 * @param timeout Timeout in milliseconds
		 * @return This builder
		 */
		public Builder serviceReadTimeout(int timeout) {
			serviceReadTimeout = timeout;
			return this;
		}
		
		/**
		 * Creates a new {@link Authenticator} with this configuration. To run the authenticator call
		 * {@link Authenticator#run()}.
		 *
		 * @return Build Authenticator object
		 */
		public Authenticator build() {
			return new Authenticator(fileFunction, authenticate, retrieveXBoxProfile, customAzureApplication, new TimeoutValues(serviceConnectTimeout, serviceReadTimeout));
		}
		
	}
	
	private final AuthenticationFileFunctionWithCustomAzureApplication fileFunction;
	private final boolean authenticate;
	private final boolean retrieveXBoxProfile;
	private final Optional<AzureApplication> customAzureApplication;
	private final TimeoutValues timeoutValues;
	
	private boolean hasRun;
	
	private AuthenticationFile resultFile;
	private Optional<User> user;
	private Optional<XBoxProfile> xBoxProfile;
	
	/**
	 * Internal constructor to setup the authenticator state. To execute the authentication run the {@link #run()} method.
	 *
	 * @param fileFunction Function that returns {@link AuthenticationFile} for authentication
	 * @param authenticate Should authenticate to get a {@link User} as a result
	 * @param retrieveXBoxProfile Should retrieve an {@link XBoxProfile} object
	 * @param customAzureApplication Optional value to pass custom azure application values
	 * @param timeoutValues Timeout values for a service connection
	 */
	protected Authenticator(AuthenticationFileFunctionWithCustomAzureApplication fileFunction, boolean authenticate, boolean retrieveXBoxProfile, Optional<AzureApplication> customAzureApplication, TimeoutValues timeoutValues) {
		this.fileFunction = fileFunction;
		this.authenticate = authenticate;
		this.retrieveXBoxProfile = retrieveXBoxProfile;
		this.customAzureApplication = customAzureApplication;
		this.timeoutValues = timeoutValues;
		
		user = Optional.empty();
		xBoxProfile = Optional.empty();
	}
	
	/**
	 * This runs the selected authentication tasks. If {@link Builder#shouldAuthenticate()} is not enabled it will only
	 * resolve the {@link AuthenticationFile}. This call is blocking and can take some time if the services take a long
	 * respond time. The default timeout time is 15 seconds per service request. Change the timeout for the services with
	 * {@link Builder#serviceConnectTimeout(int)} and {@link Builder#serviceReadTimeout(int)}.
	 * <p>
	 * Please always save the {@link #getResultFile()} if it is not null, even when {@link AuthenticationException} is
	 * thrown. This is important because when a service after the initial authentication fails the oAuth service still
	 * requires the updated tokens.
	 * </p>
	 * <p>
	 * This method can only be called once per {@link Authenticator} object.
	 * </p>
	 *
	 * @throws AuthenticationException Throws exception if login was not successful
	 */
	public void run() throws AuthenticationException {
		run(LoginStateCallback.NOOP);
	}
	
	/**
	 * This runs the selected authentication tasks. If {@link Builder#shouldAuthenticate()} is not enabled it will only
	 * resolve the {@link AuthenticationFile}. This call is blocking and can take some time if the services take a long
	 * respond time. The default timeout time is 15 seconds per service request. Change the timeout for the services with
	 * {@link Builder#serviceConnectTimeout(int)} and {@link Builder#serviceReadTimeout(int)}.
	 * <p>
	 * Please always save the {@link #getResultFile()} if it is not null, even when {@link AuthenticationException} is
	 * thrown. This is important because when a service after the initial authentication fails the oAuth service still
	 * requires the updated tokens.
	 * </p>
	 * <p>
	 * This method can only be called once per {@link Authenticator} object.
	 * </p>
	 *
	 * @param callback Login state callback for information messages. Call is on thread and should not block too long
	 * @throws AuthenticationException Throws exception if login was not successful
	 */
	public void run(LoginStateCallback callback) throws AuthenticationException {
		if (hasRun) {
			throw new IllegalStateException("Cannot run the authentication multiple times");
		}
		hasRun = true;
		
		// Resolve the initial file
		callback.call(LoginState.INITAL_FILE);
		resultFile = fileFunction.get(customAzureApplication, timeoutValues);
		
		// Authentication
		if (authenticate) {
			// Microsoft authentication
			if (resultFile instanceof final MicrosoftAuthenticationFile microsoftFile) {
				final MicrosoftLoginResponse response = MicrosoftAuthentication.authenticate(customAzureApplication, retrieveXBoxProfile, microsoftFile, timeoutValues, callback);
				
				// Set new result file
				if (response.hasRefreshToken()) {
					resultFile = new MicrosoftAuthenticationFile(microsoftFile.getClientId(), response.getRefreshToken().get());
				}
				
				// Throw exceptions
				if (response.hasException()) {
					throw response.getException().get();
				}
				
				// Validate authentication response
				if (!response.hasUser()) {
					throw new AuthenticationException("After login there should be a user");
				}
				user = response.getUser();
				
				// Validate xbox profile response
				if (retrieveXBoxProfile && !response.hasXBoxProfile()) {
					throw new AuthenticationException("XBox profile was requested but is not there");
				}
				if (response.hasXBoxProfile()) {
					xBoxProfile = response.getXBoxProfile();
				}
			} else {
				throw new AuthenticationException(resultFile + " is not a microsoft authentication file");
			}
		}
	}
	
	/**
	 * Returns the updated {@link AuthenticationFile} if authentication was requested. Else returns the initial object. If
	 * the initial authentication fails this value might be <strong>null</strong>.
	 * <p>
	 * Can only be called after {@link #run()} was called.
	 * </p>
	 *
	 * @return {@link AuthenticationFile} that should be used for the next authentication or null
	 */
	public AuthenticationFile getResultFile() {
		if (!hasRun) {
			throw new IllegalStateException("This method can only be called after the authentication was run");
		}
		return resultFile;
	}
	
	/**
	 * Returns the user if authentication was requested and no errors occurred.
	 * <p>
	 * Can only be called after {@link #run()} was called.
	 * </p>
	 *
	 * @return Minecraft User. Cannot be empty if authentication was requested and no {@link AuthenticationException} was
	 *         raised
	 */
	public Optional<User> getUser() {
		if (!hasRun) {
			throw new IllegalStateException("This method can only be called after the authentication was run");
		}
		return user;
	}
	
	/**
	 * Returns the XBox profile if authentication and the xbox profile was requested and no errors occurred.
	 * <p>
	 * Can only be called after {@link #run()} was called.
	 * </p>
	 *
	 * @return XBoxProfile. Cannot be empty if authentication and the xbox profile was requested and no
	 *         {@link AuthenticationException} was raised
	 */
	public Optional<XBoxProfile> getXBoxProfile() {
		if (!hasRun) {
			throw new IllegalStateException("This method can only be called after the authentication was run");
		}
		return xBoxProfile;
	}
	
	/**
	 * Supplier that returns an {@link AuthenticationFile} and can trow an {@link AuthenticationException}
	 */
	@FunctionalInterface
	protected interface AuthenticationFileFunction {
		
		/**
		 * Returns the {@link AuthenticationFile}
		 *
		 * @param timeoutValues Timeout values for a service connection
		 * @return {@link AuthenticationFile}
		 * @throws AuthenticationException Throws if authentication file is created with an online service with authentication
		 */
		AuthenticationFile get(TimeoutValues timeoutValues) throws AuthenticationException;
	}
	
	/**
	 * Function that returns an {@link AuthenticationFile} and can trow an {@link AuthenticationException} and supplied the
	 * custom azure application parameters
	 */
	@FunctionalInterface
	protected interface AuthenticationFileFunctionWithCustomAzureApplication {
		
		/**
		 * Returns the {@link AuthenticationFile}
		 *
		 * @param customAzureApplication Custom azure application values that is needed to handle the oauth for microsoft
		 *        accounts
		 * @param timeoutValues Timeout values for a service connection
		 * @return {@link AuthenticationFile}
		 * @throws AuthenticationException Throws if authentication file is created with an online service with authentication
		 */
		AuthenticationFile get(Optional<AzureApplication> customAzureApplication, TimeoutValues timeoutValues) throws AuthenticationException;
	}
	
	/**
	 * Functions that takes a {@link LoginState} as parameter. Can be used to display the current login state
	 */
	@FunctionalInterface
	public interface LoginStateCallback {
		
		/**
		 * Default callback
		 */
		static LoginStateCallback NOOP = state -> {
		};
		
		/**
		 * Consumes a {@link LoginState}
		 * 
		 * @param state Current Login State
		 */
		void call(LoginState state);
	}
}
