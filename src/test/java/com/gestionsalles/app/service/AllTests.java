package com.gestionsalles.app.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ProfServiceTest.class,
        SalleServiceTest.class,
        OccuperServiceTest.class
})
public class AllTests {
}
