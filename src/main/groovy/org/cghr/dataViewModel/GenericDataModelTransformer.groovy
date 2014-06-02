package org.cghr.dataViewModel

interface GenericDataModelTransformer {

    Map getModel(String sql, List params)
}
