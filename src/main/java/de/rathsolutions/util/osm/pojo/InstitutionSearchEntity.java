package de.rathsolutions.util.osm.pojo;

import javax.naming.OperationNotSupportedException;

public class InstitutionSearchEntity extends AbstractSearchEntity {
    private static final String A_INSTITUTION_SEARCH_ENTITY_CANNOT_HAVE_A = "A InstitutionSearchEntity cannot have a ";

    public InstitutionSearchEntity(String name) {
	this.name = name;
    }

    @Override
    public String getHousenumber() throws OperationNotSupportedException {
	throw new OperationNotSupportedException(A_INSTITUTION_SEARCH_ENTITY_CANNOT_HAVE_A + "housenumber!");
    }

    @Override
    public String getCity() throws OperationNotSupportedException {
	throw new OperationNotSupportedException(A_INSTITUTION_SEARCH_ENTITY_CANNOT_HAVE_A + "name!");
    }

    @Override
    public String getStreet() throws OperationNotSupportedException {
	throw new OperationNotSupportedException(A_INSTITUTION_SEARCH_ENTITY_CANNOT_HAVE_A + "street!");
    }
}
