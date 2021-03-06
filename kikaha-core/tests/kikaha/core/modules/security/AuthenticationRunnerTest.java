package kikaha.core.modules.security;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import java.util.*;
import javax.inject.Inject;
import io.undertow.server.*;
import kikaha.config.Config;
import kikaha.core.cdi.CDI;
import kikaha.core.test.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;

@RunWith( KikahaRunner.class )
public class AuthenticationRunnerTest {

	@Inject Config config;
	@Inject CDI provider;
	@Inject
    DefaultAuthenticationConfiguration defaultAuthenticationConfiguration;

	@Mock SecurityContext securityContext;
	@Mock HttpHandler rootHandler;

	AuthenticationRunner authHandler;
	HttpServerExchange exchange;
	AuthenticationRule matchedRule;

	@Test
	public void ensureThatCouldCallTheTargetHttpHandlerWhenIsAuthenticated() throws Exception {
		doReturn( true ).when( securityContext ).isAuthenticationRequired();
		when( securityContext.authenticate() ).thenReturn( true );
		when( securityContext.isAuthenticated() ).thenReturn( true );
		when( securityContext.getAuthenticatedAccount() ).thenReturn( new FixedUsernameAndRolesAccount( createExpectedRoles(), null ) );
		authHandler.run();
		verify( rootHandler ).handleRequest(exchange);
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandlerWhenDoesntMatchExpectedRoles() throws Exception {
		doReturn( true ).when( securityContext ).isAuthenticationRequired();
		doNothing().when( authHandler ).endCommunicationWithClient();
		doNothing().when( authHandler ).sendForbiddenError();
		when( securityContext.authenticate() ).thenReturn( true );
		when( securityContext.isAuthenticated() ).thenReturn( true );
		FixedUsernameAndRolesAccount accountWithUnexpectedRoles = new FixedUsernameAndRolesAccount( new HashSet<>(), null );
		when( securityContext.getAuthenticatedAccount() ).thenReturn( accountWithUnexpectedRoles );
		authHandler.run();
		verify( rootHandler, never() ).handleRequest(exchange);
		verify( authHandler ).handlePermissionDenied();
		verify( authHandler ).sendForbiddenError();
	}

	@Test
	public void ensureThatNotCallTheTargetHttpHandleWhenWasNotAuthenticatedRequests() throws Exception {
		doReturn( true ).when( securityContext ).isAuthenticationRequired();
		doNothing().when( authHandler ).endCommunicationWithClient();
		when( securityContext.authenticate() ).thenReturn( false );
		authHandler.run();
		verify( rootHandler, never() ).handleRequest(exchange);
		verify( authHandler ).endCommunicationWithClient();
	}

	@Test
	public void ensureThatIsAbleToHandleExceptionsInRunMethod() throws Exception {
		doReturn( true ).when( securityContext ).isAuthenticationRequired();
		doThrow( new IllegalStateException() ).when( securityContext ).authenticate();
		doNothing().when( authHandler ).handleException( any( Throwable.class ) );
		authHandler.run();
		verify( rootHandler, never() ).handleRequest(exchange);
		verify( authHandler ).handleException( isA( IllegalStateException.class ) );
	}

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks( this );
		initializeExchange();
		initializeAuthHandler();
	}

	void initializeExchange() {
		exchange = HttpServerExchangeStub.createHttpExchange();
	}

	void initializeAuthHandler() {
		AuthenticationRuleMatcher matcher = mockAuthRuleMatcher();
		matchedRule = spy( matcher.retrieveAuthenticationRuleForUrl( "/user" ) );
		authHandler = spy( new AuthenticationRunner( exchange, rootHandler,
			securityContext, createExpectedRoles(), "" ) );
	}

	AuthenticationRuleMatcher mockAuthRuleMatcher() {
		return spy( new AuthenticationRuleMatcher( provider, config.getConfig("server.auth"), defaultAuthenticationConfiguration) );
	}

	Set<String> createExpectedRoles() {
		Set<String> roles = new HashSet<>();
		roles.add( "Expected" );
		return roles;
	}
}
