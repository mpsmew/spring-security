/* Copyright 2004, 2005 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.acegisecurity.adapters;

import junit.framework.TestCase;

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.MockHttpServletRequest;
import net.sf.acegisecurity.MockHttpServletResponse;
import net.sf.acegisecurity.context.ContextHolder;
import net.sf.acegisecurity.context.security.SecureContextImpl;
import net.sf.acegisecurity.context.security.SecureContextUtils;
import net.sf.acegisecurity.util.MockFilterChain;


/**
 * Tests {@link HttpRequestIntegrationFilter}.
 *
 * @author Ben Alex
 * @version $Id$
 */
public class HttpRequestIntegrationFilterTests extends TestCase {
    //~ Constructors ===========================================================

    public HttpRequestIntegrationFilterTests() {
        super();
    }

    public HttpRequestIntegrationFilterTests(String arg0) {
        super(arg0);
    }

    //~ Methods ================================================================

    public static void main(String[] args) {
        junit.textui.TestRunner.run(HttpRequestIntegrationFilterTests.class);
    }

    public void testCorrectOperation() throws Exception {
        HttpRequestIntegrationFilter filter = new HttpRequestIntegrationFilter();
        PrincipalAcegiUserToken principal = new PrincipalAcegiUserToken("key",
                "someone", "password",
                new GrantedAuthority[] {new GrantedAuthorityImpl("SOME_ROLE")});

        MockHttpServletRequest request = new MockHttpServletRequest(principal,
                null);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain(true);

        filter.doFilter(request, response, chain);

        if (!(SecureContextUtils.getSecureContext().getAuthentication() instanceof PrincipalAcegiUserToken)) {
            fail("Should have returned PrincipalAcegiUserToken");
        }

        PrincipalAcegiUserToken castResult = (PrincipalAcegiUserToken) SecureContextUtils.getSecureContext()
                                                                                         .getAuthentication();
        assertEquals(principal, castResult);
    }

    public void testHandlesIfHttpRequestIsNullForSomeReason()
        throws Exception {
        HttpRequestIntegrationFilter filter = new HttpRequestIntegrationFilter();

        try {
            filter.doFilter(null, null, null);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertTrue(true);
        }
    }

    public void testHandlesIfThereIsNoPrincipal() throws Exception {
        HttpRequestIntegrationFilter filter = new HttpRequestIntegrationFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("foo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain(true);

        assertNull(SecureContextUtils.getSecureContext().getAuthentication());
        filter.doFilter(request, response, chain);
        assertNull(SecureContextUtils.getSecureContext().getAuthentication());
    }

    protected void setUp() throws Exception {
        super.setUp();
        ContextHolder.setContext(new SecureContextImpl());
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        ContextHolder.setContext(null);
    }
}
