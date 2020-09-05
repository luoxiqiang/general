package per.qiang.common.feign.fallback;

import feign.Target;
import feign.hystrix.FallbackFactory;
import org.springframework.cglib.proxy.Enhancer;

public class FeignFallbackFactory<T> implements FallbackFactory<T> {

    private final Target<T> target;

    public FeignFallbackFactory(Target<T> target) {
        this.target = target;
    }

    @Override
    public T create(Throwable cause) {
        final Class<T> targetType = target.type();
        final String targetName = target.name();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetType);
        enhancer.setUseCache(true);
        enhancer.setCallback(new FeignFallback<>(targetType, targetName, cause));
        return (T) enhancer.create();
    }

}
