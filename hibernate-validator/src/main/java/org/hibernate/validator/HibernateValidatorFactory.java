package org.hibernate.validator;

/**
 * Provide Hibernate Validator extensions to ValidatorFactory
 *
 * @author Emmanuel Bernard
 */
public interface HibernateValidatorFactory {
	/**
	 * Refine Validator configuration via options from
	 * Bean Validation as well as specific ones from Hibernate Validator
	 */
	HibernateValidatorContext usingHibernateContext();
}
