package ru.tdd.author.suites;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Проверка репозиториев")
@SelectPackages({"ru.tdd.author.integrations.repositories"})
public class RepositoriesSuite {
}
