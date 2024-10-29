package com.store.application.utils.filters;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ObjectSpecification<T> implements Specification<T> {
    private final List<FilterCriteria> filterCriteria;

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate finalPredicate = cb.conjunction();

        for (FilterCriteria criteria : filterCriteria) {
            String key = criteria.getKey();
            FilterOperator op = criteria.getOperator();
            List<String> values = criteria.getValues();

            Path<?> path;
            if (key.contains(".")) {
                String[] parts = key.split("\\.");
                Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                for (int i = 1; i < parts.length - 1; i++) {
                    join = join.join(parts[i], JoinType.LEFT);
                }
                path = join.get(parts[parts.length - 1]);
            } else {
                path = root.get(key);
            }

            switch (op) {
                case EQUALS:
                    finalPredicate = cb.and(finalPredicate, cb.equal(path, convertValue(path, values.getFirst())));
                    break;
                case NOT_EQUALS:
                    finalPredicate = cb.and(finalPredicate, cb.notEqual(path, convertValue(path, values.getFirst())));
                    break;
                case GREATER_THAN:
                    Predicate gt = buildGreaterThan(path, values.getFirst(), cb);
                    if (gt != null) {
                        finalPredicate = cb.and(finalPredicate, gt);
                    }
                    break;
                case GREATER_THAN_OR_EQUALS:
                    Predicate gte = buildGreaterThanOrEqual(path, values.getFirst(), cb);
                    if (gte != null) {
                        finalPredicate = cb.and(finalPredicate, gte);
                    }
                    break;
                case LESS_THAN:
                    Predicate lt = buildLessThan(path, values.getFirst(), cb);
                    if (lt != null) {
                        finalPredicate = cb.and(finalPredicate, lt);
                    }
                    break;
                case LESS_THAN_OR_EQUALS:
                    Predicate lte = buildLessThanOrEqual(path, values.getFirst(), cb);
                    if (lte != null) {
                        finalPredicate = cb.and(finalPredicate, lte);
                    }
                    break;
                case IN:
                    finalPredicate = cb.and(finalPredicate, buildInClause(path, values, cb));
                    break;
                case NOT_IN:
                    finalPredicate = cb.and(finalPredicate, buildNotInClause(path, values, cb));
                    break;
                case LIKE:
                    for (String value : values) {
                        if (StringUtils.hasText(value)) {
                            finalPredicate = cb.and(finalPredicate, cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));
                        }
                    }
                    break;
                case BETWEEN:
                    if (values.size() == 2) {
                        Predicate between = buildBetweenPredicate(path, values.get(0), values.get(1), cb);
                        if (between != null) {
                            finalPredicate = cb.and(finalPredicate, between);
                        }
                    } else {
                        throw new IllegalArgumentException("BETWEEN operation supports only 2 values");
                    }
                    break;
                case IS_NULL:
                    finalPredicate = cb.and(finalPredicate, cb.isNull(path));
                    break;
                case IS_NOT_NULL:
                    finalPredicate = cb.and(finalPredicate, cb.isNotNull(path));
                    break;
                default:
                    throw new UnsupportedOperationException("Operation not supported: " + op);
            }
        }

        return finalPredicate;
    }

    private Object convertValue(Path<?> path, String value) {
        if (value == null) {
            return null;
        }

        Class<?> type = path.getJavaType();
        if (type.equals(UUID.class)) {
            return UUID.fromString(value);
        } else if (type.equals(Boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (Number.class.isAssignableFrom(type)) {
            if (type.equals(Long.class)) {
                return Long.parseLong(value);
            } else if (type.equals(Integer.class)) {
                return Integer.parseInt(value);
            }
            // Add more number types as needed
        } else if (type.equals(OffsetDateTime.class)) {
            return parseDate(value);
        }
        // Add more type conversions as needed
        return value;
    }

    private Predicate buildGreaterThan(Path<?> path, String value, CriteriaBuilder cb) {
        Class<?> type = path.getJavaType();
        if (Number.class.isAssignableFrom(type)) {
            if (type.equals(Long.class)) {
                return cb.gt(path.as(Long.class), Long.parseLong(value));
            } else if (type.equals(Integer.class)) {
                return cb.gt(path.as(Integer.class), Integer.parseInt(value));
            }
            // Add more number types as needed
        } else if (type.equals(OffsetDateTime.class)) {
            return cb.greaterThan(path.as(OffsetDateTime.class), parseDate(value));
        }
        return null;
    }

    private Predicate buildGreaterThanOrEqual(Path<?> path, String value, CriteriaBuilder cb) {
        Class<?> type = path.getJavaType();
        if (Number.class.isAssignableFrom(type)) {
            if (type.equals(Long.class)) {
                return cb.ge(path.as(Long.class), Long.parseLong(value));
            } else if (type.equals(Integer.class)) {
                return cb.ge(path.as(Integer.class), Integer.parseInt(value));
            }
            // Add more number types as needed
        } else if (type.equals(OffsetDateTime.class)) {
            return cb.greaterThanOrEqualTo(path.as(OffsetDateTime.class), parseDate(value));
        }
        return null;
    }

    private Predicate buildLessThan(Path<?> path, String value, CriteriaBuilder cb) {
        Class<?> type = path.getJavaType();
        if (Number.class.isAssignableFrom(type)) {
            if (type.equals(Long.class)) {
                return cb.lt(path.as(Long.class), Long.parseLong(value));
            } else if (type.equals(Integer.class)) {
                return cb.lt(path.as(Integer.class), Integer.parseInt(value));
            }
            // Add more number types as needed
        } else if (type.equals(OffsetDateTime.class)) {
            return cb.lessThan(path.as(OffsetDateTime.class), parseDate(value));
        }
        return null;
    }

    private Predicate buildLessThanOrEqual(Path<?> path, String value, CriteriaBuilder cb) {
        Class<?> type = path.getJavaType();
        if (Number.class.isAssignableFrom(type)) {
            if (type.equals(Long.class)) {
                return cb.le(path.as(Long.class), Long.parseLong(value));
            } else if (type.equals(Integer.class)) {
                return cb.le(path.as(Integer.class), Integer.parseInt(value));
            }
            // Add more number types as needed
        } else if (type.equals(OffsetDateTime.class)) {
            return cb.lessThanOrEqualTo(path.as(OffsetDateTime.class), parseDate(value));
        }
        return null;
    }

    private Predicate buildInClause(Path<?> path, List<String> values, CriteriaBuilder cb) {
        CriteriaBuilder.In<Object> inClause = cb.in(path);
        for (String value : values) {
            if (value.equals("Not Assigned")) {
                inClause.value(convertValue(path, null));
            } else {
                inClause.value(convertValue(path, value));
            }
        }
        return inClause;
    }

    private Predicate buildNotInClause(Path<?> path, List<String> values, CriteriaBuilder cb) {
        CriteriaBuilder.In<Object> inClause = cb.in(path);
        for (String value : values) {
            inClause.value(convertValue(path, value));
        }
        return cb.not(inClause);
    }

    private Predicate buildBetweenPredicate(Path<?> path, String start, String end, CriteriaBuilder cb) {
        Class<?> type = path.getJavaType();
        if (type.equals(OffsetDateTime.class)) {
            OffsetDateTime startDate = parseDate(start);
            OffsetDateTime endDate = parseDate(end);
            return cb.between(path.as(OffsetDateTime.class), startDate, endDate);
        } else if (type.equals(Long.class)) {
            Long startInt = Long.parseLong(start);
            Long endInt = Long.parseLong(end);
            return cb.between(path.as(Long.class), startInt, endInt);
        }
        // Add more type handling for BETWEEN if needed
        return null;
    }

    private OffsetDateTime parseDate(String dateStr) {
        try {
            return OffsetDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid date format: " + dateStr + ". Expected yyyy-MM-dd'T'HH:mm:ss.SSSXXX", e);
        }
    }
}
