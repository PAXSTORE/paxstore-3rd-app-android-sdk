package com.pax.market.android.app.sdk.lint;

import com.android.annotations.NonNull;
import com.android.tools.lint.client.api.IssueRegistry;
import com.android.tools.lint.client.api.Vendor;
import com.android.tools.lint.detector.api.Issue;

import java.util.Collections;
import java.util.List;

/**
 * Issue registry of the PAXSTORE 3rd App Android SDK.
 * Registered via META-INF/services/com.android.tools.lint.client.api.IssueRegistry
 * and embedded into the published AAR through the {@code lintPublish} configuration.
 */
public class PaxStoreIssueRegistry extends IssueRegistry {

    @NonNull
    @Override
    public List<Issue> getIssues() {
        return Collections.singletonList(RpcServiceRegistrationDetector.ISSUE);
    }

    @Override
    public Vendor getVendor() {
        return new Vendor(
                "PAXSTORE",
                "com.whatspos.sdk:paxstore-3rd-app-android-sdk",
                "https://github.com/PAXSTORE/paxstore-3rd-app-android-sdk/issues",
                "paxstore-support@paxsz.com");
    }
}
