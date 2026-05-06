package cn.qihangerp.open.filter;

import cn.qihangerp.model.entity.SysOpenAuth;
import cn.qihangerp.service.SysOpenAuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class AuthService {
    private final SysOpenAuthService sysOpenAuthService;

//    private static final Map<String, String> APP_CREDENTIALS = new HashMap<>();
//
//    static {
//        APP_CREDENTIALS.put("aaa", "bbb");
//    }

    public boolean validateCredentials(String appKey, String appSecret) {
//        String secret = APP_CREDENTIALS.get(appKey);
        SysOpenAuth sysOpenAuth = sysOpenAuthService.queryByAppKey(appKey);
        if(sysOpenAuth==null) {
            log.error("无效的AppKey");
            return false;
        }else {
            return sysOpenAuth.getAppSecret() != null && sysOpenAuth.getAppSecret().equals(appSecret);
        }
    }

    public String generateSignature(String appKey, String timestamp,String signString) {
        String dataToSign = appKey + signString + timestamp;
        return DigestUtils.sha256Hex(dataToSign); // 使用 SHA-256 生成签名
    }
}
