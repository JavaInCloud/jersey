/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.client;

import java.lang.annotation.Annotation;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.process.internal.RequestScope;

/**
 * JAX-RS Response implementation that ensures that {@code readEntity(...)}
 * methods are invoked inside a request scope of the original request.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
class ScopedJaxrsResponse extends InboundJaxrsResponse {
    private final RequestScope scope;
    private final RequestScope.Instance scopeInstance;

    /**
     * Create new scoped client response.
     *
     * @param clientResponse original response.
     * @param scope request scope instance.
     */
    public ScopedJaxrsResponse(final ClientResponse clientResponse, final RequestScope scope) {
        super(clientResponse);
        this.scope = scope;
        this.scopeInstance = scope.referenceCurrent();
    }

    @Override
    public <T> T readEntity(final Class<T> entityType) throws ProcessingException, IllegalStateException {
        return scope.runInScope(scopeInstance, new Producer<T>() {
            @Override
            public T call() {
                return ScopedJaxrsResponse.super.readEntity(entityType);
            }
        });
    }

    @Override
    public <T> T readEntity(final GenericType<T> entityType) throws ProcessingException, IllegalStateException {
        return scope.runInScope(scopeInstance, new Producer<T>() {
            @Override
            public T call() {
                return ScopedJaxrsResponse.super.readEntity(entityType);
            }
        });
    }

    @Override
    public <T> T readEntity(final Class<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
        return scope.runInScope(scopeInstance, new Producer<T>() {
            @Override
            public T call() {
                return ScopedJaxrsResponse.super.readEntity(entityType, annotations);
            }
        });
    }

    @Override
    public <T> T readEntity(final GenericType<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
        return scope.runInScope(scopeInstance, new Producer<T>() {
            @Override
            public T call() {
                return ScopedJaxrsResponse.super.readEntity(entityType, annotations);
            }
        });
    }

    @Override
    public void close() throws ProcessingException {
        try {
            super.close();
        } finally {
            scopeInstance.release();
        }
    }
}
