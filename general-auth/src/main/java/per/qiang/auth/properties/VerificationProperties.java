package per.qiang.auth.properties;

import lombok.Getter;

@Getter
public class VerificationProperties {

        /**
         * 验证码有效时间，单位秒
         */
        private final Long time = 120L;
        /**
         * 验证码类型，可选值 png和 gif
         */
        private final String type = "png";
        /**
         * 图片宽度，px
         */
        private final Integer width = 130;
        /**
         * 图片高度，px
         */
        private final Integer height = 48;
        /**
         * 验证码位数
         */
        private final Integer length = 4;
        /**
         * 验证码值的类型
         * 1. 数字加字母
         * 2. 纯数字
         * 3. 纯字母
         */
        private final Integer charType = 2;
}
