package org.projectodd.yaml.schema.types;

import java.util.Map;

import org.projectodd.yaml.SchemaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBaseType {

    private static Logger log = LoggerFactory.getLogger( AbstractBaseType.class );

    private static final String OPTIONAL_PREFIX = "-";

    private static final String REQUIRED_PREFIX = "+";

    private String name;

    private boolean required = true;

    abstract AbstractBaseType build(Object yamlData) throws SchemaException;

    public String getName() {
        return name;
    }

    @SuppressWarnings("unchecked")
    AbstractBaseType initialize(String name, Object yamlData) throws SchemaException {
        TypeFactory tf = TypeFactory.instance();
        String fieldName = name;
        if (fieldName.startsWith( REQUIRED_PREFIX )) {
            fieldName = fieldName.substring( 1 );
            this.required = true;
        }
        else if (fieldName.startsWith( OPTIONAL_PREFIX )) {
            fieldName = fieldName.substring( 1 );
            this.required = false;
        }
        this.name = fieldName;

        validateRequirements( tf.getRequirements( "build", this.getClass() ), yamlData );
        validateRequirements( tf.getRequirements( this.getClass() ), yamlData );

        if (yamlData instanceof Map) {
            Map<String, Object> yamlMap = (Map<String, Object>) yamlData;
            if (yamlMap.containsKey( "required" )) {
                required = Boolean.valueOf( yamlMap.get( "required" ).toString() );
            }  
        }
        log.debug( "initialized " + (required ? "required" : "optional") + " field " + this.name );
        return this;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void validate(Object yamlData) throws SchemaException {
        log.debug( "Validating type " + this.getClass() + " using value " + yamlData );
        if (yamlData == null && required) {
            throw new SchemaException( "Field " + name + " was required but is not present." );
        }

        TypeFactory tf = TypeFactory.instance();
        validateRequirements( tf.getRequirements( "validateType", this.getClass() ), yamlData );
        validateRequirements( tf.getRequirements( this.getClass() ), yamlData );

        validateType( yamlData );
    }

    private void validateRequirements(Class<?>[] reqs, Object yamlData) throws SchemaException {
        if (reqs != null) {
            boolean matched = false; 
            for (int i = 0; matched == false && i < reqs.length; i++) {
                matched = (yamlData == null || reqs[i].isInstance( yamlData ));
            }
            if (!matched) {
                throw new SchemaException( "Schema for field " + name + " does not accept "
                        + yamlData
                        + (yamlData != null ? " of type " + yamlData.getClass() : "")
                        + " as input for "
                        + "schema type "
                        + TypeFactory.instance().getSchemaTypeId( this.getClass() ) );
            }
        }
    }

    public abstract void validateType(Object value) throws SchemaException;

}