package org.hibernate.validator;

import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.TraversableResolver;
import javax.validation.Validator;

/**
 * Represents the context that is used to create <code>Validator</code>
 * instances.
 * Provides both Bean Validation and Hibernate Validator specific context
 * configurations
 *
 * A client may use methods of the <code>ValidatorContext</code> returned by
 * <code>ValidatorFactory#usingContext</code> to customize
 * the context used to create <code>Validator</code> instances
 * (for instance establish different message interpolators or
 * traversable resolvers).
 *
 * @author Emmanuel Bernard
 */
public interface HibernateValidatorContext {
	/**
	 * At the first constraint violation detected, stop immediately and only return the
	 * first failure.
	 *
	 * The particular failure(s) returned is not deterministic. Note that more than one failure report can be returned
	 * by the provider.
	 *
	 * @return {@code this} following the chaining method pattern
	 */
	HibernateValidatorContext failFast();

	/**
	 * Defines the message interpolator implementation used by the
	 * <code>Validator</code>.
	 * If not set or if null is passed as a parameter,
	 * the message interpolator of the <code>ValidatorFactory</code>
	 * is used.
	 *
	 * @return self following the chaining method pattern
	 */
	HibernateValidatorContext messageInterpolator(MessageInterpolator messageInterpolator);

	/**
	 * Defines the traversable resolver implementation used by the
	 * <code>Validator</code>.
	 * If not set or if null is passed as a parameter,
	 * the traversable resolver of the <code>ValidatorFactory</code> is used.
	 *
	 * @return self following the chaining method pattern
	 */
	HibernateValidatorContext traversableResolver(TraversableResolver traversableResolver);

	/**
	 * Defines the constraint validator factory implementation used by the
	 * <code>Validator</code>.
	 * If not set or if null is passed as a parameter,
	 * the constraint validator factory of the <code>ValidatorFactory</code> is used.
	 *
	 * @return self following the chaining method pattern
	 */
	HibernateValidatorContext constraintValidatorFactory(ConstraintValidatorFactory factory);

	/**
	 * @return an initialized <code>Validator</code> instance respecting the defined state.
	 * Validator instances can be pooled and shared by the implementation.
	 */
	Validator getValidator();
}
