/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.btm.client.manager.config;

import org.hawkular.btm.api.model.admin.InstrumentConsumer;
import org.hawkular.btm.api.model.admin.InstrumentInvocation;
import org.hawkular.btm.api.model.admin.InstrumentType;

/**
 * This class transforms the InstrumentConsumer type.
 *
 * @author gbrown
 */
public class InstrumentConsumerTransformer extends InstrumentInvocationTransformer {

    /* (non-Javadoc)
     * @see org.hawkular.btm.client.byteman.InstrumentTypeTransformer#getInstrumentType()
     */
    @Override
    public Class<? extends InstrumentType> getInstrumentType() {
        return InstrumentConsumer.class;
    }

    /* (non-Javadoc)
     * @see org.hawkular.btm.client.manager.config.InstrumentInvocationTransformer#getEntity()
     */
    @Override
    protected String getEntity() {
        return "consumer";
    }

    /* (non-Javadoc)
     * @see org.hawkular.btm.client.manager.config.InstrumentInvocationTransformer#getParameters()
     */
    @Override
    protected String[] getParameters(InstrumentInvocation invocation) {
        String[] ret=new String[2];
        ret[0] = ((InstrumentConsumer)invocation).getEndpointTypeExpression();
        ret[1] = ((InstrumentConsumer)invocation).getUriExpression();
        return (ret);
    }

}