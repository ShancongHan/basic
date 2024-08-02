package com.example.basic.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author han
 * @date 2024/7/31
 */
@Configuration
public class OkHttp3Config {
    /**
     * X509TrustManager
     *
     * @param
     * @return javax.net.ssl.X509TrustManager
     * @throws
     * @author wliduo[i@dolyw.com]
     * @date 2022/7/14 10:15
     */
    @Bean
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };

    }

    /**
     * SSLSocketFactory
     *
     * @param
     * @return javax.net.ssl.SSLSocketFactory
     * @throws
     * @author wliduo[i@dolyw.com]
     * @date 2022/7/14 10:15
     */
    @Bean
    public SSLSocketFactory sslSocketFactory() {
        try {
            // 信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 连接数量
     *
     * @param
     * @return okhttp3.ConnectionPool
     * @throws
     * @author wliduo[i@dolyw.com]
     * @date 2022/7/14 10:15
     */
    @Bean
    public ConnectionPool pool() {
        return new ConnectionPool(200, 5, TimeUnit.MINUTES);
    }

    /**
     * 超时时间
     *
     * @param
     * @return okhttp3.OkHttpClient
     * @throws
     * @author wliduo[i@dolyw.com]
     * @date 2022/7/14 10:15
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                // 禁用缓存，防止调用失败
                // https://blog.csdn.net/u010248147/article/details/90479276
                .retryOnConnectionFailure(true)
                // 连接池
                .connectionPool(pool())
                .connectTimeout(60L, TimeUnit.SECONDS)
                .readTimeout(120L, TimeUnit.SECONDS)
                .build();
    }

}
