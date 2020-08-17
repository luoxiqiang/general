package per.qiang.auth.service;


import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import per.qiang.auth.exception.VerificationCodeException;
import per.qiang.auth.properties.VerificationProperties;
import per.qiang.common.security.constant.AuthCons;
import per.qiang.common.redis.service.RedisService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class VerificationService {

    private final RedisService redisService;
    private final VerificationProperties properties = new VerificationProperties();

    public VerificationService(RedisService redisService) {
        this.redisService = redisService;
    }

    public void create(HttpServletRequest request, HttpServletResponse response) throws VerificationCodeException,IOException{
        String key = request.getParameter(AuthCons.VERIFY_KEY);
        if (StringUtils.isBlank(key)) {
            throw new VerificationCodeException("验证码key不能为空");
        }
        setHeader(response, properties.getType());
        Captcha captcha = createCaptcha(properties);
        redisService.set(AuthCons.VERIFY_KEY_PREFIX + key, StringUtils.lowerCase(captcha.text()), properties.getTime());
        captcha.out(response.getOutputStream());
    }

    public void check(String key, String value) throws VerificationCodeException {
        Object codeInRedis = redisService.get(AuthCons.VERIFY_KEY_PREFIX + key);
        if (StringUtils.isBlank(value)) {
            throw new VerificationCodeException("请输入验证码");
        }
        if (codeInRedis == null) {
            throw new VerificationCodeException("验证码已过期");
        }
        if (!StringUtils.equalsIgnoreCase(value, String.valueOf(codeInRedis))) {
            throw new VerificationCodeException("验证码不正确");
        }
    }

    private Captcha createCaptcha(VerificationProperties properties) {
        Captcha captcha = null;
        if (StringUtils.equalsIgnoreCase(properties.getType(), "gif")) {
            captcha = new GifCaptcha(properties.getWidth(), properties.getHeight(), properties.getLength());
        } else {
            captcha = new SpecCaptcha(properties.getWidth(), properties.getHeight(), properties.getLength());
        }
        captcha.setCharType(properties.getCharType());
        return captcha;
    }

    private void setHeader(HttpServletResponse response, String type) {
        if (StringUtils.equalsIgnoreCase(type, "gif")) {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        } else {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}
