package kikaha.cloud.aws.lambda;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.*;
import kikaha.core.modules.http.WebResource;
import lombok.Getter;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for AmazonHttpApplication.
 */
@RunWith(MockitoJUnitRunner.class)
public class AmazonHttpApplicationTest {

	final AmazonHttpApplication application = new AmazonHttpApplication();

	MockHandler handler1 = new GetUsers();
	MockHandler handler2 = new GetUser();
	MockHandler handler3 = new IncludeUser();

	@Mock AmazonHttpResponseHook responseHook;

	@Before
	public void loadHandlers(){
		application.loadHandlers( asList( handler1, handler2, handler3 ) );
		application.responseHooks = asList( responseHook );
	}

	@Test
	public void ensureCanDeployAllHandlers(){
		final Map<String, List<Entry>> entriesMatcher = application.getEntriesMatcher();
		assertEquals( 2, entriesMatcher.size() );
		assertEquals( 2, entriesMatcher.get( "GET" ).size() );
		assertEquals( 1, entriesMatcher.get( "POST" ).size() );
	}

	@Test
	public void ensureCanInvokeHandler1(){
		final AmazonLambdaRequest request = newRequest( "GET", "/users" );
		final AmazonLambdaResponse response = application.handleRequest( request, null );
		assertEquals( 204, response.statusCode );
		assertTrue( handler1.isInvoked() );
		assertFalse( handler2.isInvoked() );
		assertFalse( handler3.isInvoked() );
	}

	@Test
	public void ensureCanInvokeHandler2(){
		final AmazonLambdaRequest request = newRequest( "GET", "/users/1" );
		final AmazonLambdaResponse response = application.handleRequest( request, null );
		assertEquals( 204, response.statusCode );
		assertFalse( handler1.isInvoked() );
		assertTrue( handler2.isInvoked() );
		assertFalse( handler3.isInvoked() );
	}

	@Test
	public void ensureCanInvokeHandler3(){
		final AmazonLambdaRequest request = newRequest( "POST", "/users" );
		final AmazonLambdaResponse response = application.handleRequest( request, null );
		assertEquals( 204, response.statusCode );
		assertFalse( handler1.isInvoked() );
		assertFalse( handler2.isInvoked() );
		assertTrue( handler3.isInvoked() );
	}

	@Test
	public void ensureCanInvokeResponseHook() {
		final AmazonLambdaRequest request = newRequest("GET", "/users");
		final AmazonLambdaResponse response = application.handleRequest(request, null);
		verify( responseHook ).apply( eq(request), eq(response) );
	}

	static AmazonLambdaRequest newRequest( String method, String path ) {
		final AmazonLambdaRequest amazonLambdaRequest = new AmazonLambdaRequest();
		amazonLambdaRequest.path = path;
		amazonLambdaRequest.httpMethod = method;
		amazonLambdaRequest.pathParameters = new HashMap<>();
		return amazonLambdaRequest;
	}
}

@WebResource( path = "/users" )
class GetUsers extends MockHandler {}

@WebResource( path = "/users/{id}" )
class GetUser extends MockHandler {}

@WebResource( path = "/users", method = "POST" )
class IncludeUser extends MockHandler {}

@Getter
class MockHandler implements AmazonHttpHandler {

	boolean invoked = false;

	@Override
	public AmazonLambdaResponse handle( AmazonLambdaRequest request ) {
		invoked = true;
		return AmazonLambdaResponse.noContent();
	}
}