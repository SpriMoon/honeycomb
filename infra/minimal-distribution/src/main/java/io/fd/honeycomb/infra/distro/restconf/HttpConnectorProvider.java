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

package io.fd.honeycomb.infra.distro.restconf;

import com.google.inject.Inject;
import io.fd.honeycomb.infra.distro.ProviderTrait;
import io.fd.honeycomb.infra.distro.cfgattrs.HoneycombConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;

final class HttpConnectorProvider extends ProviderTrait<ServerConnector> {

    @Inject
    private HoneycombConfiguration cfg;
    @Inject
    private Server server;

    @Override
    protected ServerConnector create() {
        ServerConnector httpConnector =
                new ServerConnector(server, cfg.acceptorsSize.get(), cfg.selectorsSize.get());
        httpConnector.setHost(cfg.restconfBindingAddress.get());
        httpConnector.setPort(cfg.restconfPort.get());
        server.addConnector(httpConnector);
        return httpConnector;
    }
}
