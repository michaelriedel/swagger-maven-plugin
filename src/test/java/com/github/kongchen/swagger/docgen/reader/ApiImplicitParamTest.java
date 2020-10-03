package com.github.kongchen.swagger.docgen.reader;

import com.github.kongchen.swagger.docgen.GenerateException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.SerializableParameter;
import org.apache.maven.plugin.logging.Log;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ApiImplicitParamTest {
    @Mock
    private Log log;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ignoreClassIfNoApiAnnotation() throws GenerateException {
        // given
        Swagger swagger = new Swagger();
        SpringMvcApiReader reader = new SpringMvcApiReader(swagger, log);
        Set<Class<?>> classes = Collections.singleton(ImplicitClassParamApi.class);
        // when
        Swagger resourceMap = reader.read(classes);
        // then
        assertThat(resourceMap.getPaths().entrySet(), is(iterableWithSize(1)));
        Path path = resourceMap.getPaths().get("/some/path/test");
        assertThat(path, is(notNullValue()));
        List<Parameter> parameters = path.getGet().getParameters();
        assertThat(parameters, is(iterableWithSize(1)));
        SerializableParameter parameter = (SerializableParameter) parameters.get(0);
        assertThat(parameter.getName(), is("param"));
        assertThat(parameter.getType(), is("string"));
        assertThat(parameter.getIn(), is("header"));
    }

    @ApiImplicitParams(@ApiImplicitParam(name = "param", dataType = "string", paramType = "header"))
    @RequestMapping("/some/path")
    private interface ImplicitClassParamApi {
        @GetMapping("/test")
        void test(int i);
    }
}
