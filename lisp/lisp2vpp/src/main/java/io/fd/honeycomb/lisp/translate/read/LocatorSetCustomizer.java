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

package io.fd.honeycomb.lisp.translate.read;

import static io.fd.honeycomb.translate.v3po.util.cache.EntityDumpExecutor.NO_PARAMS;

import com.google.common.base.Optional;
import io.fd.honeycomb.lisp.translate.read.dump.check.LocatorSetsDumpCheck;
import io.fd.honeycomb.lisp.translate.read.dump.executor.LocatorSetsDumpExecutor;
import io.fd.honeycomb.translate.read.ReadContext;
import io.fd.honeycomb.translate.read.ReadFailedException;
import io.fd.honeycomb.translate.spi.read.ListReaderCustomizer;
import io.fd.honeycomb.translate.v3po.util.FutureJVppCustomizer;
import io.fd.honeycomb.translate.v3po.util.TranslateUtils;
import io.fd.honeycomb.translate.v3po.util.cache.DumpCacheManager;
import io.fd.honeycomb.translate.v3po.util.cache.exceptions.execution.DumpExecutionFailedException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev160520.locator.sets.grouping.LocatorSetsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev160520.locator.sets.grouping.locator.sets.LocatorSet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev160520.locator.sets.grouping.locator.sets.LocatorSetBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.lisp.rev160520.locator.sets.grouping.locator.sets.LocatorSetKey;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.openvpp.jvpp.core.dto.LispLocatorSetDetails;
import org.openvpp.jvpp.core.dto.LispLocatorSetDetailsReplyDump;
import org.openvpp.jvpp.core.future.FutureJVppCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorSetCustomizer extends FutureJVppCustomizer
        implements ListReaderCustomizer<LocatorSet, LocatorSetKey, LocatorSetBuilder> {

    //TODO - temporary as public because of hack in write customizer in *.write.LocatorSetCustomizer
    public static final String LOCATOR_SETS_CACHE_ID = LocatorSetCustomizer.class.getName();
    private static final Logger LOG = LoggerFactory.getLogger(LocatorSetCustomizer.class);

    private final DumpCacheManager<LispLocatorSetDetailsReplyDump, Void> dumpManager;

    public LocatorSetCustomizer(FutureJVppCore futureJvpp) {
        super(futureJvpp);
        this.dumpManager = new DumpCacheManager.DumpCacheManagerBuilder<LispLocatorSetDetailsReplyDump, Void>()
                .withExecutor(new LocatorSetsDumpExecutor(futureJvpp))
                .withNonEmptyPredicate(new LocatorSetsDumpCheck())
                .build();
    }

    @Override
    public LocatorSetBuilder getBuilder(InstanceIdentifier<LocatorSet> id) {
        return new LocatorSetBuilder();
    }

    @Override
    public void readCurrentAttributes(InstanceIdentifier<LocatorSet> id, LocatorSetBuilder builder, ReadContext ctx)
            throws ReadFailedException {
        LOG.debug("Reading attributes for Locator Set {}", id);

        Optional<LispLocatorSetDetailsReplyDump> dumpOptional;

        try {
            dumpOptional = dumpManager.getDump(LOCATOR_SETS_CACHE_ID, ctx.getModificationCache(), NO_PARAMS);
        } catch (DumpExecutionFailedException e) {
            throw new ReadFailedException(id, e);
        }
        if (!dumpOptional.isPresent()) {
            LOG.warn("No dump present for Locator Set {}", id);
            return;
        }

        String keyName = id.firstKeyOf(LocatorSet.class).getName();
        LispLocatorSetDetailsReplyDump dump = dumpOptional.get();

        java.util.Optional<LispLocatorSetDetails> details = dump.lispLocatorSetDetails.stream()
                .filter(n -> keyName.equals(TranslateUtils.toString(n.locatorSetName)))
                .findFirst();

        if (details.isPresent()) {
            final String name  = TranslateUtils.toString(details.get().locatorSetName);

            builder.setName(name);
            builder.setKey(new LocatorSetKey(name));
        } else {
            LOG.warn("Locator Set {} not found in dump", id);
        }
    }

    @Override
    public List<LocatorSetKey> getAllIds(InstanceIdentifier<LocatorSet> id, ReadContext context)
            throws ReadFailedException {
        LOG.debug("Dumping Locator Set {}", id);

        Optional<LispLocatorSetDetailsReplyDump> dumpOptional = null;
        try {
            dumpOptional = dumpManager.getDump(LOCATOR_SETS_CACHE_ID, context.getModificationCache(), NO_PARAMS);
        } catch (DumpExecutionFailedException e) {
            LOG.error("Error dumping Locator Set {}", e, id);
            return Collections.emptyList();
        }

        if (dumpOptional.isPresent()) {
            return dumpOptional.get().lispLocatorSetDetails.stream()
                    .map(set -> new LocatorSetKey(TranslateUtils.toString(set.locatorSetName)))
                    .collect(Collectors.toList());
        } else {
            LOG.warn("No data dumped for Locator Set {}", id);
            return Collections.emptyList();
        }
    }

    @Override
    public void merge(Builder<? extends DataObject> builder, List<LocatorSet> readData) {
        ((LocatorSetsBuilder) builder).setLocatorSet(readData);
    }
}
