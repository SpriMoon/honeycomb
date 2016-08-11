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

package io.fd.honeycomb.infra.distro.netconf

import com.google.inject.Inject
import com.google.inject.name.Named
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import io.fd.honeycomb.infra.distro.ProviderTrait
import org.opendaylight.netconf.api.monitoring.NetconfMonitoringService
import org.opendaylight.netconf.impl.osgi.NetconfMonitoringServiceImpl
import org.opendaylight.netconf.mapping.api.NetconfOperationServiceFactory
/**
 * Mirror of org.opendaylight.controller.config.yang.config.netconf.northbound.impl.NetconfServerMonitoringModule
 */
@Slf4j
@ToString
class NetconfMonitoringServiceProvider extends ProviderTrait<NetconfMonitoringService> {

    @Inject
    @Named(NetconfModule.NETCONF_MAPPER_AGGREGATOR)
    NetconfOperationServiceFactory aggregator

    @Override
    def create() {
        new NetconfMonitoringServiceImpl(aggregator)
    }
}