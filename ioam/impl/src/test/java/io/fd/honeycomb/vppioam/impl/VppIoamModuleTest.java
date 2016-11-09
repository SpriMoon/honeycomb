/*
 * Copyright (c) 2016 Cisco and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.fd.honeycomb.vppioam.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.initMocks;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import io.fd.honeycomb.translate.impl.write.registry.FlatWriterRegistryBuilder;
import io.fd.honeycomb.translate.write.WriterFactory;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;

import io.fd.vpp.jvpp.ioamtrace.future.FutureJVppIoamtraceFacade;
import io.fd.vpp.jvpp.JVppRegistry;


public class VppIoamModuleTest {

    @Named("honeycomb-context")
    @Bind
    @Mock
    private DataBroker honeycombContext;

    @Named("honeycomb-initializer")
    @Bind
    @Mock
    private DataBroker honeycombInitializer;

    @Bind
    @Mock
    private JVppRegistry registry;

    @Inject
    private Set<WriterFactory> writerFactories = new HashSet<>();

    @Before
    public void setUp() throws Exception {

        initMocks(this);

        Guice.createInjector(new VppIoamModule(MockJVppIoamProvider.class), BoundFieldModule.of(this)).injectMembers(this);
    }

    @Test
    public void testWriterFactories() throws Exception {
        assertThat(writerFactories, is(not(empty())));

        final FlatWriterRegistryBuilder registryBuilder = new FlatWriterRegistryBuilder();
        writerFactories.forEach(factory -> factory.init(registryBuilder));
        assertNotNull(registryBuilder.build());
    }

    private static final class MockJVppIoamProvider implements Provider<FutureJVppIoamtraceFacade> {

        @Override
        public FutureJVppIoamtraceFacade get() {
            return mock(FutureJVppIoamtraceFacade.class);
        }
    }
}
