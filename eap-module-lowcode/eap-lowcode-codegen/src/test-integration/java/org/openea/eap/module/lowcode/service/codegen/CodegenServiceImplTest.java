package org.openea.eap.module.lowcode.service.codegen;

import org.openea.eap.framework.test.core.ut.BaseDbUnitTest;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

class CodegenServiceImplTest extends BaseDbUnitTest {

    @Resource
    private CodegenServiceImpl codegenService;

    @Test
    public void tetCreateCodegenTable() {
        codegenService.createCodegen(0L, "infra_test_demo");
//        infraCodegenService.createCodegenTable("infra_codegen_table");
//        infraCodegenService.createCodegen("infra_codegen_column");
    }

}
