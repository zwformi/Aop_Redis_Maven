package com.zw.util;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/5/18.
 */
public class AopUtils {
    /**
     * 获取被拦截方法对象
     * MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     * 而缓存的注解在实现类的方法上
     * 所以应该使用反射获取当前对象的方法对象
     * @param pjp
     * @return
     * @throws NoSuchMethodException
     */
    public static Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        //--------------------------------------------------------------------------
        // 获取参数的类型
        //--------------------------------------------------------------------------
        Object[] args = pjp.getArgs();
        Class[] argTypes = new Class[pjp.getArgs().length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }

        String methodName = pjp.getSignature().getName();
        Class<?> targetClass = pjp.getTarget().getClass();
        Method[] methods = targetClass.getMethods();

        //--------------------------------------------------------------------------
        // 查找Class<?>里函数名称、参数数量、参数类型（相同或子类）都和拦截的method相同的Method
        //--------------------------------------------------------------------------
        Method method = null;
        for (int i = 0; i < methods.length; i++){
            if (methods[i].getName() == methodName){
                Class<?>[] parameterTypes = methods[i].getParameterTypes();
                boolean isSameMethod = true;

                // 如果相比较的两个method的参数长度不一样，则结束本次循环，与下一个method比较
                if (args.length != parameterTypes.length) {
                    continue;
                }

                //--------------------------------------------------------------------------
                // 比较两个method的每个参数，是不是同一类型或者传入对象的类型是形参的子类
                //--------------------------------------------------------------------------
                for (int j = 0;parameterTypes != null && j < parameterTypes.length ;j++) {
                    if (parameterTypes[j] != argTypes[j] && !parameterTypes[j].isAssignableFrom(argTypes[j])) {
                        isSameMethod = false;
                        break;
                    }
                }
                if (isSameMethod) {
                    method = methods[i];
                    break;
                }
            }
        }
        return method;
    }
}
