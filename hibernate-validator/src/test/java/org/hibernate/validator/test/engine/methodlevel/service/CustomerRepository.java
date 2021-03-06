// $Id: CustomerRepository.java 19033 Sep 19, 2010 9:50:04 AM gunnar.morling $
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
package org.hibernate.validator.test.engine.methodlevel.service;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.test.engine.methodlevel.model.Customer;

/**
 * @author Gunnar Morling
 */
public interface CustomerRepository extends RepositoryBase<Customer> {

	void findCustomerByName(@NotNull String name);

	void persistCustomer(@NotNull @Valid Customer customer);

	void findCustomerByAgeAndName(Integer age, @NotNull String name);

	void foo(@Min(1) Long id);

	void bar(Customer customer);
}
