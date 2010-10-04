package org.hibernate.validator.engine;

import org.hibernate.validator.HibernateValidatorContext;
import org.hibernate.validator.metadata.BeanMetaDataCache;
import org.hibernate.validator.metadata.ConstraintHelper;

import javax.validation.*;

/**
 * @author Emmanuel Bernard
 */
public class HibernateValidatorContextImpl implements HibernateValidatorContext {
	private MessageInterpolator messageInterpolator;
	private TraversableResolver traversableResolver;
	private ConstraintValidatorFactory constraintValidatorFactory;
	private final MessageInterpolator factoryMessageInterpolator;
	private final TraversableResolver factoryTraversableResolver;
	private final ConstraintValidatorFactory factoryConstraintValidatorFactory;
	private final ConstraintHelper constraintHelper;
	private final BeanMetaDataCache beanMetaDataCache;
	private boolean failFast;

	public HibernateValidatorContextImpl(ConstraintValidatorFactory constraintValidatorFactory,
								MessageInterpolator factoryMessageInterpolator,
								TraversableResolver factoryTraversableResolver,
								ConstraintHelper constraintHelper,
								BeanMetaDataCache beanMetaDataCache,
								boolean failFast) {
		this.factoryConstraintValidatorFactory = constraintValidatorFactory;
		this.factoryMessageInterpolator = factoryMessageInterpolator;
		this.factoryTraversableResolver = factoryTraversableResolver;
		this.constraintHelper = constraintHelper;
		this.beanMetaDataCache = beanMetaDataCache;
		messageInterpolator( factoryMessageInterpolator );
		traversableResolver( factoryTraversableResolver );
		constraintValidatorFactory( factoryConstraintValidatorFactory );
		this.failFast = failFast;
	}

	public HibernateValidatorContext failFast() {
		failFast = true;
		return this;
	}

	public HibernateValidatorContext messageInterpolator(MessageInterpolator messageInterpolator) {
		if ( messageInterpolator == null ) {
			this.messageInterpolator = factoryMessageInterpolator;
		}
		else {
			this.messageInterpolator = messageInterpolator;
		}
		return this;
	}

	public HibernateValidatorContext traversableResolver(TraversableResolver traversableResolver) {
		if ( traversableResolver == null ) {
			this.traversableResolver = factoryTraversableResolver;
		}
		else {
			this.traversableResolver = traversableResolver;
		}
		return this;
	}

	public HibernateValidatorContext constraintValidatorFactory(ConstraintValidatorFactory factory) {
		if ( constraintValidatorFactory == null ) {
			this.constraintValidatorFactory = factoryConstraintValidatorFactory;
		}
		else {
			this.constraintValidatorFactory = factory;
		}
		return this;
	}

	public Validator getValidator() {
		return new ValidatorImpl(
				constraintValidatorFactory,
				messageInterpolator,
				traversableResolver,
				constraintHelper,
				beanMetaDataCache,
				failFast
		);
	}
}
