package com.pax.market.android.app.sdk.lint;

import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Context;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Location;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.SourceCodeScanner;
import com.android.tools.lint.detector.api.XmlContext;
import com.android.tools.lint.detector.api.XmlScanner;
import com.android.tools.lint.model.LintModelVariant;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.uast.UCallExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Since PAXSTORE Android SDK v11.1.0 the {@code RPCService} declaration is no longer
 * merged into the host app's manifest automatically. Apps that keep calling the
 * update inquirer APIs ({@code StoreSdk.initInquirer(...)} / {@code initInquirerOnly(...)} /
 * {@code RPCService.initInquirer(...)}) must declare the service in their own
 * AndroidManifest.xml, otherwise the inquirer silently stops working.
 *
 * <p>This detector reports a fatal error when such an API call is found in the app
 * sources while the manifest does not declare the service, so old customers get a
 * build-time error (via {@code lintVitalRelease} or IDE inspection) instead of a
 * silent behavior change after upgrading the SDK.</p>
 */
public class RpcServiceRegistrationDetector extends Detector implements XmlScanner, SourceCodeScanner {

    static final String RPC_SERVICE_CLASS = "com.pax.market.android.app.sdk.RPCService";
    static final String STORE_SDK_CLASS = "com.pax.market.android.app.sdk.StoreSdk";

    private static final String ACTION_SUFFIX = ".ACTION_RPC_SERVICE";
    private static final String ANDROID_URI = "http://schemas.android.com/apk/res/android";
    private static final String TAG_SERVICE = "service";
    private static final String TAG_ACTION = "action";

    public static final Issue ISSUE = Issue.create(
            "PaxStoreRpcServiceNotRegistered",
            "RPCService must be registered in AndroidManifest.xml to use the Update Inquirer",
            "Since PAXSTORE Android SDK v11.1.0 the RPCService declaration is no longer merged into "
                    + "your app's manifest automatically. If you want PAXSTORE client to ask your app "
                    + "before it gets updated, declare the service in your own AndroidManifest.xml:\n"
                    + "\n"
                    + "<service android:name=\"com.pax.market.android.app.sdk.RPCService\"\n"
                    + "    android:foregroundServiceType=\"dataSync\"\n"
                    + "    android:permission=\"com.market.android.app.sdk.INSTALL_INQUIRER\"\n"
                    + "    android:exported=\"true\">\n"
                    + "    <intent-filter>\n"
                    + "        <action android:name=\"${applicationId}.ACTION_RPC_SERVICE\" />\n"
                    + "    </intent-filter>\n"
                    + "</service>\n"
                    + "\n"
                    + "Without this declaration the SDK throws an IllegalStateException at runtime "
                    + "when initInquirer()/initInquirerOnly() is called, so the app crashes on startup "
                    + "instead of being silently upgraded without being asked.\n"
                    + "\n"
                    + "If you do not need the update inquirer anymore, simply remove the "
                    + "initInquirer()/initInquirerOnly() call from your code.\n"
                    + "\n"
                    + "See docs/InstallInquirerIntegration.md and docs/Migrations.md for details.",
            Category.CORRECTNESS,
            10,
            Severity.FATAL,
            new Implementation(RpcServiceRegistrationDetector.class,
                    EnumSet.of(Scope.MANIFEST, Scope.JAVA_FILE)));

    /**An update inquirer API call was found in the analyzed sources*/
    private boolean sawInquirerApiCall;
    /**At least one manifest file was analyzed*/
    private boolean sawManifest;
    /**RPCService with the expected action is declared in an analyzed manifest*/
    private boolean sawRpcServiceDeclaration;
    /**Location of the first inquirer API call, used to report the error*/
    private Location callLocation;

    @Override
    public List<String> getApplicableMethodNames() {
        return Arrays.asList("initInquirer", "initInquirerOnly");
    }

    @Override
    public void visitMethodCall(JavaContext context, UCallExpression node, PsiMethod method) {
        String owner = null;
        if (method != null) {
            PsiClass containingClass = method.getContainingClass();
            if (containingClass != null) {
                owner = containingClass.getQualifiedName();
            }
        }
        if (!RPC_SERVICE_CLASS.equals(owner) && !STORE_SDK_CLASS.equals(owner)) {
            return;
        }
        sawInquirerApiCall = true;
        if (callLocation == null) {
            callLocation = context.getLocation(node);
        }
    }

    @Override
    public void visitDocument(XmlContext context, Document document) {
        sawManifest = true;
        if (containsRpcService(document, context.getProject().getPackage())) {
            sawRpcServiceDeclaration = true;
        }
    }

    @Override
    public void afterCheckProject(Context context) {
        if (!sawInquirerApiCall) {
            return;
        }
        //Only diagnose the app (main) module, library modules are not runnable apps
        if (context.getProject() != context.getMainProject()) {
            return;
        }
        boolean declared = sawRpcServiceDeclaration;
        if (!declared) {
            //The app's own manifest may not contain the service while a wrapper library still
            //merges it in; the merged manifest referenced by the lint model is authoritative
            //when available (never use Project.getMergedManifest() here, it probes legacy
            //intermediate folders and may pick up stale files from previous builds)
            Document merged = getModelMergedManifest(context);
            if (merged != null) {
                declared = containsRpcService(merged, packageName(context));
            } else if (!sawManifest) {
                //No manifest information at all (e.g. single file analysis in the IDE),
                //skip instead of risking a false positive
                return;
            }
        }
        if (declared) {
            return;
        }
        Location location = callLocation != null ? callLocation : Location.create(context.file);
        context.report(ISSUE, location,
                "The Update Inquirer API is called but `com.pax.market.android.app.sdk.RPCService` "
                        + "is not registered in AndroidManifest.xml, the SDK will throw an "
                        + "IllegalStateException at runtime and the app will crash on startup.");
    }

    /**
     * Reads the merged manifest referenced by the lint model (fresh output of the
     * manifest merger task), returns null when it is not available.
     */
    private static Document getModelMergedManifest(Context context) {
        try {
            LintModelVariant variant = context.getMainProject().getBuildVariant();
            if (variant != null) {
                File mergedManifest = variant.getMergedManifest();
                if (mergedManifest != null && mergedManifest.isFile()) {
                    javax.xml.parsers.DocumentBuilderFactory factory =
                            javax.xml.parsers.DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    return factory.newDocumentBuilder().parse(mergedManifest);
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static String packageName(Context context) {
        try {
            return context.getMainProject().getPackage();
        } catch (Throwable ignored) {
            return null;
        }
    }

    static boolean containsRpcService(Document document, String packageName) {
        if (document == null) {
            return false;
        }
        NodeList services = document.getElementsByTagName(TAG_SERVICE);
        for (int i = 0; i < services.getLength(); i++) {
            Element service = (Element) services.item(i);
            String name = service.getAttributeNS(ANDROID_URI, "name");
            if (name == null || !RPC_SERVICE_CLASS.equals(resolveName(name, packageName))) {
                continue;
            }
            NodeList actions = service.getElementsByTagName(TAG_ACTION);
            for (int j = 0; j < actions.getLength(); j++) {
                Element action = (Element) actions.item(j);
                String actionName = action.getAttributeNS(ANDROID_URI, "name");
                if (actionName != null && actionName.endsWith(ACTION_SUFFIX)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String resolveName(String name, String packageName) {
        if (name.startsWith(".")) {
            return packageName + name;
        }
        if (name.indexOf('.') == -1) {
            return packageName + "." + name;
        }
        return name;
    }
}
