/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,  
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.validator.metadata;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Type;
import java.util.Set;

import org.hibernate.validator.engine.ConstraintTree;
import org.hibernate.validator.engine.ValidationContext;
import org.hibernate.validator.engine.ValueContext;
import org.hibernate.validator.metadata.site.ConstraintSite;

/**
 * Instances of this class abstract the constraint type  (class, method or field constraint) and give access to
 * meta data about the constraint. This allows a unified handling of constraints in the validator implementation.
 *
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 */
//TODO GM: parametrize with type of constraint site
public class MetaConstraint<T, A extends Annotation> {

	/**
	 * The constraint tree created from the constraint annotation.
	 */
	private final ConstraintTree<A> constraintTree;

	/**
	 * The constraint descriptor.
	 */
	private final ConstraintDescriptorImpl<A> constraintDescriptor;

	/**
	 * The site at which this constraint is defined.
	 */
	private final ConstraintSite site;

	/**
	 * @param constraintDescriptor The constraint descriptor for this constraint
	 * @param site meta data about constraint placement
	 */
	public MetaConstraint(ConstraintDescriptorImpl<A> constraintDescriptor, ConstraintSite site) {
		this.constraintTree = new ConstraintTree<A>( constraintDescriptor );
		this.constraintDescriptor = constraintDescriptor;
		this.site = site;
	}

	/**
	 * @return Returns the list of groups this constraint is part of. This might include the default group even when
	 *         it is not explicitly specified, but part of the redefined default group list of the hosting bean.
	 */
	public final Set<Class<?>> getGroupList() {
		return constraintDescriptor.getGroups();
	}

	public final ConstraintDescriptorImpl<A> getDescriptor() {
		return constraintDescriptor;
	}

	public final ElementType getElementType() {
		return constraintDescriptor.getElementType();
	}

	public <T, U, V> boolean validateConstraint(ValidationContext<T> executionContext, ValueContext<U, V> valueContext) {
		valueContext.setElementType( getElementType() );
		valueContext.setTypeOfAnnotatedElement( typeOfAnnotatedElement() );

		return constraintTree.validateConstraints( executionContext, valueContext );
	}

	public final ConstraintSite getSite() {
		return site;
	}

	/**
	 * @param o the object from which to retrieve the value.
	 *
	 * @return Returns the value for this constraint from the specified object. Depending on the type either the value itself
	 *         is returned of method or field access is used to access the value.
	 */
	public final Object getValue(Object o) {
		return site.getValue( o );
	}

	protected final Type typeOfAnnotatedElement() {
		return site.typeOfAnnotatedElement();
	}

	@Override
	public String toString() {
		return "MetaConstraint [constraintTree=" + constraintTree + ", site="
				+ site + "]";
	}
}
