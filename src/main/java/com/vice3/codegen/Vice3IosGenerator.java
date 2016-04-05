package com.vice3.codegen;

import io.swagger.codegen.*;
import io.swagger.models.properties.*;

import java.util.*;
import java.io.File;

import org.apache.commons.lang.StringUtils;

public class Vice3IosGenerator extends DefaultCodegen implements CodegenConfig {
    protected Set<String> foundationClasses = new HashSet<String>();
    protected String modelsFolder = "Models";
    protected String apiFolder = "Networking";
    protected String classPrefix = "Mtl";
    protected String[] specialWords = {"new", "copy"};

    public Vice3IosGenerator() {
        super();
        
        outputFolder = "generated-code" + File.separator + "objc";
        modelTemplateFiles.put("model-header.mustache", ".h");
        modelTemplateFiles.put("model-body.mustache", ".m");
        apiTemplateFiles.put("api-header.mustache", ".h");
        apiTemplateFiles.put("api-body.mustache", ".m");
        embeddedTemplateDir = templateDir = "Vice3-iOS";

        defaultIncludes.clear();
        defaultIncludes.add("bool");
        defaultIncludes.add("BOOL");
        defaultIncludes.add("int");
        defaultIncludes.add("NSURL");
        defaultIncludes.add("NSString");
        defaultIncludes.add("NSObject");
        defaultIncludes.add("NSArray");
        defaultIncludes.add("NSNumber");
        defaultIncludes.add("NSDate");
        defaultIncludes.add("NSDictionary");
        defaultIncludes.add("NSMutableArray");
        defaultIncludes.add("NSMutableDictionary");
        
        languageSpecificPrimitives.clear();
        languageSpecificPrimitives.add("NSNumber");
        languageSpecificPrimitives.add("NSString");
        languageSpecificPrimitives.add("NSObject");
        languageSpecificPrimitives.add("NSDate");
        languageSpecificPrimitives.add("NSURL");
        languageSpecificPrimitives.add("bool");
        languageSpecificPrimitives.add("BOOL");

        typeMapping.clear();
        typeMapping.put("enum", "NSString");
        typeMapping.put("date", "NSDate");
        typeMapping.put("DateTime", "NSDate");
        typeMapping.put("boolean", "NSNumber");
        typeMapping.put("string", "NSString");
        typeMapping.put("integer", "NSNumber");
        typeMapping.put("int", "NSNumber");
        typeMapping.put("float", "NSNumber");
        typeMapping.put("long", "NSNumber");
        typeMapping.put("double", "NSNumber");
        typeMapping.put("array", "NSArray");
        typeMapping.put("map", "NSDictionary");
        typeMapping.put("number", "NSNumber");
        typeMapping.put("List", "NSArray");
        typeMapping.put("object", "NSObject");
        typeMapping.put("file", "NSURL");


        // ref: http://www.tutorialspoint.com/objective_c/objective_c_basic_syntax.htm
        reservedWords = new HashSet<String>(
                Arrays.asList(
                    // local variable names in API methods (endpoints)
                    "resourcePath", "pathParams", "queryParams", "headerParams",
                    "responseContentType", "requestContentType", "authSettings",
                    "formParams", "files", "bodyParam",
                    // objc reserved words
                    "auto", "else", "long", "switch",
                    "break", "enum", "register", "typedef",
                    "case", "extern", "return", "union",
                    "char", "float", "short", "unsigned",
                    "const", "for", "signed", "void",
                    "continue", "goto", "sizeof", "volatile",
                    "default", "if", "id", "static", "while",
                    "do", "int", "struct", "_Packed",
                    "double", "protocol", "interface", "implementation",
                    "NSObject", "NSInteger", "NSNumber", "CGFloat",
                    "property", "nonatomic", "retain", "strong",
                    "weak", "unsafe_unretained", "readwrite", "readonly",
                    "description"
                ));

        importMapping = new HashMap<String, String>();

        foundationClasses = new HashSet<String>(
                Arrays.asList(
                        "NSNumber",
                        "NSObject",
                        "NSString",
                        "NSDate",
                        "NSURL",
                        "NSDictionary")
        );

        instantiationTypes.put("array", "NSMutableArray");
        instantiationTypes.put("map", "NSMutableDictionary");
    }

    @Override
    public CodegenType getTag() {
        return CodegenType.CLIENT;
    }

    @Override
    public String getName() {
        return "Vice3-iOS";
    }

    @Override
    public String getHelp() {
        return "Generates an Objective-C client library.";
    }

