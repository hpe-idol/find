package com.autonomy.abc.config;

import com.autonomy.abc.selenium.application.ApplicationType;
import com.autonomy.abc.selenium.application.HSOApplication;
import com.autonomy.abc.selenium.navigation.HSODElementFactory;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Ignore
@RunWith(Parameterized.class)
public abstract class HostedTestBase extends ABCTestBase {
    public HostedTestBase(TestConfig config) {
        super(config);
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> parameters() throws IOException {
        final Collection<ApplicationType> applicationTypes = Collections.singletonList(ApplicationType.HOSTED);
        return parameters(applicationTypes);
    }


    @Override
    public HSODElementFactory getElementFactory() {
        return (HSODElementFactory) super.getElementFactory();
    }

    @Override
    public HSOApplication getApplication() {
        return (HSOApplication) super.getApplication();
    }
}
