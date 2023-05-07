package com.example.auth.data;

import com.example.auth.exception.DataNotFound;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;

public class PasswordConstraintsValidator implements ConstraintValidator<Password, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        PasswordValidator passwordValidator = new PasswordValidator(
                Arrays.asList(

                        new LengthRule(10, 128),

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

        constraintValidatorContext.buildConstraintViolationWithTemplate(passwordValidator.getMessages(result).stream().findFirst()
                        .orElseThrow(() -> new DataNotFound("data not found")))
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;

    }
}