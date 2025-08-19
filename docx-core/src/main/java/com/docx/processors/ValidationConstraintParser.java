package com.docx.processors;

import com.docx.models.ValidationConstraint;
import jakarta.validation.constraints.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@org.springframework.stereotype.Component
public class ValidationConstraintParser {

    private static final Map<Class<? extends Annotation>, String> CONSTRAINT_DESCRIPTIONS = new HashMap<>();
    
    static {
        CONSTRAINT_DESCRIPTIONS.put(NotNull.class, "Must not be null");
        CONSTRAINT_DESCRIPTIONS.put(NotBlank.class, "Must not be null or blank");
        CONSTRAINT_DESCRIPTIONS.put(NotEmpty.class, "Must not be null or empty");
        CONSTRAINT_DESCRIPTIONS.put(Size.class, "Size must be within specified bounds");
        CONSTRAINT_DESCRIPTIONS.put(Min.class, "Must be greater than or equal to minimum value");
        CONSTRAINT_DESCRIPTIONS.put(Max.class, "Must be less than or equal to maximum value");
        CONSTRAINT_DESCRIPTIONS.put(Pattern.class, "Must match the specified pattern");
        CONSTRAINT_DESCRIPTIONS.put(Email.class, "Must be a valid email address");
        CONSTRAINT_DESCRIPTIONS.put(Positive.class, "Must be a positive number");
        CONSTRAINT_DESCRIPTIONS.put(PositiveOrZero.class, "Must be a positive number or zero");
        CONSTRAINT_DESCRIPTIONS.put(Negative.class, "Must be a negative number");
        CONSTRAINT_DESCRIPTIONS.put(NegativeOrZero.class, "Must be a negative number or zero");
        CONSTRAINT_DESCRIPTIONS.put(DecimalMin.class, "Must be greater than or equal to specified decimal value");
        CONSTRAINT_DESCRIPTIONS.put(DecimalMax.class, "Must be less than or equal to specified decimal value");
        CONSTRAINT_DESCRIPTIONS.put(Digits.class, "Must have specified number of integer and fraction digits");
        CONSTRAINT_DESCRIPTIONS.put(Future.class, "Must be a future date");
        CONSTRAINT_DESCRIPTIONS.put(FutureOrPresent.class, "Must be a future date or present");
        CONSTRAINT_DESCRIPTIONS.put(Past.class, "Must be a past date");
        CONSTRAINT_DESCRIPTIONS.put(PastOrPresent.class, "Must be a past date or present");
    }

    public List<ValidationConstraint> parseConstraints(Parameter parameter) {
        List<ValidationConstraint> constraints = new ArrayList<>();
        
        for (Annotation annotation : parameter.getAnnotations()) {
            ValidationConstraint constraint = parseConstraint(annotation);
            if (constraint != null) {
                constraints.add(constraint);
            }
        }
        
        return constraints;
    }

    public List<ValidationConstraint> parseConstraints(Field field) {
        List<ValidationConstraint> constraints = new ArrayList<>();
        
        for (Annotation annotation : field.getAnnotations()) {
            ValidationConstraint constraint = parseConstraint(annotation);
            if (constraint != null) {
                constraints.add(constraint);
            }
        }
        
        return constraints;
    }

    public List<ValidationConstraint> parseConstraints(Class<?> clazz) {
        List<ValidationConstraint> constraints = new ArrayList<>();
        
        for (Field field : clazz.getDeclaredFields()) {
            constraints.addAll(parseConstraints(field));
        }
        
        return constraints;
    }

    private ValidationConstraint parseConstraint(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        
        if (!CONSTRAINT_DESCRIPTIONS.containsKey(annotationType)) {
            return null;
        }
        
        String name = annotationType.getSimpleName();
        String description = CONSTRAINT_DESCRIPTIONS.get(annotationType);
        
        ValidationConstraint constraint = new ValidationConstraint(name, description);
        
        // Extract attributes based on annotation type
        Map<String, Object> attributes = extractAttributes(annotation);
        constraint.setAttributes(attributes);
        
        // Customize description with attribute values
        String customizedDescription = customizeDescription(annotationType, description, attributes);
        constraint.setDescription(customizedDescription);
        
        return constraint;
    }

    private Map<String, Object> extractAttributes(Annotation annotation) {
        Map<String, Object> attributes = new HashMap<>();
        
        try {
            Method[] methods = annotation.annotationType().getDeclaredMethods();
            for (Method method : methods) {
                // Skip methods that are not attribute methods
                if (method.getParameterCount() > 0 || 
                    method.getName().equals("equals") || 
                    method.getName().equals("hashCode") || 
                    method.getName().equals("toString") ||
                    method.getName().equals("annotationType")) {
                    continue;
                }
                
                Object value = method.invoke(annotation);
                attributes.put(method.getName(), value);
            }
        } catch (Exception e) {
            // Log error in real implementation
        }
        
        return attributes;
    }

    private String customizeDescription(Class<? extends Annotation> annotationType, 
                                      String baseDescription, 
                                      Map<String, Object> attributes) {
        
        if (annotationType == Size.class) {
            Integer min = (Integer) attributes.get("min");
            Integer max = (Integer) attributes.get("max");
            return String.format("Size must be between %d and %d", min, max);
        }
        
        if (annotationType == Min.class) {
            Long value = (Long) attributes.get("value");
            return String.format("Must be greater than or equal to %d", value);
        }
        
        if (annotationType == Max.class) {
            Long value = (Long) attributes.get("value");
            return String.format("Must be less than or equal to %d", value);
        }
        
        if (annotationType == Pattern.class) {
            String regexp = (String) attributes.get("regexp");
            return String.format("Must match pattern: %s", regexp);
        }
        
        if (annotationType == DecimalMin.class) {
            String value = (String) attributes.get("value");
            Boolean inclusive = (Boolean) attributes.get("inclusive");
            String operator = inclusive ? "greater than or equal to" : "greater than";
            return String.format("Must be %s %s", operator, value);
        }
        
        if (annotationType == DecimalMax.class) {
            String value = (String) attributes.get("value");
            Boolean inclusive = (Boolean) attributes.get("inclusive");
            String operator = inclusive ? "less than or equal to" : "less than";
            return String.format("Must be %s %s", operator, value);
        }
        
        if (annotationType == Digits.class) {
            Integer integer = (Integer) attributes.get("integer");
            Integer fraction = (Integer) attributes.get("fraction");
            return String.format("Must have at most %d integer digits and %d fraction digits", 
                               integer, fraction);
        }
        
        return baseDescription;
    }

    public boolean isValidationAnnotation(Annotation annotation) {
        return CONSTRAINT_DESCRIPTIONS.containsKey(annotation.annotationType());
    }
}