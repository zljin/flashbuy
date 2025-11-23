package com.zljin.flashbuy.controller;

import jakarta.annotation.Resource;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringResourceAccessor;
import liquibase.resource.ResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/function")
public class FunctionController implements ResourceLoaderAware {

    @Resource
    private LiquibaseProperties liquibaseProperties;

    protected ResourceLoader resourceLoader;

    @Resource
    private DataSource dataSource;

    @PostMapping("/{version}/rollback-liquibase")
    public ResponseEntity<String> rollback(@PathVariable("version") String version) throws LiquibaseException, SQLException {
        String changeLogStr = liquibaseProperties.getChangeLog();
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(dataSource.getConnection()));
        ScopeLiquibase scopeLiquibase = new ScopeLiquibase(changeLogStr, new SpringResourceAccessor(resourceLoader), database);
        log.info("rollback liquibase now...");
        scopeLiquibase.rollback(version,"rollback");
        return ResponseEntity.ok("rollback success");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private static class ScopeLiquibase extends Liquibase {
        public ScopeLiquibase(String changeLogFile, ResourceAccessor resourceAccessor, Database conn) {
            super(changeLogFile, resourceAccessor, conn);
        }

        @Override
        public void rollback(String tagToRollBackTo, String contexts) throws LiquibaseException {
            Map<String, Object> scopeMap = new HashMap<>();
            scopeMap.put(Scope.Attr.database.name(), getDatabase());
            scopeMap.put(Scope.Attr.resourceAccessor.name(), getResourceAccessor());
            try {
                Scope.child(scopeMap, () -> super.rollback(tagToRollBackTo, contexts));
            } catch (Exception e) {
                throw new LiquibaseException(e);
            }
        }
    }
}
