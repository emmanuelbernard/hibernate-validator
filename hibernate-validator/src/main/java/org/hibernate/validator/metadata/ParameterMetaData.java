// $Id: ParameterMetaData.java 19033 Sep 29, 2010 10:57:34 PM gunnar.morling $
/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Contains constraint-related meta-data for one method parameter.
 * 
 * @author Gunnar Morling
 */
public class ParameterMetaData implements Iterable<MetaConstraint<?, ? extends Annotation>> {

	public final static ParameterMetaData NULL = new ParameterMetaData(0, Collections.<MetaConstraint<?, ? extends Annotation>>emptyList(), false);
	
	private final int index;
	
	private final List<MetaConstraint<?, ? extends Annotation>> constraints;

	private final boolean isCascading;
	
	public ParameterMetaData(int index, List<MetaConstraint<?, ? extends Annotation>> constraints, boolean isCascading) {
		
		this.index = index;
		this.constraints = constraints;
		this.isCascading = isCascading;
	}

	public int getIndex() {
		return index;
	}

	public boolean isCascading() {
		return isCascading;
	}

	public Iterator<MetaConstraint<?, ? extends Annotation>> iterator() {
		return constraints.iterator();
	}
	
}
