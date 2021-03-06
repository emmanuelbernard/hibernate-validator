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
package org.hibernate.validator.engine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.Path;

/**
 * @author Hardy Ferentschik
 */
public final class PathImpl implements Path, Serializable {

	private static final long serialVersionUID = 7564511574909882392L;

	public static final String PROPERTY_PATH_SEPARATOR = ".";

	/**
	 * Regular expression used to split a string path into its elements.
	 *
	 * @see <a href="http://www.regexplanet.com/simple/index.jsp">Regular expression tester</a>
	 */
	private static final Pattern PATH_PATTERN = Pattern.compile( "(\\w+)(\\[(\\w*)\\])?(\\.(.*))*" );
	private static final int PROPERTY_NAME_GROUP = 1;
	private static final int INDEXED_GROUP = 2;
	private static final int INDEX_GROUP = 3;
	private static final int REMAINING_STRING_GROUP = 5;

	private final List<Node> nodeList;
	private NodeImpl currentLeafNode;
	private int hashCode;

	/**
	 * Returns a {@code Path} instance representing the path described by the given string. To create a root node the empty string should be passed.
	 *
	 * @param propertyPath the path as string representation.
	 *
	 * @return a {@code Path} instance representing the path described by the given string.
	 *
	 * @throws IllegalArgumentException in case {@code property == null} or {@code property} cannot be parsed.
	 */
	public static PathImpl createPathFromString(String propertyPath) {
		if ( propertyPath == null ) {
			throw new IllegalArgumentException( "null is not allowed as property path." );
		}

		if ( propertyPath.length() == 0 ) {
			return createNewPath( null );
		}

		return parseProperty( propertyPath );
	}

	public static PathImpl createNewPath(String name) {
		PathImpl path = new PathImpl();
		path.addNode( name );
		return path;
	}

	public static PathImpl createRootPath() {
		return createNewPath( null );
	}

	public static PathImpl createCopy(PathImpl path) {
		return new PathImpl( path );
	}

	public final boolean isRootPath() {
		return nodeList.size() == 1 && nodeList.get( 0 ).getName() == null;
	}

	public final PathImpl getPathWithoutLeafNode() {
		return new PathImpl( nodeList.subList( 0, nodeList.size() - 1 ) );
	}

	public final NodeImpl addNode(String nodeName) {
		NodeImpl parent = nodeList.size() == 0 ? null : (NodeImpl) nodeList.get( nodeList.size() - 1 );
		currentLeafNode = new NodeImpl( nodeName, parent, false, null, null );
		nodeList.add( currentLeafNode );
		hashCode = -1;
		return currentLeafNode;
	}

	public final NodeImpl makeLeafNodeIterable() {
		NodeImpl leafNode = getLeafNode();
		currentLeafNode = new NodeImpl( leafNode.getName(), leafNode.getParent(), true, null, null );
		nodeList.remove( leafNode );
		nodeList.add( currentLeafNode );
		hashCode = -1;
		return currentLeafNode;
	}

	public final NodeImpl setLeafNodeIndex(Integer index) {
		NodeImpl leafNode = getLeafNode();
		currentLeafNode = new NodeImpl( leafNode.getName(), leafNode.getParent(), true, index, null );
		nodeList.remove( leafNode );
		nodeList.add( currentLeafNode );
		hashCode = -1;
		return currentLeafNode;
	}

	public final NodeImpl setLeafNodeMapKey(Object key) {
		NodeImpl leafNode = getLeafNode();
		currentLeafNode = new NodeImpl( leafNode.getName(), leafNode.getParent(), true, null, key );
		nodeList.remove( leafNode );
		nodeList.add( currentLeafNode );
		hashCode = -1;
		return currentLeafNode;
	}

	public final NodeImpl getLeafNode() {
		return currentLeafNode;
	}

	public final Iterator<Path.Node> iterator() {
		if ( nodeList.size() == 0 ) {
			return Collections.<Path.Node>emptyList().iterator();
		}
		if ( nodeList.size() == 1 ) {
			return nodeList.iterator();
		}
		return nodeList.subList( 1, nodeList.size() ).iterator();
	}

	public final String asString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for ( int i = 1; i < nodeList.size(); i++ ) {
			NodeImpl nodeImpl = (NodeImpl) nodeList.get( i );
			if ( nodeImpl.getName() != null ) {
				if ( !first ) {
					builder.append( PROPERTY_PATH_SEPARATOR );
				}
				builder.append( nodeImpl.asString() );
			}

			first = false;
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return asString();
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		PathImpl path = (PathImpl) o;
		if ( nodeList != null && !nodeList.equals( path.nodeList ) ) {
			return false;
		}
		if ( nodeList == null && path.nodeList != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		if(hashCode == -1) {
			buildHashCode();
		}
		return hashCode;
	}

	public void buildHashCode() {
		hashCode = nodeList != null ? nodeList.hashCode() : 0;
	}

	/**
	 * Copy constructor.
	 *
	 * @param path the path to make a copy of.
	 */
	private PathImpl(PathImpl path) {
		this.nodeList = new ArrayList<Node>();
		NodeImpl parent = null;
		NodeImpl node = null;
		for ( int i = 0; i < path.nodeList.size(); i++ ) {
			node = (NodeImpl) path.nodeList.get( i );
			NodeImpl newNode = new NodeImpl( node, parent );
			this.nodeList.add( newNode );
			parent = newNode;
		}
		currentLeafNode = node;
	}

	private PathImpl() {
		nodeList = new ArrayList<Node>();
	}

	private PathImpl(List<Node> nodeList) {
		this.nodeList = new ArrayList<Node>();
		for ( Node node : nodeList ) {
			this.nodeList.add( node );
		}
	}

	private static PathImpl parseProperty(String property) {
		PathImpl path = createNewPath( null );
		String tmp = property;
		do {
			Matcher matcher = PATH_PATTERN.matcher( tmp );
			if ( matcher.matches() ) {

				// create the node
				String value = matcher.group( PROPERTY_NAME_GROUP );
				path.addNode( value );

				// is the node indexable
				if ( matcher.group( INDEXED_GROUP ) != null ) {
					path.makeLeafNodeIterable();
				}

				// take care of the index/key if one exists
				String indexOrKey = matcher.group( INDEX_GROUP );
				if ( indexOrKey != null && indexOrKey.length() > 0 ) {
					try {
						Integer i = Integer.parseInt( indexOrKey );
						path.setLeafNodeIndex( i );
					}
					catch ( NumberFormatException e ) {
						path.setLeafNodeMapKey( indexOrKey );
					}
				}

				// match the remaining string
				tmp = matcher.group( REMAINING_STRING_GROUP );
			}
			else {
				throw new IllegalArgumentException( "Unable to parse property path " + property );
			}
		} while ( tmp != null );

		if ( path.getLeafNode().isIterable() ) {
			path.addNode( null );
		}

		return path;
	}
}
