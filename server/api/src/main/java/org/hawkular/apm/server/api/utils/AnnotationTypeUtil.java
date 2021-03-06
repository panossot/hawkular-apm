/*
 * Copyright 2015-2016 Red Hat, Inc. and/or its affiliates
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

package org.hawkular.apm.server.api.utils;

import org.hawkular.apm.api.model.PropertyType;
import org.hawkular.apm.server.api.model.zipkin.AnnotationType;

/**
 * @author Pavol Loffay
 */
public class AnnotationTypeUtil {

    private AnnotationTypeUtil() {}

    public static PropertyType toPropertyType(AnnotationType annotationType) {
        if (annotationType == null) {
            return PropertyType.Text;
        }

        PropertyType propertyType;

        switch (annotationType) {
            case STRING:
                propertyType = PropertyType.Text;
                break;
            case BOOL:
                propertyType = PropertyType.Boolean;
                break;
            case I16:
            case I32:
            case I64:
            case DOUBLE:
                propertyType = PropertyType.Number;
                break;
            default:
                propertyType = PropertyType.Text;
        }

        return propertyType;
    }
}
