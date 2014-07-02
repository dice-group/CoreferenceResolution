/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.aksw.simba.decomposition;

import org.la4j.matrix.Matrix;

/**
 *
 * @author ngonga
 */
public class LUDecomposition extends SimpleMatrixDecomposition{
    
    @Override
    public void init(int r)
    {
        Matrix Ma = M.copy();
    }
}
