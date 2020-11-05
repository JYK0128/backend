package com.example.demo.utils;

import com.example.demo.domain.member.Member;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnnotationUtils {
    public Map getFieldAnnotations(Class clz){
        Map<String, ArrayList<String>> annotations = new HashMap<>();
        for(Field field: clz.getDeclaredFields()){
            for(Annotation annotation:field.getDeclaredAnnotations()){
                String aType = annotation.annotationType().getTypeName();
                aType = aType.substring(aType.lastIndexOf(".")+1);

                if(!annotations.containsKey(aType)) annotations.put(aType, new ArrayList<>());
                annotations.get(aType).add(field.getName());
            }
        }

        return annotations;
    }

    public Map getClassAnnotations(Class clz){
        return null;
    }
}
