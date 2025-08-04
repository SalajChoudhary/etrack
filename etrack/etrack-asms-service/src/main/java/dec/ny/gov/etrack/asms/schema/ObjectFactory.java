
package dec.ny.gov.etrack.asms.schema;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dec.ny.gov.etrack.asms.schema package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dec.ny.gov.etrack.asms.schema
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SchemaSecurityGroups }
     * 
     */
    public SchemaSecurityGroups createSchemaSecurityGroups() {
        return new SchemaSecurityGroups();
    }

    /**
     * Create an instance of {@link SchemaUser }
     * 
     */
    public SchemaUser createSchemaUser() {
        return new SchemaUser();
    }

    /**
     * Create an instance of {@link SchemaSecurityObjects }
     * 
     */
    public SchemaSecurityObjects createSchemaSecurityObjects() {
        return new SchemaSecurityObjects();
    }

    /**
     * Create an instance of {@link SchemaSecurityObjects.Groups }
     * 
     */
    public SchemaSecurityObjects.Groups createSchemaSecurityObjectsGroups() {
        return new SchemaSecurityObjects.Groups();
    }

    /**
     * Create an instance of {@link SchemaSecurityObjects.Groups.Group }
     * 
     */
    public SchemaSecurityObjects.Groups.Group createSchemaSecurityObjectsGroupsGroup() {
        return new SchemaSecurityObjects.Groups.Group();
    }

    /**
     * Create an instance of {@link SchemaSecurityGroups.Groups }
     * 
     */
    public SchemaSecurityGroups.Groups createSchemaSecurityGroupsGroups() {
        return new SchemaSecurityGroups.Groups();
    }

    /**
     * Create an instance of {@link SchemaUser.Name }
     * 
     */
    public SchemaUser.Name createSchemaUserName() {
        return new SchemaUser.Name();
    }

    /**
     * Create an instance of {@link SchemaUser.BusinessAddress }
     * 
     */
    public SchemaUser.BusinessAddress createSchemaUserBusinessAddress() {
        return new SchemaUser.BusinessAddress();
    }

    /**
     * Create an instance of {@link SchemaSecurityObjects.Groups.Group.SchemaSecurityObject }
     * 
     */
    public SchemaSecurityObjects.Groups.Group.SchemaSecurityObject createSchemaSecurityObjectsGroupsGroupSchemaSecurityObject() {
        return new SchemaSecurityObjects.Groups.Group.SchemaSecurityObject();
    }

}
