package com.supets.lib.supetsrouter.di;

import java.lang.reflect.Field;

public class ModuleDI {

    public static void injectField(Object target) {
        Field[] fields = target.getClass().getFields();
        for (Field field : fields) {
            ModuleAop annotation = field.getAnnotation(ModuleAop.class);
            if (annotation == null) {
                continue;
            }
            try {
                String className = annotation.moduleImplName();
                Class<?> targetClass;
                if ("".equals(className)){
                    targetClass= annotation.moduleImplClass();
                }else{
                    targetClass= Class.forName(className);
                }
                Object object =targetClass .newInstance();
                field.setAccessible(true);
                field.set(target, object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
