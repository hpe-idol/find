/*
 * Copyright 2017 Hewlett Packard Enterprise Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.find.core.customization;

import com.github.sommeri.less4j.LessCompiler;
import com.github.sommeri.less4j.core.DefaultLessCompiler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StyleCompilerTest {
    private Compiler compiler;
    @Mock
    private DefaultLessCompiler lessCompiler;
    @Mock
    private com.github.sommeri.less4j.LessCompiler.Configuration lessCompilerConfig;
    @Mock
    private LessCompiler.CompilationResult result;

    private HashMap<String, String> extraVariables = new HashMap<>();
    private HashMap<String, String> extraVariablesExpected = new HashMap<>();
    private String output;

    @Before
    public void setUp() throws Exception {
        extraVariables.put("name", "value");
        extraVariablesExpected.put("@name", "value");

        when(result.getCss()).thenReturn("css!");
        when(lessCompiler.compile(any(LessResource.class), eq(lessCompilerConfig))).thenReturn(result);
        compiler = new StyleCompiler(extraVariables, lessCompiler, lessCompilerConfig);
        output = compiler.compile(new InMemoryPath("./less/file.less"));
    }

    @Test
    public void testCompileCallsThroughToCompiler() throws Exception {
        verify(lessCompiler, times(1))
            .compile(any(LessResource.class), eq(lessCompilerConfig));
        assertEquals(output, "css!");
    }

    @Test
    public void testCompilePrependsAtToVariableNames() throws Exception {
        verify(lessCompilerConfig, times(1))
            .addExternalVariables(eq(extraVariablesExpected));
    }
}
