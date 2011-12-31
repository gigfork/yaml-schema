package org.projectodd.yaml.schema.types;

import org.projectodd.yaml.Schema;
import org.projectodd.yaml.SchemaException;

@SchemaType("natural")
public class NaturalType extends IntegerType {

    @Override
    @Requires(Integer.class)
    public void validateType(Schema schema, Object value) throws SchemaException {
        if (value == null) {
            throw new SchemaException( "Natural field " + getName() + " cannot be null." );
        }
        else if (((Integer) value) < 1) {
            throw new SchemaException( "Natural field " + getName() + " must have a value >= 1." );
        }
    }

}
