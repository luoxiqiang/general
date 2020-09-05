package com.alibaba.cloud.sentinel.feign;

import feign.Contract;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.Target;
import feign.hystrix.FallbackFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;
import per.qiang.common.feign.fallback.FeignFallbackFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class CustomSentinelFeign {

    public static CustomSentinelFeign.Builder builder() {
        return new CustomSentinelFeign.Builder();
    }

    public static final class Builder extends Feign.Builder implements ApplicationContextAware {
        private Contract contract = new Contract.Default();
        private ApplicationContext applicationContext;
        private FeignContext feignContext;

        @Override
        public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CustomSentinelFeign.Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        @Override
        public Feign build() {
            super.invocationHandlerFactory(new InvocationHandlerFactory() {
                @Override
                public InvocationHandler create(Target target, Map<Method, MethodHandler> dispatch) {
                    Object feignClientFactoryBean = CustomSentinelFeign.Builder.this.applicationContext.getBean("&" + target.type().getName());

                    Class fallback = (Class) getFieldValue(feignClientFactoryBean, "fallback");
                    Class fallbackFactory = (Class) getFieldValue(feignClientFactoryBean, "fallbackFactory");
                    String name = (String) getFieldValue(feignClientFactoryBean, "name");

                    Object fallbackInstance;
                    FallbackFactory fallbackFactoryInstance;
                    // check fallback and fallbackFactory properties
                    if (void.class != fallback) {
                        fallbackInstance = getFromContext(name, "fallback", fallback, target.type());
                        return new SentinelInvocationHandler(target, dispatch, new FallbackFactory.Default(fallbackInstance));
                    }
                    if (void.class != fallbackFactory) {
                        fallbackFactoryInstance = (FallbackFactory) getFromContext(name, "fallbackFactory", fallbackFactory, FallbackFactory.class);
                        return new SentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
                    }
                    // 默认的 fallbackFactory
                    FeignFallbackFactory funFallbackFactory = new FeignFallbackFactory(target);
                    return new SentinelInvocationHandler(target, dispatch, funFallbackFactory);
                }

                private Object getFromContext(String name, String type, Class fallbackType, Class targetType) {
                    Object fallbackInstance = feignContext.getInstance(name, fallbackType);
                    if (fallbackInstance == null) {
                        throw new IllegalStateException(String.format("No %s instance of type %s found for feign client %s", type, fallbackType, name));
                    }

                    if (!targetType.isAssignableFrom(fallbackType)) {
                        throw new IllegalStateException(String.format("Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s", type, fallbackType, targetType, name));
                    }
                    return fallbackInstance;
                }
            });
            super.contract(new SentinelContractHolder(contract));
            return super.build();
        }

        private Object getFieldValue(Object instance, String fieldName) {
            Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
            try {
                if (field != null) {
                    field.setAccessible(true);
                    return field.get(instance);
                }
            } catch (IllegalAccessException ignored) {
            }
            return null;
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
            feignContext = this.applicationContext.getBean(FeignContext.class);
        }
    }


}
