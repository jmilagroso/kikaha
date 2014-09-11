package kikaha.core.auth;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;

import java.util.Set;

class EmptyUndertowSessionManager implements SessionManager {

	@Override
	public String getDeploymentName() {
		return null;
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public Session createSession( HttpServerExchange serverExchange, SessionConfig sessionCookieConfig ) {
		return new EmptyUndertowSession();
	}

	@Override
	public Session getSession( HttpServerExchange serverExchange, SessionConfig sessionCookieConfig ) {
		return null;
	}

	@Override
	public Session getSession( String sessionId ) {
		return null;
	}

	@Override
	public void registerSessionListener( SessionListener listener ) {
	}

	@Override
	public void removeSessionListener( SessionListener listener ) {
	}

	@Override
	public void setDefaultSessionTimeout( int timeout ) {
	}

	@Override
	public Set<String> getTransientSessions() {
		return null;
	}

	@Override
	public Set<String> getActiveSessions() {
		return null;
	}

	@Override
	public Set<String> getAllSessions() {
		return null;
	}
}