    @Override
    public void processOpts() {
        super.processOpts();

        modelPackage = modelsFolder;
        apiPackage = apiFolder;

        supportingFiles.add(new SupportingFile("ApiRequest.h", apiFolder, "ApiRequest.h"));
        supportingFiles.add(new SupportingFile("ApiRequest.m", apiFolder, "ApiRequest.m"));
        supportingFiles.add(new SupportingFile("NSString+Path.h", apiFolder, "NSString+Path.h"));
        supportingFiles.add(new SupportingFile("NSString+Path.m", apiFolder, "NSString+Path.m"));
        supportingFiles.add(new SupportingFile("BaseObject.h", modelsFolder, "BaseObject.h"));
        supportingFiles.add(new SupportingFile("BaseObject.m", modelsFolder, "BaseObject.m"));
        supportingFiles.add(new SupportingFile("BaseApi.h", apiFolder, "BaseApi.h"));
        supportingFiles.add(new SupportingFile("BaseApi.m", apiFolder, "BaseApi.m"));
    }

    @Override
    public String toInstantiationType(Property p) {
        if (p instanceof MapProperty) {
            MapProperty ap = (MapProperty) p;
            String inner = getSwaggerType(ap.getAdditionalProperties());
            return instantiationTypes.get("map");
        } else if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            String inner = getSwaggerType(ap.getItems());
            return instantiationTypes.get("array");
        } else {
            return null;
        }
    }

    @Override
    public String getTypeDeclaration(String name) {
        if (languageSpecificPrimitives.contains(name) && !foundationClasses.contains(name)) {
            return name;
        } else {
            return name + "*";
        }
    }

    @Override
    public String getSwaggerType(Property p) {
        String swaggerType = super.getSwaggerType(p);
        String type = null;
        if (typeMapping.containsKey(swaggerType)) {
            type = typeMapping.get(swaggerType);
            if (languageSpecificPrimitives.contains(type) && !foundationClasses.contains(type)) {
                return toModelName(type);
            }
        } else {
            type = swaggerType;
        }
        return toModelName(type);
    }

    @Override
    public String getTypeDeclaration(Property p) {
        if (p instanceof ArrayProperty) {
            ArrayProperty ap = (ArrayProperty) p;
            Property inner = ap.getItems();
            String innerType = getSwaggerType(inner);

            String innerTypeDeclaration = getTypeDeclaration(inner);

            if (innerTypeDeclaration.endsWith("*")) {
                innerTypeDeclaration = innerTypeDeclaration.substring(0, innerTypeDeclaration.length() - 1);
            }

            // In this codition, type of property p is array of primitive,
            // return container type with pointer, e.g. `NSArray<NSString*>*`
            if (languageSpecificPrimitives.contains(innerType)) {
                return getSwaggerType(p) + "<" + innerTypeDeclaration + "*>*";
            }
            // In this codition, type of property p is array of model,
            // return container type combine inner type with pointer, e.g. `NSArray<SWGTag>*'
            else {
                return getSwaggerType(p) + "<" + innerTypeDeclaration + "*>*";
            }
        } else if (p instanceof MapProperty) {
            MapProperty mp = (MapProperty) p;
            Property inner = mp.getAdditionalProperties();

            String innerTypeDeclaration = getTypeDeclaration(inner);

            if (innerTypeDeclaration.endsWith("*")) {
                innerTypeDeclaration = innerTypeDeclaration.substring(0, innerTypeDeclaration.length() - 1);
            }
            return getSwaggerType(p) + "<NSString*, " + innerTypeDeclaration + "*>*";
        } else {
            String swaggerType = getSwaggerType(p);

            // In this codition, type of p is objective-c primitive type, e.g. `NSSNumber',
            // return type of p with pointer, e.g. `NSNumber*'
            if (languageSpecificPrimitives.contains(swaggerType) &&
                    foundationClasses.contains(swaggerType)) {
                return swaggerType + "*";
            }
            // In this codition, type of p is c primitive type, e.g. `bool',
            // return type of p, e.g. `bool'
            else if (languageSpecificPrimitives.contains(swaggerType)) {
                return swaggerType;
            }
            // In this codition, type of p is objective-c object type, e.g. `SWGPet',
            // return type of p with pointer, e.g. `SWGPet*'
            else {
                return swaggerType + "*";
            }
        }
    }

    @Override
    public String toModelName(String type) {
        type = type.replaceAll("[^0-9a-zA-Z_]", "_");
        type = type.replace("Model","");

        // language build-in classes
        if (typeMapping.keySet().contains(type) ||
                foundationClasses.contains(type) ||
                importMapping.values().contains(type) ||
                defaultIncludes.contains(type) ||
                languageSpecificPrimitives.contains(type)) {
            return camelize(type).replace("$","");
        }
        // custom classes
        else {
            return classPrefix + camelize(type).replace("$","");
        }
    }

    @Override
    public String toModelFilename(String name) {
        // should be the same as the model name
        return toModelName(name);
    }

    @Override
    protected void setNonArrayMapProperty(CodegenProperty property, String type) {
        super.setNonArrayMapProperty(property, type);
        if ("NSDictionary".equals(type)) {
            property.setter = "initWithDictionary";
        } else {
            property.setter = "initWithValues";
        }
    }

    @Override
    public String toModelImport(String name) {
        return name;
    }

    @Override
    public String apiFileFolder() {
        return outputFolder + File.separatorChar + apiPackage();
    }

    @Override
    public String modelFileFolder() {
        return outputFolder + File.separatorChar + modelPackage();
    }

    @Override
    public String toApiName(String name) {
        return camelize(name) + "Api";
    }

    @Override
    public String toApiFilename(String name) {
        return camelize(name) + "Api";
    }

    @Override
    public String toVarName(String name) {
        // sanitize name
        name = sanitizeName(name);

        // if it's all upper case, do noting
        if (name.matches("^[A-Z_]$")) {
            return name;
        }

        // if name starting with special word, escape with '_'
        for(int i =0; i < specialWords.length; i++) {
            if (name.matches("(?i:^" + specialWords[i] + ".*)"))
                name = escapeSpecialWord(name);
        }

        // camelize (lower first character) the variable name
        // e.g. `pet_id` to `petId`
        name = camelize(name, true);

        // for reserved word or word starting with number, prepend `_`
        if (reservedWords.contains(name) || name.matches("^\\d.*")) {
            name = escapeReservedWord(name);
        }


        return name;
    }

    @Override
    public String toParamName(String name) {
        // should be the same as variable name
        return toVarName(name);
    }

    @Override
    public String escapeReservedWord(String name) {
        if (name.equals("id"))
            return "uid";
        else if (name.equals("description"))
            return "desc";
        else
            return "_" + name;
    }

    public String escapeSpecialWord(String name) {
        return "v_" + name;
    }

    @Override
    public String toOperationId(String operationId) {
        // throw exception if method name is empty
        if (StringUtils.isEmpty(operationId)) {
            throw new RuntimeException("Empty method name (operationId) not allowed");
        }

        // method name cannot use reserved keyword, e.g. return
        if (reservedWords.contains(operationId)) {
            throw new RuntimeException(operationId + " (reserved word) cannot be used as method name");
        }

        operationId = operationId.replace("Format", "");
        operationId = camelize(operationId);
        operationId = operationId.replace("GET", "get");
        operationId = operationId.replace("POST", "post");
        operationId = operationId.replace("PUT", "put");
        operationId = operationId.replace("DELETE", "delete");
        operationId = operationId.replace("PATCH", "patch");

        return operationId;
    }

    public void setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
    }
    
    /**
     * Return the default value of the property
     *
     * @param p Swagger property object
     * @return string presentation of the default value of the property
     */
    @Override
    public String toDefaultValue(Property p) {
        if (p instanceof StringProperty) {
            StringProperty dp = (StringProperty) p;
            if (dp.getDefault() != null) {
                return "@\"" + dp.getDefault().toString() + "\"";
            }
        } else if (p instanceof BooleanProperty) {
            BooleanProperty dp = (BooleanProperty) p;
            if (dp.getDefault() != null) {
                if (dp.getDefault().toString().equalsIgnoreCase("false"))
                    return "@0";
                else
                    return "@1";
            }
        } else if (p instanceof DateProperty) {
            // TODO
        } else if (p instanceof DateTimeProperty) {
            // TODO
        } else if (p instanceof DoubleProperty) {
            DoubleProperty dp = (DoubleProperty) p;
            if (dp.getDefault() != null) {
                return "@" + dp.getDefault().toString();
            }
        } else if (p instanceof FloatProperty) {
            FloatProperty dp = (FloatProperty) p;
            if (dp.getDefault() != null) {
                return "@" + dp.getDefault().toString();
            }
        } else if (p instanceof IntegerProperty) {
            IntegerProperty dp = (IntegerProperty) p;
            if (dp.getDefault() != null) {
                return "@" + dp.getDefault().toString();
            }
        } else if (p instanceof LongProperty) {
            LongProperty dp = (LongProperty) p;
            if (dp.getDefault() != null) {
                return "@" + dp.getDefault().toString();
            }
        }

        return null;
    }

}