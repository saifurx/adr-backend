package com.kasa.adr.util;

import com.kasa.adr.dto.ArbitratorCreateRequest;
import com.kasa.adr.dto.ClaimantCreateRequest;
import com.kasa.adr.dto.DefaulterDetails;
import com.kasa.adr.dto.TemplateMapObject;
import com.kasa.adr.model.Address;
import com.kasa.adr.model.Case;
import com.kasa.adr.model.User;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonUtils {

    public static User getUserFromToken(String token) {
        return User.builder().build();
    }

    public static String generatePassword() {
        return "abcd1234";
    }

    public static String createBase64encodeToken(String caseId) {
        return "abc1234";
    }

    public static String replacePlaceholders(String template, Object obj) throws IllegalAccessException {
        if (template == null || obj == null) {
            throw new IllegalArgumentException("Template and object must not be null.");
        }

        String result = template;

        // Match placeholders like ${propertyName}
        String placeholderRegex = "\\{\\{(\\w+(\\.\\w+)*)}}";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(placeholderRegex);
        java.util.regex.Matcher matcher = pattern.matcher(template);

        while (matcher.find()) {
            String placeholder = matcher.group(0); // Full match: ${propertyName}
            String propertyPath = matcher.group(1); // Group inside: propertyName or nested.property

            try {
                Object value = getValueFromObject(obj, propertyPath);
                result = result.replace(placeholder, value != null ? value.toString() : "");
            } catch (Exception e) {
                // Ignore if the property does not exist or cannot be accessed
                result = result.replace(placeholder, "");
            }
        }

        return result;
    }

    private static Object getValueFromObject(Object obj, String propertyPath) throws Exception {
        String[] properties = propertyPath.split("\\.");
        Object currentObject = obj;

        for (String property : properties) {
            Field field = currentObject.getClass().getDeclaredField(property);
            field.setAccessible(true); // Access private fields
            currentObject = field.get(currentObject);

            if (currentObject == null) {
                return null;
            }
        }

        return currentObject;
    }

    public static List<String> extractFieldNames(Class<?> clazz) {
        List<String> fieldNames = new ArrayList<>();
        Set<Class<?>> visitedClasses = new HashSet<>();
        extractFieldNamesRecursive(clazz, fieldNames, "", visitedClasses);
        return fieldNames;
    }

    private static void extractFieldNamesRecursive(Class<?> clazz, List<String> fieldNames, String parentPrefix, Set<Class<?>> visitedClasses) {
        if (clazz == null || clazz.isPrimitive() || isWrapperType(clazz) || visitedClasses.contains(clazz)) {
            return;
        }

        visitedClasses.add(clazz); // Track visited classes to avoid circular references

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            String fullFieldName = parentPrefix.isEmpty() ? field.getName() : parentPrefix + "." + field.getName();
            fieldNames.add(fullFieldName);

            // Recursively process nested fields if not a primitive, wrapper, or collection type
            if (!isPrimitiveOrWrapperOrCollection(field.getType())) {
                extractFieldNamesRecursive(field.getType(), fieldNames, fullFieldName, visitedClasses);
            }
        }
    }

    private static boolean isPrimitiveOrWrapperOrCollection(Class<?> clazz) {
        return clazz.isPrimitive() ||
                isWrapperType(clazz) ||
                Iterable.class.isAssignableFrom(clazz) ||
                clazz.isArray();
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == String.class ||
                Number.class.isAssignableFrom(clazz) ||
                clazz == Boolean.class ||
                clazz == Character.class;
    }

    public static void main(String[] args) {
        try {
            TemplateMapObject placeHoderObj = TemplateMapObject.builder().build();
            placeHoderObj.setDefaulter(DefaulterDetails.builder().name("Saifur").build());
            String replacePlaceholders = replacePlaceholders("Hello ${defaulter.name}", placeHoderObj);
            System.out.println(replacePlaceholders);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static TemplateMapObject getTemplateMapObject(Case aCase) {
        DefaulterDetails defaulterDetails = getDefaulterDetails(aCase);
        ClaimantCreateRequest claimant = getClaimant(aCase);
        //   ArbitratorCreateRequest arbitrator = getArbitrator(aCase);
        return TemplateMapObject.builder().defaulter(defaulterDetails).claimant(claimant).build();
    }

    private static ArbitratorCreateRequest getArbitrator(Case aCase) {
        return ArbitratorCreateRequest.builder().name(aCase.getAssignedArbitrator().getName()).build();
    }

    private static ClaimantCreateRequest getClaimant(Case aCase) {
        return ClaimantCreateRequest.builder()
                .name(aCase.getClaimantAdmin().getName())
                .mobile(aCase.getClaimantAdmin().getMobile())
                .email(aCase.getClaimantAdmin().getEmail())

                .address(aCase.getClaimantAdmin().getInstitutionProfile().getAddress())
                .build();
    }

    private static DefaulterDetails getDefaulterDetails(Case aCase) {
        Address address = Address.builder().line1(aCase.getAddress().getLine1()).build();

        return DefaulterDetails.builder().name(aCase.getName())
                .address(address)
                .email(aCase.getEmail())
                .mobile(aCase.getMobile())
                .name(aCase.getName())
                .KASAAppointmentDate(aCase.getKASAAppointmentDate())
                .loanRecallNoticeNumber(aCase.getLoanRecallNoticeNumber())
                .loanRecallNoticeDate(aCase.getLoanRecallNoticeDate())
                .LRNAmount(aCase.getLRNAmount())
                .loan1(aCase.getLoans().get(0))
                .invocationRefNo(aCase.getInvocationRefNo())
                .build();
    }
}
