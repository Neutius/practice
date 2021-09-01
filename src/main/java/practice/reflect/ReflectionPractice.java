package com.github.neutius.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectionPractice {

    private List<Object> objectList = new ArrayList<>();
    private Map<Object, List<Field>> objectFieldMap = new HashMap<>();
    private Map<Object, List<Method>> objectMethodMap = new HashMap<>();
    private Map<Object, List<Constructor<?>>> objectConstructorMap = new HashMap<>();

    public List<Object> getObjectList() {
        return objectList;
    }

    public static boolean objectsHaveTheSameValues(Object o1, Object o2) {
        boolean result = true;
        Class<?> o1Class = o1.getClass();
        Class<?> o2Class = o2.getClass();
        List<Method> o1Methods = sortedMethodList(o1Class);
        List<Method> o2Methods = sortedMethodList(o2Class);

        if (!o1Class.getCanonicalName().equalsIgnoreCase(o2Class.getCanonicalName())) {
            return false;
        }
        if (o1Methods.size() != o2Methods.size()) {
            return false;
        }

        for (int index = 0; index < o1Methods.size(); index++) {
            Method method1 = o1Methods.get(index);
            Method method2 = o2Methods.get(index);
            if (method1.getParameterCount() != method2.getParameterCount()) {
                result = false;
            }
            if (method1.getParameterCount() == 0) {
                try {
                    method1.setAccessible(true);
                    method2.setAccessible(true);
                    Object value1 = method1.invoke(o1);
                    Object value2 = method2.invoke(o2);
                    if (!value1.equals(value2)) {
                        result = false;
                    }
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }

        return result;
    }

    private static List<Method> sortedMethodList(Class<?> o1Class) {
        return Arrays.stream(o1Class.getDeclaredMethods())
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
    }

    public List<Field> getFieldsFor(Object object) {
        List<Field> result = objectFieldMap.get(object);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public List<Method> getMethodsFor(Object object) {
        List<Method> result = objectMethodMap.get(object);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public List<Constructor<?>> getConstructorsFor(Object object) {
        List<Constructor<?>> result = objectConstructorMap.get(object);
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public void inspect(Object object) {
        objectList.add(object);

        inspectFields(object);
        inspectMethods(object);
        inspectConstructors(object);
    }

    private void inspectFields(Object object) {
        List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
        objectFieldMap.put(object, fields);

        for (Field field : fields) {
            String name = field.getName();
            System.out.println("Field name: " + name + " - Field.toGenericString: " + field.toGenericString()
                    + " - Field.toString: " + field.toString());

            try {
                field.setAccessible(true);
                Object o = field.get(object);
                System.out.println("field.get(object): " + o);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void inspectMethods(Object object) {
        List<Method> methods = Arrays.asList(object.getClass().getDeclaredMethods());
        objectMethodMap.put(object, methods);

        for (Method method : methods) {
            Class<?> returnType = method.getReturnType();
            String name = method.getName();
            System.out.println("Method name: " + name + " - returnType: " + returnType + " - method.toGenericString: "
                    + method.toGenericString() + " - method.toString: " + method.toString());

            try {
                method.setAccessible(true);
                Object o = method.invoke(object);
                System.out.println("method.invoke(object): " + o);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void inspectConstructors(Object object) {
        List<Constructor<?>> constructors = Arrays.asList(object.getClass().getDeclaredConstructors());
        objectConstructorMap.put(object, constructors);

        for (Constructor<?> constructor : constructors) {
            String name = constructor.getName();
            System.out.println("Constructor name: " + name + " - constructor.toGenericString: " + constructor.toGenericString()
                    + " - constructor.toString: " + constructor.toString());

            try {
                constructor.setAccessible(true);
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                System.out.println("constructor.getParameterTypes(): " + Arrays.toString(parameterTypes));

            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
