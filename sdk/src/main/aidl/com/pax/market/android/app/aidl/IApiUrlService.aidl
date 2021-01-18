// IApiUrlService.aidl
package com.pax.market.android.app.aidl;

// Declare any non-default types here with import statements
import com.pax.market.android.app.sdk.dto.StoreProxyInfo;
import com.pax.market.android.app.sdk.dto.DcUrlInfo;


interface IApiUrlService {
    String getApiUrl();
    StoreProxyInfo getStoreProxyInfo();
    DcUrlInfo getDcUrlInfo();
    String getSn();
}
