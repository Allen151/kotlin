/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaCallableMemberDescriptor
import org.jetbrains.kotlin.resolve.OperatorKind
import org.jetbrains.kotlin.resolve.OperatorKindResolver

object JvmOperatorKindResolver : OperatorKindResolver {
    override fun getOperatorKind(descriptor: FunctionDescriptor): OperatorKind {
        if (!descriptor.isOperator) return OperatorKind.NON_OPERATOR

        return when (descriptor.kind) {
            CallableMemberDescriptor.Kind.FAKE_OVERRIDE,
            CallableMemberDescriptor.Kind.DELEGATION ->
                if (descriptor.overriddenDescriptors.any { getOperatorKind(it) == OperatorKind.OPERATOR })
                    OperatorKind.OPERATOR
                else
                    OperatorKind.INFERRED_OPERATOR
            CallableMemberDescriptor.Kind.DECLARATION,
            CallableMemberDescriptor.Kind.SYNTHESIZED->
                if (descriptor is JavaCallableMemberDescriptor)
                    OperatorKind.INFERRED_OPERATOR
                else
                    OperatorKind.OPERATOR
        }
    }
}