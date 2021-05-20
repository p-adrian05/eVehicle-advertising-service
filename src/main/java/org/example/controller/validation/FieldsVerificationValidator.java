package org.example.controller.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsVerificationValidator implements ConstraintValidator<FieldsVerification,Object> {
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsVerification constraintAnnotation) {
        field = constraintAnnotation.field();
        fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        Object fieldValue = new BeanWrapperImpl(o).getPropertyValue(field);
        Object fieldMatchValue = new BeanWrapperImpl(o).getPropertyValue(fieldMatch);

        if(fieldValue !=null){
            return fieldValue.equals(fieldMatchValue);
        }
        return fieldMatchValue == null;
    }
}
