package ru.tdd.author.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;


@Suite
@SuiteDisplayName("Unit тесты сервисов")
@SelectPackages({"ru.tdd.author.unit.services"})
public class UnitServiceTest {
}

