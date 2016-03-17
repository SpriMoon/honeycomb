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

package io.fd.honeycomb.v3po.impl.trans.impl;

import com.google.common.annotations.Beta;
import io.fd.honeycomb.v3po.impl.trans.ChildVppReader;
import io.fd.honeycomb.v3po.impl.trans.VppReader;
import io.fd.honeycomb.v3po.impl.trans.impl.spi.RootVppReaderCustomizer;
import io.fd.honeycomb.v3po.impl.trans.util.VppReaderUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import org.opendaylight.yangtools.concepts.Builder;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.ChildOf;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

@Beta
@ThreadSafe
public final class CompositeRootVppReader<C extends DataObject, B extends Builder<C>> extends AbstractCompositeVppReader<C, B>
    implements VppReader<C> {

    private final RootVppReaderCustomizer<C, B> customizer;

    public CompositeRootVppReader(@Nonnull final Class<C> managedDataObjectType,
                                  @Nonnull final List<ChildVppReader<? extends ChildOf<C>>> childReaders,
                                  @Nonnull final List<ChildVppReader<? extends Augmentation<C>>> childAugReaders,
                                  @Nonnull final RootVppReaderCustomizer<C, B> customizer) {
        super(managedDataObjectType, childReaders, childAugReaders);
        this.customizer = customizer;
    }

    public CompositeRootVppReader(@Nonnull final Class<C> managedDataObjectType,
                                  @Nonnull final List<ChildVppReader<? extends ChildOf<C>>> childReaders,
                                  @Nonnull final RootVppReaderCustomizer<C, B> customizer) {
        this(managedDataObjectType, childReaders, VppReaderUtils.<C>emptyAugReaderList(), customizer);
    }

    public CompositeRootVppReader(@Nonnull final Class<C> managedDataObjectType,
                                  @Nonnull final RootVppReaderCustomizer<C, B> customizer) {
        this(managedDataObjectType, VppReaderUtils.<C>emptyChildReaderList(), VppReaderUtils.<C>emptyAugReaderList(),
            customizer);
    }

    @Override
    protected void readCurrentAttributes(@Nonnull final InstanceIdentifier<C> id, @Nonnull final B builder) {
        customizer.readCurrentAttributes(builder);
    }

    @Override
    protected B getBuilder(@Nonnull final InstanceIdentifier<? extends DataObject> id) {
        // TODO instantiate builder from customizer(as is) or reflection ?
        return customizer.getBuilder();
    }

}
