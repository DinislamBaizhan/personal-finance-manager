package com.example.finance.data;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import org.passay.*;

import java.util.Arrays;

public class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        PasswordValidator passwordValidator = new PasswordValidator(
                Arrays.asList(
                        new LengthRule(6, 128),
                        new CharacterRule(EnglishCharacterData.UpperCase, 1),
                        new CharacterRule(EnglishCharacterData.LowerCase, 1),
                        new CharacterRule(EnglishCharacterData.Digit, 1),
                        new CharacterRule(EnglishCharacterData.Special, 1),
                        new WhitespaceRule()
                )
        );

        RuleResult result = passwordValidator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        constraintValidatorContext.buildConstraintViolationWithTemplate(passwordValidator.
                        getMessages(result).stream().findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("data not found")))
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        throw new ValidationException();
    }
}