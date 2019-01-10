// IApiUrlService.aidl
package com.pax.market.android.app.aidl;

// Declare any non-default types here with import statements

import com.pax.market.android.app.sdk.dto.TerminalInfo;

interface IRemoteSdkService {
    TerminalInfo getBaseTerminalInfo();
}
