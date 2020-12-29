package com.dxhy.order.service.impl;

import cn.hutool.core.util.StrUtil;
import com.dxhy.order.api.ICommonDisposeService;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.constant.Constant;
import com.dxhy.order.dao.AuthenticationInfoMapper;
import com.dxhy.order.model.AuthenticationInfo;
import com.dxhy.order.protocol.CommonRequestParam;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 接口工具类
 *
 * @author ZSC-DXHY
 */
@Service
@Slf4j
public class CommonDisposeServiceImpl implements ICommonDisposeService {
    
    private static final String LOGGER_MSG = "(加解密接口)";
    
    @Resource
    private AuthenticationInfoMapper authenticationInfoMapper;
    @Resource
    private RedisService redisService;
    
    /**
     * 参数解密 解压
     */
    @Override
    public String commonDecrypt(CommonRequestParam param) {
        return commonDecrypt(param.getZipCode(), param.getEncryptCode(), param.getContent(), param.getSecretId());
    }
    
    /**
     * 参数压缩 加密
     */
    @Override
    public String commonEncrypt(CommonRequestParam param) {
        return commonEncrypt(param.getZipCode(), param.getEncryptCode(), param.getContent(), param.getSecretId());
    }
    
    /**
     * 根据secretId读取对应的secretKey
     * 或者根据税号读取对应的secretId
     *
     * @param secretId
     * @return
     */
    @Override
    public String getAuthMap(String secretId) {
        String string = redisService.get(secretId);
        //有值直接返回
        if (StringUtils.isNotBlank(string)) {
            return string;
        }
        //没值查询数据库，存值并返回
        String value = StringUtils.EMPTY;
        List<AuthenticationInfo> authenticateAction = authenticationInfoMapper.selectAuthticationAll(ConfigureConstant.STRING_0);
        log.info("{},初始化,遍历数据库数据:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(authenticateAction));
        for (AuthenticationInfo authenticationInfo : authenticateAction) {
            if (secretId.equals(authenticationInfo.getSecretId())) {
                redisService.set(secretId, authenticationInfo.getSecretKey(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                value = authenticationInfo.getSecretKey();
            }
            if (secretId.equals(authenticationInfo.getNsrsbh())) {
                redisService.set(secretId, authenticationInfo.getSecretId(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                value = authenticationInfo.getSecretId();
            }
        }
    
        return value;
    
    }
    
    
    /**
     * 参数压缩 加密
     */
    public String commonEncrypt(String zipCode, String encryptCode, String content, String secretId) {
        String json = content;
        byte[] de = null;
        // 加密
        if (ConfigurerInfo.ENCRYPTCODE_1.equals(encryptCode)) {
            try {
                // 从内存中获取对应的secreKey
                String secretKey = getAuthMap(secretId);
                // 截取秘钥
                String password = secretKey.substring(0, ConfigurerInfo.PASSWORD_SIZE);
                // 加密
                de = TripleDesUtil.encryptMode(password, json.getBytes());
            } catch (Exception e) {
                log.error("{}3DES加密出现异常:{}", LOGGER_MSG, e);
            }
        }
        if (ConfigurerInfo.ZIPCODE_1.equals(zipCode)) {
            // 压缩
            try {
                if (de != null) {
                    de = GZipUtils.compress(de);
                } else {
                    de = GZipUtils.compress(json.getBytes());
                }
            } catch (Exception e) {
                log.error("{}GZIP压缩出现异常:{}", LOGGER_MSG, e);
            }
        }
        try {
            if (de != null) {
                json = Base64Encoding.encodeToString(de);
            } else {
                json = Base64Encoding.encodeToString(content.getBytes());
            }
        } catch (Exception e) {
            log.error("{}base64压缩出现异常:{}", LOGGER_MSG, e);
        }
        return json;
    }
    
    /**
     * 参数解密 解密
     *
     * @return
     */
    public String commonDecrypt(String zipCode, String encryptCode, String content, String secretId) {
        
        String json = content;
        byte[] de = null;
        try {
            json = Base64Encoding.decodeToString(content);
            de = Base64Encoding.decode(content);
        } catch (Exception e) {
            log.error("{}base64解密出现异常:{}", LOGGER_MSG, e);
        }
        if (ConfigurerInfo.ZIPCODE_1.equals(zipCode)) {
            // 解压缩
            try {
                de = GZipUtils.decompress(de);
                json = new String(de, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("{}解压缩出现异常:{}", LOGGER_MSG, e);
            }
        }
        // 加密
        if (ConfigurerInfo.ENCRYPTCODE_1.equals(encryptCode)) {
            // 从内存中获取对应的secreKey
            String secretKey = getAuthMap(secretId);
            // 截取秘钥
            String password = secretKey.substring(0, ConfigurerInfo.PASSWORD_SIZE);
            try {
                // 解密
                json = new String(TripleDesUtil.decryptMode(password, de), StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.error("{}3DES解密出现异常:{}", LOGGER_MSG, e);
            }
        }
        return json;
    }
    
    /**
     * 根据参数,获取http请求的URL
     *
     * @param nsrsbh
     * @param zipCode
     * @param encryptCode
     * @param content
     * @param url
     * @return
     */
    @Override
    public Map<String, String> getRequestParameter(String nsrsbh, String zipCode, String encryptCode, String content, String url, String interfaceType) {
    
        /**
         * 根据接口类型判断使用哪个id和key
         */
        String secretId = "";
        String secretKey = "";
        if (StrUtil.isNotBlank(interfaceType) && ConfigureConstant.STRING_3.equals(interfaceType)) {
            secretId = OpenApiConfig.pushMyinvoiceSecretId;
            secretKey = OpenApiConfig.pushMyinvoiceSecretKey;
        } else {
            /**
             * 根据税号读取对应的secretId
             */
            secretId = getAuthMap(nsrsbh);
            log.debug("{}根据税号:{},获取到的secretId为:{}", LOGGER_MSG, nsrsbh, secretId);
            secretKey = getAuthMap(secretId);
            log.debug("{}根据secretId:{},获取到的secretKey为:{}", LOGGER_MSG, secretId, secretKey);
        }
    
        //String signUrl = getAuthUrl(url);
        //log.debug("{}根据数据库请求URL:{},获取到的签名URL为:{}", LOGGER_MSG, url, signUrl);
    
        CommonRequestParam commonRequestParam = new CommonRequestParam();
        commonRequestParam.setZipCode(zipCode);
        commonRequestParam.setEncryptCode(encryptCode);
        commonRequestParam.setContent(content);
        commonRequestParam.setSecretId(secretId);
        String json = commonEncrypt(commonRequestParam);
        
        // 封装公共参数
        return getSignature(zipCode, encryptCode, json, secretId, secretKey, null);
        
    }
    
    
    /**
     * 拼接url
     *
     * @param reqUrl
     * @return
     */
    public static String getAuthUrl(String reqUrl) {
        if (reqUrl.split(ConfigureConstant.STRING_COLON).length > ConfigureConstant.INT_2) {
            String one = reqUrl.split(ConfigureConstant.STRING_COLON)[1];
            String two = reqUrl.split(ConfigureConstant.STRING_COLON)[2];
            //支持带端口数据的返回
            if (two.indexOf("/") > 0) {
                reqUrl = one.replaceAll("//", "") + ConfigureConstant.STRING_COLON + two;
            } else {
                reqUrl = one.replaceAll("//", "") + two.substring(two.indexOf("/"));
            }
        } else if (reqUrl.split(ConfigureConstant.STRING_COLON).length == ConfigureConstant.INT_2) {
            String one = reqUrl.split(ConfigureConstant.STRING_COLON)[1];
            reqUrl = one.contains("//") ? one.replaceAll("//", "") : reqUrl;
        }
        return "POST" + reqUrl + "?";
    }
    
    
    /**
     * 封装并对公共参数排序
     *
     * @param gzip
     * @param encode
     * @param content
     * @param secretId
     * @param secretKey
     * @param signUrl
     * @return
     */
    private static Map<String, String> getSignature(String gzip, String encode, String content, String secretId, String secretKey, String signUrl) {
        Calendar c = Calendar.getInstance();
        long timeInMillis = c.getTimeInMillis();
        int nonce = HmacSHA1Util.getRandNum(1, 999999);
        HashMap<String, String> reqMap = new HashMap<>(10);
        try {
            TreeMap<String, String> sortMap = new TreeMap<>();
            sortMap.put(ConfigurerInfo.NONCE, String.valueOf(nonce));
            sortMap.put(ConfigurerInfo.SECRETID, secretId);
            sortMap.put(ConfigurerInfo.TIMESTAMP, String.valueOf(timeInMillis));
            sortMap.put(ConfigurerInfo.CONTENT, content);
            sortMap.put(ConfigurerInfo.ENCRYPTCODE, encode);
            sortMap.put(ConfigurerInfo.ZIPCODE, gzip);
            String localSign = HmacSHA1Util.genSign(signUrl, sortMap, secretKey);
            log.debug("{}生成的签名值为:{}", LOGGER_MSG, localSign);
            sortMap.put(ConfigurerInfo.SIGNATURE, localSign);
            reqMap = new HashMap<>(sortMap);
        } catch (Exception e) {
            log.error("{}生成签名异常:{}", LOGGER_MSG, e);
        }
        return reqMap;
    }
    
}
