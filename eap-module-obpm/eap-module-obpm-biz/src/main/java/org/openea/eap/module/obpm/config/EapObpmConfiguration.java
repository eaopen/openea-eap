package org.openea.eap.module.obpm.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;

@Configurable
@ComponentScan(basePackages={"org.openea.eap.module.obpm","org.openbpm"})
public class EapObpmConfiguration {

}
