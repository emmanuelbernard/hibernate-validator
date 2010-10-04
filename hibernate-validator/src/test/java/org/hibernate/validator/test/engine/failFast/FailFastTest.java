package org.hibernate.validator.test.engine.failFast;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.HibernateValidatorFactory;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.test.util.TestUtil;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.hibernate.validator.test.util.TestUtil.assertCorrectPropertyPaths;
import static org.hibernate.validator.test.util.TestUtil.assertNumberOfViolations;

/**
 * @author Emmanuel Bernard
 */
public class FailFastTest {
	@Test
	public void testFailFastSetOnValidatorFactory() {
		final HibernateValidatorConfiguration configuration = TestUtil.getConfiguration();
		final ValidatorFactory factory = configuration.failFast(true).buildValidatorFactory();

		final Validator validator = factory.getValidator();
		A testInstance = new A();

		Set<ConstraintViolation<A>> constraintViolations = validator.validate( testInstance );
		assertNumberOfViolations( constraintViolations, 1 );
	}

	@Test
	public void testFailFastSetOnValidator() {
		final HibernateValidatorConfiguration configuration = TestUtil.getConfiguration();
		final ValidatorFactory factory = configuration.buildValidatorFactory();
		Validator validator = factory.getValidator();

		A testInstance = new A();

		Set<ConstraintViolation<A>> constraintViolations = validator.validate( testInstance );
		assertNumberOfViolations( constraintViolations, 2 );

		validator =
				factory.unwrap(HibernateValidatorFactory.class)
					.usingHibernateContext()
						.failFast(true)
					.getValidator();


		constraintViolations = validator.validate( testInstance );
		assertNumberOfViolations( constraintViolations, 1 );
	}

	class A {
		@NotNull
		String b;

		@NotNull @Email
		String c;
	}
}
