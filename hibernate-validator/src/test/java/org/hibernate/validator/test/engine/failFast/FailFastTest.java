package org.hibernate.validator.test.engine.failFast;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.HibernateValidatorFactory;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.test.util.TestUtil;
import org.testng.annotations.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;
import java.util.Set;

import static org.hibernate.validator.test.util.TestUtil.assertCorrectPropertyPaths;
import static org.hibernate.validator.test.util.TestUtil.assertNumberOfViolations;

/**
 * @author Emmanuel Bernard
 */
public class FailFastTest {

	@Test
	public void testFailFastSetOnValidatorFactoryViaProperty() {
		final HibernateValidatorConfiguration configuration = TestUtil.getConfiguration();
		final ValidatorFactory factory = configuration.addProperty( HibernateValidatorConfiguration.FAIL_FAST, "true" )
				.buildValidatorFactory();

		final Validator validator = factory.getValidator();
		A testInstance = new A();

		Set<ConstraintViolation<A>> constraintViolations = validator.validate( testInstance );
		assertNumberOfViolations( constraintViolations, 1 );
	}

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

	@Test
	public void testFailSafePerf() {
		final Validator regularValidator = TestUtil.getConfiguration().buildValidatorFactory().getValidator();
		final Validator failFastValidator = TestUtil.getConfiguration().buildValidatorFactory().unwrap( HibernateValidatorFactory.class ).usingHibernateContext().failFast( true ).getValidator();

		final int looptime = 50000;
		for (int i = 0 ; i < looptime; i++) {
			validateBatch(regularValidator);
		}

		for (int i = 0 ; i < looptime; i++) {
			validateBatch(failFastValidator);
		}

		long start = System.nanoTime();
		for (int i = 0 ; i < looptime; i++) {
			validateBatch(regularValidator);
		}
		long timeOfRegular = System.nanoTime() - start;

		start = System.nanoTime();
		for (int i = 0 ; i < looptime; i++) {
			validateBatch(failFastValidator);
		}
		long timeOfFailFast = System.nanoTime() - start;

		System.err.println("Regular = " + timeOfRegular + "\n FailFast:" + timeOfFailFast);
	}

	private void validateBatch(Validator validator) {
		validator.validate( buildA() );
	}

	static int i = 0;
	A buildA() {
		A a = new A();
		a.b = "bbb" + i++;
		a.file = "test" + i++ + ".txt";
		a.bs.add( buildB() );
		a.bs.add( buildB() );
		a.bs.add( buildB() );
		a.bs.add( buildB() );
		return a;
	}

	B buildB() {
		B b = new B();
		b.size = 45 + i++;
		return b;
	}

	class A {
		@NotNull
		String b;

		@NotNull @Email
		String c;

		@Pattern(regexp = ".*\\.txt$")
		String file;

		@Valid
		Set<B> bs = new HashSet<B>();
	}

	class B {
		@Min(value = 10) @Max(value=30) @NotNull
		Integer size;
	}
}
