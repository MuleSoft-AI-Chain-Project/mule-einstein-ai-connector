package com.mulesoft.connector.einsteinai.internal.extension;

import com.mulesoft.connector.einsteinai.internal.config.EinsteinConfiguration;
import com.mulesoft.connector.einsteinai.internal.error.EinsteinErrorType;
import com.mulesoft.connector.einsteinai.internal.proxy.DefaultHttpProxyConfig;
import com.mulesoft.connector.einsteinai.internal.proxy.HttpProxyConfig;
import org.mule.runtime.api.meta.Category;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.SubTypeMapping;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.runtime.extension.api.annotation.error.ErrorTypes;
import org.mule.runtime.extension.api.annotation.license.RequiresEnterpriseLicense;
import org.mule.sdk.api.annotation.JavaVersionSupport;

import static org.mule.sdk.api.meta.JavaVersion.JAVA_17;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_8;
import static org.mule.sdk.api.meta.JavaVersion.JAVA_11;


/**
 * This is the main class of an extension, is the entry point from which configurations, connection providers, operations and
 * sources are going to be declared.
 */
@Xml(prefix = "ms-einstein-ai")
@Extension(name = "Einstein AI", category = Category.SELECT)
@ErrorTypes(EinsteinErrorType.class)
@SubTypeMapping(baseType = HttpProxyConfig.class, subTypes = {DefaultHttpProxyConfig.class})
@Configurations(EinsteinConfiguration.class)
@RequiresEnterpriseLicense(allowEvaluationLicense = true)
@JavaVersionSupport({JAVA_8, JAVA_11, JAVA_17})
public class EinsteinAIConnector {
}
