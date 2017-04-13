package tech.washmore.util.duplicate;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Washmore on 2017/4/10.
 */
@Aspect
public class AvoidDuplicateTokenInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvoidDuplicateTokenInterceptor.class);

    /**
     * 切面配置
     */
    @Pointcut("@annotation(tech.washmore.util.duplicate.annotations.DuplicateToken)")
    public void pointCut() {
    }

    /**
     * 切面环绕增强
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("pointCut()")
    public Object interceptor(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //获取被拦截的方法
        Method method = signature.getMethod();

        Object obj = pjp.getTarget();
        if (!existControllerAnn(obj.getClass(), null)) {
            return pjp.proceed();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String tokenName = this.getTokenName(method, request);

        HttpSession session = request.getSession();
        if (session.getAttribute(tokenName) != null) {
            LOGGER.error("重复提交 [url:{}, token:{}]", request.getRequestURL(), tokenName);
//            try {
//                return method.getReturnType().newInstance();
//            } catch (Exception ex) {
//                return null;
//            }
            throw new IllegalAccessException("数据处理中，请勿重复提交!");
        }
        session.setAttribute(tokenName, UUID.randomUUID().toString());

        Object result = pjp.proceed();

        session = request.getSession(false);
        if (session != null) {
            request.getSession(false).removeAttribute(tokenName);
        }

        return result;
    }

    private String getTokenName(Method method, HttpServletRequest request) {
        String className = method.getDeclaringClass().getName();
        String classMethod = method.getName();
        String requestUrl = request.getRequestURL().toString();
        String params = JSON.toJSONString(request.getParameterMap());
        return className + "_" + classMethod + "_" + requestUrl + "_" + params;
    }

    /**
     * 判断是否存在Controller注解(包含RestController等)
     */
    private boolean existControllerAnn(Class<?> cls, Set<Annotation> exists) {
        if (cls == null) {
            return false;
        }
        Annotation[] anns = cls.getAnnotations();
        if (anns == null || anns.length == 0) {
            return false;
        }
        if (exists == null) {
            exists = new HashSet<Annotation>();
        }
        for (Annotation ann : anns) {
            if (exists.contains(ann)) {
                continue;
            }
            exists.add(ann);
            if (ann instanceof Controller) {
                return true;
            } else if (existControllerAnn(ann.annotationType(), exists)) {
                return true;
            }
        }
        return false;
    }

}
