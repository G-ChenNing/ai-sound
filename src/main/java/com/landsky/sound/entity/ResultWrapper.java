package com.landsky.sound.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * 操作结果返回包装类
 *
 * @author tangh
 */
public class ResultWrapper implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ResultWrapper.class);

    private static final long serialVersionUID = 1L;

    public static final String SUFFIX_SUCCESS = "成功";
    public static final String SUFFIX_FAILURE = "失败";

    private static final Object[] EMPTY_ARRAY = new Object[]{};

    /**
     * 操作是否成功
     */
    private boolean success;

    /**
     * 操作信息
     */
    private String message;

    /**
     * 附帶数据
     */
    private Object object;

    private boolean feignHystrix;

    public ResultWrapper() {

    }

    public ResultWrapper(boolean success) {
        this.success = success;
    }

    public static ResultWrapper debug() {
        return success(false).message("调试中");
    }

    public static ResultWrapper success(boolean success) {
        return new ResultWrapper(success);
    }

    public static ResultWrapper success() {
        return success(true);
    }

    public static ResultWrapper failure() {
        return success(false);
    }

    /**
     * @return feign熔断，说明feign调用失败
     */
    public static ResultWrapper feignHystrix() {
        ResultWrapper fh = success(false);
        fh.setFeignHystrix(true);
        fh.setMessage("远程服务调用异常");
        return fh;
    }


    public <T extends ResultWrapper> T message(String message) {
        this.message = message;
        return (T) this;
    }

    /**
     * 传入的信息根据是否成功附带后缀
     *
     * @param message
     * @return
     */
    public <T extends ResultWrapper> T submessage(String message) {
        if (success) {
            this.message = message + SUFFIX_SUCCESS;
        } else {
            this.message = message + SUFFIX_FAILURE;
        }
        return (T) this;
    }

    public <T extends ResultWrapper> T messageAdd() {
        return submessage("添加");
    }

    public <T extends ResultWrapper> T messageEdit() {
        return submessage("编辑");
    }

    public <T extends ResultWrapper> T messageDelete() {
        return submessage("删除");
    }

    public <T extends ResultWrapper> T messageQuery() {
        return submessage("查询");
    }

    public <T extends ResultWrapper> T messageExist(String msg) {
        return message(msg + "已存在");
    }

    public <T extends ResultWrapper> T messageNotExist(String msg) {
        return message(msg + "不存在");
    }

    public <T extends ResultWrapper> T messageNotEmpty(String msg) {
        return message(msg + "不能为空");
    }

    public <T extends ResultWrapper> T object(Object object) {
        this.object = object;
        return (T) this;
    }

    public <T extends ResultWrapper> T object(Object... object) {
        this.object = object;
        return (T) this;
    }

    public <T extends ResultWrapper> T objectIf(boolean ifObject, Object object) {
        if (ifObject) {
            return object(object);
        }
        return (T) this;
    }

    public <T extends ResultWrapper> T objectIf(boolean ifObject, Object... object) {
        if (ifObject) {
            return object(object);
        }
        return (T) this;
    }

    public <T extends ResultWrapper> T objectEmptyArray() {
        this.object = EMPTY_ARRAY;
        return (T) this;
    }

    public <T extends ResultWrapper> T objectEmptyList() {
        this.object = Collections.emptyList();
        return (T) this;
    }


    public <T extends ResultWrapper> T throwable(Throwable e) {
        this.message = e.getMessage();
        this.object = e;
        return (T) this;
    }

    public <T extends ResultWrapper> T transaction() {
        if (success) {
            return (T) this;
        } else {
            throw new RuntimeException(message);
        }
    }

    public <T extends ResultWrapper> T cache(boolean success, CacheHandler ch) {
        if (this.success == success && ch != null) {
            ch.cache();
        }
        return (T) this;
    }

    public <T extends ResultWrapper> T cacheOnSuccess(CacheHandler ch) {
        return cache(true, ch);
    }

    public <T extends ResultWrapper> T cacheOnFailure(CacheHandler ch) {
        return cache(false, ch);
    }


    public static <T> ResultWrapper list(Collection<T> collection) {
        return ResultWrapper.success().messageQuery().object(collection);
    }

    public static <T> ResultWrapper page(IPage<T> page) {
        return ResultWrapper.success().messageQuery().object(page);
    }


    public boolean isSuccess() {
        return success;
    }


    public String getMessage() {
        return message;
    }


    public Object getObject() {
        return object;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isFeignHystrix() {
        return feignHystrix;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public <T> T getObjectCastSafety() {
        return (T) object;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public <T> T getObjectCastUnsafe() {
        return getObjectCastUnsafeDefault(null);
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public <T> T getObjectCastUnsafeDefault(T defaultValue) {
        try {
            if (object == null) {
                return defaultValue;
            }
            return (T) object;
        } catch (Exception e) {
            logger.error("getObjectCastUnsafeDefault", e);
            return defaultValue;
        }
    }



    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setFeignHystrix(boolean feignHystrix) {
        this.feignHystrix = feignHystrix;
    }

    @Override
    public String toString() {
        return "ResultWrapper [success=" + success + ", message=" + message + ", object=" + object + "]";
    }

    @FunctionalInterface
    public interface CacheHandler {
        void cache();
    }
}
