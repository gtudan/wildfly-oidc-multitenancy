package com.example.oidcdemo;

import org.wildfly.security.http.oidc.*;

import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.logging.Logger;

public class OidcConfigResolver implements OidcClientConfigurationResolver {

    private final Logger log = Logger.getLogger(getClass().getName());

    public OidcConfigResolver() {
        log.info("Config resolver initializing");
    }

    @Override
    public OidcClientConfiguration resolve(OidcHttpFacade.Request request) {
        log.info("Resolving config for request " + request.getURI());

        ServletContext servletContext = CDI.current().select(ServletContext.class).get();
        OidcJsonConfiguration oidcJsonConfiguration = loadOidcConfig(servletContext);

        // load your realm config in here and add it to the json config
        oidcJsonConfiguration.setRealm("myRealm");

        return OidcClientConfigurationBuilder.build(oidcJsonConfiguration);
    }

    /**
     * @return the adapter config from the {@code keycloak.json}
     */
    private OidcJsonConfiguration loadOidcConfig(ServletContext context) {
        final InputStream oidcConfig = context.getResourceAsStream("/WEB-INF/oidc.json");
        if (oidcConfig == null) {
            throw new IllegalStateException("OIDC is not configured - /WEB-INF/oidc.json is missing");
        }
        return OidcClientConfigurationBuilder.loadOidcJsonConfiguration(oidcConfig);
    }
}
