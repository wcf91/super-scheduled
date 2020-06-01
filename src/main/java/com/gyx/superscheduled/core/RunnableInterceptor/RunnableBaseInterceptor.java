package com.gyx.superscheduled.core.RunnableInterceptor;

import com.gyx.superscheduled.core.RunnableInterceptor.strengthen.BaseStrengthen;
import com.gyx.superscheduled.exception.SuperScheduledException;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class RunnableBaseInterceptor implements MethodInterceptor {


    private BaseStrengthen strengthen;


    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object result;
        if ("invoke".equals(method.getName())) {
            strengthen.before(obj, method, args);
            try {
                result = methodProxy.invokeSuper(obj, args);
            } catch (Exception e) {
                strengthen.exception(obj, method, args);
                throw new SuperScheduledException(strengthen.getClass() + "中强化执行时发生错误", e);
            } finally {
                strengthen.afterFinally(obj, method, args);
            }
            strengthen.after(obj, method, args);

        } else {
            result = methodProxy.invokeSuper(obj, args);
        }
        return result;
    }

    public RunnableBaseInterceptor(Object object) {
        if (BaseStrengthen.class.isAssignableFrom(object.getClass())) {
            this.strengthen = (BaseStrengthen) object;
        } else {
            throw new SuperScheduledException(object.getClass() + "对象不是BaseStrengthen类型");
        }
    }

    public RunnableBaseInterceptor() {
    }
}
