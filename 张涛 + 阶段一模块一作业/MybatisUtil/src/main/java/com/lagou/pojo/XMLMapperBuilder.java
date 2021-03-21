package com.lagou.pojo;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream inputStream) throws DocumentException {
        Document document = new SAXReader().read(inputStream);
        Element rootElement = document.getRootElement();

        String namespace = rootElement.attributeValue("namespace");

        List<String> stringList = Arrays.asList("select", "delete", "insert", "update");
        for (String elementList : stringList) {
            List<Element> nodes = rootElement.selectNodes(elementList);
            for (Element element : nodes) {
                String id = element.attributeValue("id");
                String resultType = element.attributeValue("resultType");
                String parameterType = element.attributeValue("parameterType");
                String sqlText = element.getTextTrim();
                MappedStatement mappedStatement = new MappedStatement();
                mappedStatement.setId(id);
                mappedStatement.setParameterType(parameterType);
                mappedStatement.setResultType(resultType);
                mappedStatement.setSql(sqlText);
                System.out.println("mappedStatement: " + mappedStatement);
                String key = namespace + "." + id;
                configuration.getMappedStatementMap().put(key, mappedStatement);
            }
        }

    }


}
