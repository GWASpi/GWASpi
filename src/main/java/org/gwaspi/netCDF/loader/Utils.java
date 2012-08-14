/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gwaspi.netCDF.loader;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.gwaspi.constants.cNetCDF.Defaults.AlleleBytes;
import org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding;
/**
 *
 * @author Fernando Muñiz Fernandez
 * IBE, Institute of Evolutionary Biology (UPF-CSIC)
 * CEXS-UPF-PRBB
 */
public class Utils {

    protected enum Bases
    {
        A, C, T, G;
    }

    public static String[] fixXYMTChrData(String[] values, int chrNb) throws IOException{
        //CHECK FOR PSEUDO-AUTOSOMAL FLAG => XY

        if(values[chrNb].equals("23")){ //23=X
            values[chrNb] = "X"; //2=chr
        }
        if(values[chrNb].equals("24")){ //24=Y
            values[chrNb] = "Y";
        }
        if(values[chrNb].equals("25")){ //25=XY pseudo autosomal
            values[chrNb] = "XY";
        }
        if(values[chrNb].equals("26")){ //26=MT
            values[chrNb] = "MT";
        }
        return values;
    }

    public static Object[] fixPlusAlleles(Object[] values, int strandNb, int allelesNb) throws IOException {

            //FLIP TO (+)STRAND AND PRESERVE ALPHABETICAL ORDER
            String cleanAlleles = "";
            if(values[strandNb].equals(org.gwaspi.constants.cImport.StrandFlags.strandMIN)){ //check if strand is "-"
                switch (Bases.valueOf(values[allelesNb].toString().substring(0, 1))) //inspect 1st allele
                {
                    case A:
                        cleanAlleles+="T";
                        break;
                    case C:
                        cleanAlleles+="G";
                        break;
                    case G:
                        cleanAlleles+="C";
                        break;
                    case T:
                        cleanAlleles+="A";
                        break;
                    default:
                        cleanAlleles+="0";
                }
                switch (Bases.valueOf(values[allelesNb].toString().substring(1, 2))) //inspect 2nd allele
                {
                    case A: //must have been AA, is now T|A
                        cleanAlleles+="T"; //TT
                        break;
                    case C: //was AC or CC, is now T|C or G|C
                        cleanAlleles+="G";
                        break;
                    case G: //was AG, CG or GG, is now T|G, G|G or C|G
                        cleanAlleles+="C";
                        break;
                    case T: //was AT, CT, GT or TT, is now T|T, G|T, C|T or A|T
                        cleanAlleles+="A";
                        break;
                    default:
                        cleanAlleles+="0";
                }
                values[allelesNb] = cleanAlleles;
            }

        return values;
    }

    public static GenotypeEncoding detectGTEncoding(LinkedHashMap lhm){
        org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding gtEcoding = org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding.UNKNOWN;
        
        HashSet allAlleles = new HashSet();
        for (Iterator it = lhm.keySet().iterator(); it.hasNext();) {
            Object key = it.next();
            byte[] values = (byte[]) lhm.get(key);
            allAlleles.add(values[0]);
            allAlleles.add(values[1]);
        }

        if(allAlleles.contains(AlleleBytes.B)){
           gtEcoding = org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding.AB0;
        } else {
            if(allAlleles.contains(AlleleBytes.C) ||
               allAlleles.contains(AlleleBytes.G) ||
               allAlleles.contains(AlleleBytes.T)){
               gtEcoding = org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding.ACGT0;
            } else {
                if(allAlleles.contains(AlleleBytes._3) ||
                   allAlleles.contains(AlleleBytes._4)){
                   gtEcoding = org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding.O1234;
                } else { //ONLY CONTAINS 1,2
                   gtEcoding = org.gwaspi.constants.cNetCDF.Defaults.GenotypeEncoding.O12;
                }
            }
        }
        
        return gtEcoding;
    }


}
